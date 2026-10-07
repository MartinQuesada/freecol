#!/usr/bin/env bash
# Launch FreeCol from this repo with development mods enabled.
#
# Usage:
#   ./bin/run-mods.sh              # build if needed, run with default mods
#   ./bin/run-mods.sh --debug      # also enable debug menus
#   MODS=libertadores ./bin/run-mods.sh
#   ./bin/run-mods.sh --no-splash  # any FreeCol CLI args are forwarded
#
# Default mods: libertadores, deeperBuildings, tasajo, lumberCraft, livestock, cacao, vanilla, tabernas, conversos, hdGraphics
# Config/saves for this launcher live in .freecol-dev/ (not your main FreeCol prefs).

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

DEV_DIR="$ROOT/.freecol-dev"
CONFIG_DIR="$DEV_DIR/config"
DATA_DIR="$DEV_DIR/data"
OPTIONS_FILE="$CONFIG_DIR/freecol/options.xml"
JAR="$ROOT/FreeCol.jar"
DEFAULT_MODS="${MODS:-libertadores,deeperBuildings,tasajo,lumberCraft,livestock,cacao,vanilla,tabernas,conversos,hdGraphics}"

log() { printf '==> %s\n' "$*"; }
die() { printf 'error: %s\n' "$*" >&2; exit 1; }

# FreeCol still uses java.applet (Cortado video) → needs JDK 11..16.
# Prefer Homebrew openjdk@11, then java_home 11/17 if applet present.
pick_java() {
  local candidates=()
  [[ -d /opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home ]] \
    && candidates+=("/opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home")
  [[ -d /usr/local/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home ]] \
    && candidates+=("/usr/local/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home")
  if command -v /usr/libexec/java_home >/dev/null; then
    local h
    for v in 11 12 13 14 15 16; do
      h="$(/usr/libexec/java_home -v "$v" 2>/dev/null || true)"
      [[ -n "$h" ]] && candidates+=("$h")
    done
  fi
  # Last resort: whatever is on PATH (may fail to compile on JDK 17+)
  if [[ -n "${JAVA_HOME:-}" ]]; then
    candidates=("$JAVA_HOME" "${candidates[@]}")
  fi

  local home
  for home in "${candidates[@]}"; do
    if [[ -x "$home/bin/javac" && -x "$home/bin/java" ]]; then
      export JAVA_HOME="$home"
      export PATH="$JAVA_HOME/bin:$PATH"
      log "Java: $("$JAVA_HOME/bin/java" -version 2>&1 | head -1)"
      return 0
    fi
  done
  die "Necesitás JDK 11 (FreeCol usa java.applet). Probá: brew install openjdk@11"
}

need_java() {
  pick_java
  command -v java >/dev/null || die "java no encontrado"
  command -v javac >/dev/null || die "javac no encontrado"
}

ensure_ant() {
  if command -v ant >/dev/null; then
    return 0
  fi
  if command -v brew >/dev/null; then
    log "Ant no está instalado. Instalando con Homebrew..."
    brew install ant
  else
    die "Ant no encontrado y no hay Homebrew. Instalá Ant o usá: brew install ant"
  fi
}

jar_stale() {
  [[ ! -f "$JAR" ]] && return 0
  # Rebuild if any source/data mod is newer than the jar
  local newest
  newest="$(find "$ROOT/src" \
    "$ROOT/data/mods/libertadores" "$ROOT/data/mods/deeperBuildings" "$ROOT/data/mods/tasajo" \
    "$ROOT/data/mods/lumberCraft" "$ROOT/data/mods/livestock" "$ROOT/data/mods/cacao" \
    "$ROOT/data/mods/vanilla" "$ROOT/data/mods/tabernas" "$ROOT/data/mods/conversos" \
    "$ROOT/data/mods/basicBuildings" "$ROOT/data/mods/hdGraphics" \
    -type f \( -name '*.java' -o -name '*.xml' -o -name '*.properties' -o -name '*.jpg' -o -name '*.png' \) \
    -newer "$JAR" 2>/dev/null | head -1 || true)"
  [[ -n "$newest" ]]
}

