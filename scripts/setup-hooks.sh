#!/usr/bin/env bash
# 저장소의 Git hook(.githooks)을 활성화한다. clone 후 한 번 실행한다.
set -euo pipefail

cd "$(dirname "$0")/.."
git config core.hooksPath .githooks
echo "Git hook 활성화: core.hooksPath=.githooks"
