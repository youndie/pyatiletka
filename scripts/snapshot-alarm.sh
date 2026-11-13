#!/usr/bin/env bash
# B-17: the hourly snapshot's alarm clock. GitHub's schedule on this account runs hours late or not at
# all (research, risk 2), so a cron line on the owner's always-on Linux box dispatches the snapshot
# workflow instead. The run itself stays in GitHub Actions, under its own GITHUB_TOKEN; the box only
# says "now".
#
#   scripts/snapshot-alarm.sh install   copy this script to ~/.local/bin and add the cron line (idempotent)
#   scripts/snapshot-alarm.sh status    the cron line, the token's presence (never its value), the log tail
#   scripts/snapshot-alarm.sh           dispatch once and log the outcome (what cron runs)
#
# The token: a fine-grained one, Actions: read and write on youndie/pyatiletka only, in TOKEN_FILE with
# mode 600. Without the file, gh's own login on the box is used. Nothing here prints it.
set -euo pipefail

REPO=youndie/pyatiletka
WORKFLOW=snapshot.yaml
MINUTE=7
TOKEN_FILE=${PYATILETKA_TOKEN_FILE:-$HOME/.config/pyatiletka/dispatch-token}
LOG=${PYATILETKA_ALARM_LOG:-$HOME/.local/state/pyatiletka/alarm.log}
INSTALLED=$HOME/.local/bin/pyatiletka-snapshot-alarm
MARK="# pyatiletka snapshot alarm (B-17)"

export PATH=/usr/local/bin:/usr/bin:/bin:$PATH

dispatch() {
  mkdir -p "$(dirname "$LOG")"
  if [ -r "$TOKEN_FILE" ]; then
    GH_TOKEN=$(<"$TOKEN_FILE")
    export GH_TOKEN
  fi
  local at out code=0
  at=$(date -u +%Y-%m-%dT%H:%M:%SZ)
  out=$(gh workflow run "$WORKFLOW" -R "$REPO" --ref main 2>&1) || code=$?
  printf '%s exit=%s %s\n' "$at" "$code" "$(printf '%s' "$out" | tr '\n' ' ')" >>"$LOG"
  return "$code"
}

install_alarm() {
  install -D -m 755 "${BASH_SOURCE[0]}" "$INSTALLED"
  local line="$MINUTE * * * * $INSTALLED $MARK"
  { crontab -l 2>/dev/null | grep -vF "$MARK" || true; echo "$line"; } | crontab -
  echo "installed: $INSTALLED"
  status_alarm
}

status_alarm() {
  echo "cron: $(crontab -l 2>/dev/null | grep -F "$MARK" || echo 'none')"
  if [ -r "$TOKEN_FILE" ]; then
    echo "token file: present, mode $(stat -c %a "$TOKEN_FILE")"
  else
    echo "token file: absent ($TOKEN_FILE) - gh's own login is used"
  fi
  echo "log ($LOG):"
  tail -n 5 "$LOG" 2>/dev/null || echo "  empty"
}

case "${1:-}" in
  install) install_alarm ;;
  status) status_alarm ;;
  "") dispatch ;;
  *) echo "usage: $0 [install|status]" >&2; exit 2 ;;
esac
