#!/usr/bin/env bash
# PreToolUse(Edit|Write): 실수로 고치면 안 되는 파일을 막는다.
#  - .env 계열 (.env.example 제외): 비밀 값이 들어갈 수 있다.
#  - 이미 커밋된 Flyway 마이그레이션: 적용된 파일을 바꾸면 체크섬이 어긋난다. 새 버전을 추가해야 한다.
# 차단은 exit 2 + stderr 메시지로 Claude에게 이유를 알린다.
set -uo pipefail

file=$(jq -r '.tool_input.file_path // empty')
[ -z "$file" ] && exit 0

name=$(basename "$file")
case "$name" in
  .env.example) ;;
  .env|.env.*)
    echo "차단: $name 은 직접 수정하지 않는다. 필요한 변수는 backend/.env.example 에 추가하고 값은 사용자가 .env에 넣는다." >&2
    exit 2
    ;;
esac

case "$file" in
  */db/migration/V*.sql)
    if git -C "$(dirname "$file")" ls-files --error-unmatch "$file" >/dev/null 2>&1; then
      echo "차단: $name 은 이미 커밋된 마이그레이션이다. 수정하지 말고 다음 버전(V{n+1}__*.sql)을 새로 추가한다." >&2
      exit 2
    fi
    ;;
esac

exit 0
