#!/usr/bin/env bash
# Install what ./bin/run-mods.sh needs on Debian (or Ubuntu): JDK 11, Ant, git, python3.
#
# Debian 11 (bullseye) ships openjdk-11-jdk. Debian 12+ no longer does, so
# JDK 11 comes from the Eclipse Adoptium (Temurin) apt repository instead.
# Where that package can not be installed (32-bit ARM on Debian 13, whose
# libasound2 is now libasound2t64), the Temurin tarball is unpacked into
# /usr/lib/jvm instead.
#
# Usage: ./bin/setup-debian.sh

set -euo pipefail

log() { printf '==> %s\n' "$*"; }
die() { printf 'error: %s\n' "$*" >&2; exit 1; }

SUDO=""
[[ "$(id -u)" -ne 0 ]] && SUDO="sudo"

. /etc/os-release
DEB_ARCH="$(dpkg --print-architecture)"
log "Sistema: ${PRETTY_NAME:-desconocido} ($DEB_ARCH)"

$SUDO apt-get update
$SUDO apt-get install -y ant git python3 wget gpg ca-certificates

install_temurin_tarball() {
  local arch
  case "$DEB_ARCH" in
    amd64) arch=x64 ;;
    arm64) arch=aarch64 ;;
    armhf) arch=arm ;;
    ppc64el) arch=ppc64le ;;
    s390x) arch=s390x ;;
    *) die "No hay Temurin 11 para $DEB_ARCH" ;;
  esac

  local alsa=libasound2
  apt-cache show libasound2t64 >/dev/null 2>&1 && alsa=libasound2t64
  log "Instalando librerías que necesita el JDK"
  $SUDO apt-get install -y "$alsa" libfreetype6 fontconfig fonts-dejavu-core \
    libx11-6 libxext6 libxrender1 libxtst6 libxi6

  local dest="/usr/lib/jvm/temurin-11-jdk-$DEB_ARCH"
  local tmp
  tmp="$(mktemp -d)"
  log "Descargando Temurin 11 ($arch) en $dest"
  wget -qO "$tmp/jdk.tar.gz" \
    "https://api.adoptium.net/v3/binary/latest/11/ga/linux/$arch/jdk/hotspot/normal/eclipse"
  $SUDO rm -rf "$dest"
  $SUDO mkdir -p "$dest"
  $SUDO tar -xzf "$tmp/jdk.tar.gz" -C "$dest" --strip-components=1
  rm -rf "$tmp"
}

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
  if ! $SUDO apt-get install -y temurin-11-jdk; then
    log "El paquete temurin-11-jdk no se puede instalar en $DEB_ARCH: uso el tar.gz"
    install_temurin_tarball
  fi
fi

log "JDK 11 instalado:"
for j in /usr/lib/jvm/temurin-11-jdk* /usr/lib/jvm/java-11-openjdk*; do
  if [[ -x "$j/bin/java" ]]; then
    echo "    $j"
    "$j/bin/java" -version 2>&1 | head -1 | sed 's/^/    /'
  fi
done
log "Listo. Ahora: ./bin/run-mods.sh"
