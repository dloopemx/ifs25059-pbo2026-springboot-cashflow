#!/usr/bin/env sh
# Maven Wrapper sederhana (Linux/macOS/Jenkins).
# Urutan: pakai `mvn` jika sudah terpasang; jika belum, unduh Apache Maven
# memakai curl, wget, atau Java (fallback untuk container JDK tanpa curl/wget,
# misalnya eclipse-temurin di Jenkins), lalu ekstrak dengan tar.
set -eu

MAVEN_VERSION="3.9.11"
BASE_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"

if command -v mvn >/dev/null 2>&1; then
  exec mvn -f "${BASE_DIR}/pom.xml" "$@"
fi

if [ -z "${MAVEN_USER_HOME:-}" ]; then
  if [ -n "${MAVEN_LOCAL_REPO:-}" ]; then
    # Simpan Maven di sebelah cache dependency (volume Jenkins) agar tidak diunduh ulang.
    MAVEN_USER_HOME="$(dirname -- "${MAVEN_LOCAL_REPO}")"
  else
    MAVEN_USER_HOME="${HOME}/.m2"
  fi
fi

DIST_URL="${MAVEN_DIST_URL:-https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz}"
CACHE_DIR="${MAVEN_USER_HOME}/wrapper/dists/apache-maven-${MAVEN_VERSION}"
MAVEN_HOME="${CACHE_DIR}/apache-maven-${MAVEN_VERSION}"

if [ ! -x "${MAVEN_HOME}/bin/mvn" ]; then
  mkdir -p "${CACHE_DIR}"
  TMP_DIR="$(mktemp -d)"
  trap 'rm -rf "${TMP_DIR}"' EXIT
  ARCHIVE="${TMP_DIR}/maven.tar.gz"
  echo "Maven tidak ditemukan; mengunduh Apache Maven ${MAVEN_VERSION}..."

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "${DIST_URL}" -o "${ARCHIVE}"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "${ARCHIVE}" "${DIST_URL}"
  else
    if [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
      JAVA_CMD="${JAVA_HOME}/bin/java"
    else
      JAVA_CMD="java"
    fi
    cat > "${TMP_DIR}/Downloader.java" <<'JAVA'
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Downloader {
    public static void main(String[] args) throws Exception {
        URLConnection connection = URI.create(args[0]).toURL().openConnection();
        if (connection instanceof HttpURLConnection http) {
            http.setInstanceFollowRedirects(true);
            if (http.getResponseCode() >= 400) {
                throw new IllegalStateException("HTTP " + http.getResponseCode() + " saat mengunduh " + args[0]);
            }
        }
        try (InputStream in = connection.getInputStream()) {
            Files.copy(in, Path.of(args[1]), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
JAVA
    "${JAVA_CMD}" "${TMP_DIR}/Downloader.java" "${DIST_URL}" "${ARCHIVE}"
  fi

  tar -xzf "${ARCHIVE}" -C "${CACHE_DIR}"
  chmod +x "${MAVEN_HOME}/bin/mvn"
fi

exec "${MAVEN_HOME}/bin/mvn" -f "${BASE_DIR}/pom.xml" "$@"
