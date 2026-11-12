// B-04: injected before the page's own scripts (Page.addScriptToEvaluateOnNewDocument). Compose draws
// through WebGL, which first-contentful-paint does not see, so the frames are taken from the context
// itself: when it is created, and the time of every draw call.
(() => {
  window.__b04 = { ctx: -1, draws: [] };
  const getContext = HTMLCanvasElement.prototype.getContext;
  HTMLCanvasElement.prototype.getContext = function (type, ...rest) {
    const c = getContext.call(this, type, ...rest);
    if (c && window.__b04.ctx < 0) window.__b04.ctx = performance.now();
    if (c && !c.__b04 && (type === "webgl2" || type === "webgl")) {
      c.__b04 = true;
      for (const m of ["drawArrays", "drawElements"]) {
        const f = c[m].bind(c);
        c[m] = (...a) => {
          const d = window.__b04.draws;
          if (d.length < 20000) d.push(performance.now());
          return f(...a);
        };
      }
    }
    return c;
  };
})();
