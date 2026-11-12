// B-04: how long a cold page takes to its first frame and to the board, in headless Chrome over CDP.
//
//   node measurements/wasm/first-frame.mjs <chrome binary> <arm> <url> <profile> <runs> >> runs.tsv
//
// One fresh Chrome per run (its own profile directory, the cache disabled), so every run is a first
// visit. Google Chrome in `--headless=new`, not the headless shell: the shell has no WebGL and the page
// throws before its first frame. Compose draws through WebGL, which first-contentful-paint does not
// see, so first-frame-hook.js times the draw calls. Recorded per run, in ms from the navigation's start:
//   board    — the page's own console line `pyatiletka: board Content`: the snapshot is read and the
//              board's state is built (before the canvas exists: skiko's wasm is still loading);
//   ctx      — the WebGL context is created: Compose is up;
//   first    — the first draw call: the first frame;
//   ready    — the first draw after the last font or image of the kit arrived: the board as it looks;
//   kib      — bytes on the wire until then (transfer sizes, compressed as served), in KiB.
// Profiles: `local` — no throttling; `slow4g` — Lighthouse's mobile constants applied as DevTools
// throttling: 150 ms RTT, 1.6 Mbit/s down, 750 kbit/s up, the CPU slowed 4×.
import { spawn } from "node:child_process";
import { mkdtempSync, readFileSync, rmSync, existsSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";

const [chrome, arm, url, profile, runsArg] = process.argv.slice(2);
const runs = Number(runsArg ?? 5);
const PROFILES = {
  local: { net: null, cpu: 1 },
  slow4g: {
    net: { offline: false, latency: 150, downloadThroughput: (1.6 * 1e6) / 8, uploadThroughput: (750 * 1e3) / 8 },
    cpu: 4,
  },
};
const p = PROFILES[profile];
if (!p) throw new Error(`unknown profile ${profile}`);

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

async function once() {
  const dir = mkdtempSync(join(tmpdir(), "b04-"));
  const proc = spawn(chrome, [
    "--headless=new", "--remote-debugging-port=0", `--user-data-dir=${dir}`,
    "--no-first-run", "--no-default-browser-check", "--window-size=1280,800", "about:blank",
  ], { stdio: "ignore" });
  try {
    const portFile = join(dir, "DevToolsActivePort");
    for (let i = 0; i < 100 && !existsSync(portFile); i++) await sleep(50);
    const port = readFileSync(portFile, "utf8").split("\n")[0];
    const targets = await (await fetch(`http://127.0.0.1:${port}/json/list`)).json();
    const ws = new WebSocket(targets.find((t) => t.type === "page").webSocketDebuggerUrl);
    await new Promise((r) => ws.addEventListener("open", r, { once: true }));
    let id = 0;
    const pending = new Map();
    const listeners = [];
    ws.addEventListener("message", (e) => {
      const m = JSON.parse(e.data);
      if (m.id && pending.has(m.id)) { pending.get(m.id)(m); pending.delete(m.id); }
      else listeners.forEach((l) => l(m));
    });
    const send = (method, params = {}) =>
      new Promise((r) => { const i = ++id; pending.set(i, r); ws.send(JSON.stringify({ id: i, method, params })); });

    await send("Network.enable");
    await send("Network.setCacheDisabled", { cacheDisabled: true });
    await send("Runtime.enable");
    await send("Page.enable");
    await send("Page.addScriptToEvaluateOnNewDocument", { source: readFileSync(new URL("./first-frame-hook.js", import.meta.url), "utf8") });
    if (p.net) await send("Network.emulateNetworkConditions", p.net);
    if (p.cpu > 1) await send("Emulation.setCPUThrottlingRate", { rate: p.cpu });

    const board = new Promise((resolve, reject) => {
      const t = setTimeout(() => reject(new Error("no board within 180 s")), 180_000);
      listeners.push((m) => {
        if (m.method !== "Runtime.consoleAPICalled") return;
        const text = m.params.args.map((a) => a.value ?? "").join(" ");
        if (text.includes("pyatiletka: board Content")) { clearTimeout(t); resolve(); }
        if (text.includes("pyatiletka: board Error")) { clearTimeout(t); reject(new Error(text)); }
      });
    });
    const t0 = Date.now();
    const inflight = new Set();
    listeners.push((m) => {
      if (m.method === "Network.requestWillBeSent") inflight.add(m.params.requestId);
      if (m.method === "Network.loadingFinished" || m.method === "Network.loadingFailed") inflight.delete(m.params.requestId);
    });
    let boardAt = -1;
    listeners.push((m) => {
      if (m.method === "Runtime.consoleAPICalled" && boardAt < 0 &&
        m.params.args.some((a) => String(a.value ?? "").includes("pyatiletka: board Content"))) boardAt = m.params.timestamp;
    });
    await send("Page.navigate", { url });
    await board;
    // Wait until the page is quiet: it has drawn, nothing is in flight, and no new draw for 2 s.
    const probe = `String(window.__b04?.draws.length ?? 0)`;
    let last = "", quietSince = Date.now();
    while (!(inflight.size === 0 && last !== "0" && Date.now() - quietSince >= 2000)) {
      if (Date.now() - t0 > 180_000) throw new Error("never quiet");
      await sleep(250);
      const now = (await send("Runtime.evaluate", { expression: probe, returnByValue: true })).result.result.value;
      if (now !== last || inflight.size > 0) { last = now; quietSince = Date.now(); }
    }
    const res = await send("Runtime.evaluate", {
      expression: `JSON.stringify((() => {
        const kit = performance.getEntriesByType("resource").filter((e) => e.name.includes("/composeResources/"));
        const arrived = kit.reduce((s, e) => Math.max(s, e.responseEnd), 0);
        const draws = window.__b04.draws;
        const ready = draws.find((t) => t >= arrived) ?? -1;
        const wire = performance.getEntriesByType("navigation").concat(performance.getEntriesByType("resource"))
          .filter((e) => e.responseEnd <= ready).reduce((s, e) => s + (e.transferSize || 0), 0);
        return { origin: performance.timeOrigin, ctx: window.__b04.ctx, first: draws[0] ?? -1, ready, kib: wire / 1024, kit: kit.length };
      })())`,
      returnByValue: true,
    });
    const r = JSON.parse(res.result.result.value);
    ws.close();
    return { ...r, board: boardAt - r.origin };
  } finally {
    proc.kill("SIGKILL");
    await sleep(300);
    rmSync(dir, { recursive: true, force: true });
  }
}

for (let i = 1; i <= runs; i++) {
  const r = await once();
  console.log([arm, profile, i, Math.round(r.board), Math.round(r.ctx), Math.round(r.first), Math.round(r.ready), Math.round(r.kib), r.kit].join("\t"));
}
