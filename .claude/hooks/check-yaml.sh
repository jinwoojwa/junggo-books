#!/usr/bin/env bash
# PostToolUse(Edit|Write): application*.yml 을 고친 직후 YAML 문법을 검사한다.
# 값에 ': ' 가 들어가면 기동이 실패하는 문제를 바로 잡는다. (docs/troubleshooting/001)
set -uo pipefail

file=$(jq -r '.tool_input.file_path // empty')
case "$file" in
  */src/main/resources/application*.yml) ;;
  *) exit 0 ;;
esac

if ! err=$(ruby -ryaml -e 'YAML.load_file(ARGV[0])' "$file" 2>&1); then
  echo "YAML 문법 오류: $file" >&2
  echo "$err" | head -5 >&2
  echo "힌트: 값에 ': '(콜론+공백)가 있으면 값 전체를 따옴표로 감싼다." >&2
  exit 2
fi

exit 0
