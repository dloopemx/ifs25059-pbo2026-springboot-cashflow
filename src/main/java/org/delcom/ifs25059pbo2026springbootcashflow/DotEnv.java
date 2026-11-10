package org.delcom.ifs25059pbo2026springbootcashflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Memuat konfigurasi sederhana dari .env tanpa menimpa environment yang sudah ada. */
public final class DotEnv {
    private DotEnv() { }

    public static void load() {
        Path file = Path.of(".env");
        if (!Files.isRegularFile(file)) return;
        try {
            for (String raw : Files.readAllLines(file)) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int separator = line.indexOf('=');
                if (separator <= 0) continue;
                String key = line.substring(0, separator).trim();
                String value = line.substring(separator + 1).trim();
                if ((value.startsWith("\"") && value.endsWith("\"")) ||
                    (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }
                if (System.getenv(key) == null && System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Tidak dapat membaca file .env", exception);
        }
    }
}
