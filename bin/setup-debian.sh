#!/usr/bin/env bash
# Install what ./bin/run-mods.sh needs on Debian (or Ubuntu): JDK 11, Ant, git, python3.
#
# Debian 11 (bullseye) ships openjdk-11-jdk. Debian 12+ no longer does, so
# JDK 11 comes from the Eclipse Adoptium (Temurin) apt repository instead.
#
# Usage: ./bin/setup-debian.sh

set -euo pipefail

log() { printf '==> %s\n' "$*"; }

SUDO=""
[[ "$(id -u)" -ne 0 ]] && SUDO="sudo"

. /etc/os-release
log "Sistema: ${PRETTY_NAME:-desconocido}"

$SUDO apt-get update
$SUDO apt-get install -y ant git python3 wget gpg ca-certificates

if apt-cache show openjdk-11-jdk >/dev/null 2>&1; then
  log "Instalando openjdk-11-jdk desde los repositorios del sistema"
  $SUDO apt-get install -y openjdk-11-jdk
else
  log "openjdk-11-jdk no está en ${VERSION_CODENAME:-este sistema}: uso Temurin 11 (Adoptium)"
  wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public \
    | gpg --dearmor | $SUDO tee /usr/share/keyrings/adoptium.gpg >/dev/null
  echo "deb [signed-by=/usr/share/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb ${VERSION_CODENAME} main" \
    | $SUDO tee /etc/apt/sources.list.d/adoptium.list >/dev/null
  $SUDO apt-get update
  $SUDO apt-get install -y temurin-11-jdk
fi

log "JDK 11 instalado en:"
ls -d /usr/lib/jvm/temurin-11-jdk* /usr/lib/jvm/java-11-openjdk* 2>/dev/null || true
log "Listo. Ahora: ./bin/run-mods.sh"
