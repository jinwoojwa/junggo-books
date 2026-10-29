#!/usr/bin/env bash
# 변경을 마치기 전에 실행하는 단일 검증 진입점.
# 현재는 backend 빌드(컴파일 + 테스트)만 확인한다. frontend가 생기면 여기에 추가한다.
set -euo pipefail

cd "$(dirname "$0")/.."

echo "==> backend: ./gradlew build"
(cd backend && ./gradlew build)

echo "==> verify OK"
