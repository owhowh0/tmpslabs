#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [ ! -f "out/production/lab2.jar" ]; then
    bash "$SCRIPT_DIR/build.sh"
fi

java -cp out/production/lab2.jar com.tmps.lab2.client.MainKt "$@"