build() {
  need_java
  ensure_ant
  log "Compilando FreeCol (ant package)..."
  ant -q package
  [[ -f "$JAR" ]] || die "No se generó FreeCol.jar"
}

seed_options() {
  mkdir -p "$(dirname "$OPTIONS_FILE")"
  mkdir -p "$DATA_DIR/save" "$DATA_DIR/mods"

  local mods_xml=""
  local mod
  IFS=',' read -r -a mod_array <<< "$DEFAULT_MODS"
  for mod in "${mod_array[@]}"; do
    mod="$(echo "$mod" | tr -d '[:space:]')"
    [[ -z "$mod" ]] && continue
    [[ -d "$ROOT/data/mods/$mod" ]] || die "Mod no encontrado: data/mods/$mod"
    mods_xml+="      <modOption id=\"${mod}\" value=\"${mod}\"/>"$'\n'
  done

  # Only (re)write if missing or FORCE_SEED=1 — keeps window sizes etc. once created
  if [[ ! -f "$OPTIONS_FILE" || "${FORCE_SEED:-0}" == "1" ]]; then
    log "Escribiendo opciones con mods: $DEFAULT_MODS"
    cat > "$OPTIONS_FILE" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<clientOptions id="clientOptions" editable="true" visible="true">
  <optionGroup id="clientOptions.personal" editable="true" visible="true">
    <languageOption id="model.option.languageOption" value="es_ES"/>
  </optionGroup>
  <optionGroup id="clientOptions.mods" editable="true" visible="true">
    <modListOption id="clientOptions.mods.userMods" maximumNumber="20">
      <template>
        <modOption id="model.option.mod"/>
      </template>
${mods_xml}    </modListOption>
  </optionGroup>
</clientOptions>
EOF
  else
    # Ensure default mods are listed without wiping the rest of the options file.
    local missing=0
    for mod in "${mod_array[@]}"; do
      mod="$(echo "$mod" | tr -d '[:space:]')"
      [[ -z "$mod" ]] && continue
      if ! grep -q "modOption id=\"${mod}\"" "$OPTIONS_FILE"; then
        missing=1
        break
      fi
    done
    if [[ "$missing" -eq 1 ]]; then
      log "Actualizando lista de mods en opciones: $DEFAULT_MODS"
      # Replace the modListOption body (keep template + inject defaults)
      python3 - "$OPTIONS_FILE" "$DEFAULT_MODS" <<'PY'
import sys
from pathlib import Path
path = Path(sys.argv[1])
mods = [m.strip() for m in sys.argv[2].split(",") if m.strip()]
text = path.read_text()
start = text.find('<modListOption id="clientOptions.mods.userMods"')
if start < 0:
    sys.exit("modListOption not found")
end = text.find("</modListOption>", start)
if end < 0:
    sys.exit("modListOption end not found")
end += len("</modListOption>")
body = [
    '    <modListOption id="clientOptions.mods.userMods" maximumNumber="20">',
    "      <template>",
    '        <modOption id="model.option.mod"/>',
    "      </template>",
]
for m in mods:
    body.append(f'      <modOption id="{m}" value="{m}"/>')
body.append("    </modListOption>")
path.write_text(text[:start] + "\n".join(body) + text[end:])
PY
    fi
  fi
}

run_game() {
  local extra_args=("$@")
  log "Lanzando FreeCol"
  log "  data:   $ROOT/data"
  log "  config: $CONFIG_DIR"
  log "  mods:   $DEFAULT_MODS"
  exec java -Xmx2G -jar "$JAR" \
    --freecol-data "$ROOT/data" \
    --user-config-directory "$CONFIG_DIR" \
    --user-data-directory "$DATA_DIR" \
    --clientOptions "$OPTIONS_FILE" \
    --no-intro \
    "${extra_args[@]}"
}

# --- main ---
need_java

if [[ "${1:-}" == "--rebuild" ]]; then
  shift
  build
elif jar_stale; then
  build
else
  log "FreeCol.jar al día"
fi

seed_options
run_game "$@"
