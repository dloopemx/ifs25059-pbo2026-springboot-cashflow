#!/usr/bin/env sh
set -eu
MAVEN_VERSION="3.9.11"
BASE_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
if command -v mvn >/dev/null 2>&1; then exec mvn "$@"; fi
CACHE_DIR="${MAVEN_USER_HOME:-${HOME}/.m2}/wrapper/dists/apache-maven-${MAVEN_VERSION}"
MAVEN_HOME="${CACHE_DIR}/apache-maven-${MAVEN_VERSION}"
if [ ! -x "${MAVEN_HOME}/bin/mvn" ]; then
  mkdir -p "$CACHE_DIR"
  ARCHIVE="${CACHE_DIR}/maven.zip"
  URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.zip"
  echo "Maven tidak ditemukan; mengunduh Apache Maven ${MAVEN_VERSION}..."
  if command -v curl >/dev/null 2>&1; then curl -fL "$URL" -o "$ARCHIVE"
  elif command -v wget >/dev/null 2>&1; then wget -O "$ARCHIVE" "$URL"
  else echo "curl atau wget diperlukan untuk mengunduh Maven." >&2; exit 1; fi
  if command -v unzip >/dev/null 2>&1; then unzip -oq "$ARCHIVE" -d "$CACHE_DIR"
  else echo "unzip diperlukan untuk mengekstrak Maven." >&2; exit 1; fi
  rm -f "$ARCHIVE"
fi
exec "${MAVEN_HOME}/bin/mvn" -f "${BASE_DIR}/pom.xml" "$@"
