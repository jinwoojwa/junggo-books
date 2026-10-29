#!/usr/bin/env bash
# commit-msg hook의 제목 검사를 확인한다.
set -euo pipefail

cd "$(dirname "$0")/.."
hook=.githooks/commit-msg
tmp="$(mktemp)"
trap 'rm -f "$tmp"' EXIT
fail=0

check() { # check <기대: ok|reject> <제목>
  printf '%s\n\n- 본문\n' "$2" > "$tmp"
  if "$hook" "$tmp" 2>/dev/null; then actual=ok; else actual=reject; fi
  if [[ "$actual" != "$1" ]]; then
    echo "FAIL: 기대=$1 실제=$actual 제목='$2'"
    fail=1
  fi
}

check ok "feat(backend): 책 검색 API 추가"
check ok "chore: 설정 정리"
check ok "docs(adr): ADR-013 추가"
check ok "Merge branch 'main'"
check ok "Revert \"feat: x\""
check reject "책 검색 API 추가"
check reject "feature(backend): 추가"
check reject "feat(backend):설명"
check reject "feat(backend): 책 검색 API 추가."
check reject "Feat(backend): 추가"

[[ $fail -eq 0 ]] && echo "hooks OK"
exit $fail
