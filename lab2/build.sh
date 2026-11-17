#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

KOTLINC_CMD=$(which kotlinc 2>/dev/null || echo "/opt/intellij-idea-ultimate-edition/plugins/Kotlin/kotlinc/bin/kotlinc")
if [ ! -x "$KOTLINC_CMD" ]; then
    echo "Error: kotlinc compiler not found. Please install kotlinc or ensure it is in PATH."
    exit 1
fi

mkdir -p out/production
echo "Compiling Lab 2 Kotlin sources..."
"$KOTLINC_CMD" $(find src -name "*.kt") -include-runtime -d out/production/lab2.jar
echo "Build successful -> out/production/lab2.jar"
