#!/usr/bin/env bash
# 변경을 마치기 전에 실행하는 단일 검증 진입점.
# backend(컴파일 + 테스트 + 포맷)와 frontend(lint + typecheck + build)를 확인한다.
set -euo pipefail

cd "$(dirname "$0")/.."

echo "==> hooks: commit-msg 검사"
scripts/test-hooks.sh

echo "==> backend: ./gradlew build"
(cd backend && ./gradlew build)

echo "==> frontend: pnpm install / lint / typecheck / build"
# corepack이 package.json의 packageManager 버전으로 pnpm을 실행한다 (별도 설치 불필요)
export COREPACK_ENABLE_DOWNLOAD_PROMPT=0
(
  cd frontend
  corepack pnpm install --frozen-lockfile
  corepack pnpm lint
  corepack pnpm typecheck
  corepack pnpm build
)

echo "==> verify OK"
