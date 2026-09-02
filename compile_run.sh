#!/bin/bash
# ============================================================
#  compile_run.sh
#  Build and run the Sunrise Dental Clinic Management System.
#  Run this script from the SunriseDentalClinic/ directory.
# ============================================================

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src"
OUT_DIR="$PROJECT_DIR/out"

# ── JDK Auto-Detection ─────────────────────────────────────────
# Priority 1: Portable Temurin JDK extracted in /tmp
PORTABLE_JDK="/tmp/jdk-21.0.4+7/Contents/Home"
if [ -d "$PORTABLE_JDK" ]; then
    export JAVA_HOME="$PORTABLE_JDK"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

# Priority 2: System JAVA_HOME (if already set)
# (already on PATH — nothing to do)

# Priority 3: macOS java_home helper
if ! command -v javac &>/dev/null; then
    SYSTEM_JH=$(/usr/libexec/java_home 2>/dev/null)
    if [ -n "$SYSTEM_JH" ]; then
        export JAVA_HOME="$SYSTEM_JH"
        export PATH="$JAVA_HOME/bin:$PATH"
    fi
fi

if ! command -v javac &>/dev/null; then
    echo ""
    echo "  ERROR: No Java Development Kit (JDK) found."
    echo "  Please install Java 11+ from https://adoptium.net and try again."
    exit 1
fi

echo ""
echo "  ╔═══════════════════════════════════════════════╗"
echo "  ║   Sunrise Dental Clinic — Build & Run Tool    ║"
echo "  ╚═══════════════════════════════════════════════╝"
echo ""

# ── Step 1: Create output directory ───────────────────────────
mkdir -p "$OUT_DIR"

# ── Step 2: Compile ────────────────────────────────────────────
echo "  [1/2]  Compiling Java sources..."
javac -d "$OUT_DIR" "$SRC_DIR"/*.java 2>&1

if [ $? -ne 0 ]; then
    echo ""
    echo "  ERROR: Compilation failed. Please fix the errors above and try again."
    exit 1
fi

echo "         Compilation successful."
echo ""

# ── Step 3: Run (from project root so data/ path resolves) ────
echo "  [2/2]  Starting application..."
echo ""
cd "$PROJECT_DIR"
java -cp "$OUT_DIR" Main

echo ""
echo "  Application exited."
