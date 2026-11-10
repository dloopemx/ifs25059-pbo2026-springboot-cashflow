# PBO Practice 4 — Cashflow

Aplikasi pencatatan transaksi keuangan pribadi menggunakan Java 25, Spring Boot 4.1.2, Maven, Vaadin 25.3.4, dan Clean Architecture. Penyimpanan bersifat **in-memory**, sehingga data akan kembali kosong setelah aplikasi direstart (sesuai modul).

## Fitur

- Tambah pemasukan (`INCOME`) dan pengeluaran (`EXPENSE`).
- Validasi keterangan wajib diisi dan jumlah harus positif, numerik, serta maksimal dua angka desimal.
- Pencarian keterangan tanpa membedakan huruf besar/kecil.
- Pengurutan melalui empat `SortOption` berbasis `Comparator`.
- Ringkasan total transaksi, pemasukan, pengeluaran, dan saldo.
- Hapus transaksi dengan konfirmasi.
- Format uang `Rp 1,000,000.00`.
- Domain exception terstruktur dan presenter terpisah.

## Identitas proyek

- Artifact: `ifs25059-pbo2026-springboot-cashflow`
- Group: `org.delcom`
- Package: `org.delcom.ifs25059pbo2026springbootcashflow`
- Repository yang digunakan sebelumnya: `dloopemx/ifs25059-pbo2026-springboot-cashflow` (branch `main`)
- Entry point: `App.java`

## Persyaratan

JDK 25 dan koneksi internet untuk pengunduhan dependency Maven pertama kali. Jika Maven sudah terpasang, wrapper menggunakan instalasi `mvn` yang tersedia. Jika belum, `mvnw` mengunduh Apache Maven 3.9.11 ke cache pengguna.

## Menjalankan di Windows

```powershell
# Salin template jika .env belum tersedia
Copy-Item .env.example .env

# Jalankan aplikasi
.\mvnw.cmd spring-boot:run
```

Buka `http://localhost:8080`. Ubah `APP_PORT` di `.env` jika port 8080 sedang digunakan.

## Build JAR production

```powershell
.\mvnw.cmd -B clean package -Pproduction
java -jar target\ifs25059-pbo2026-springboot-cashflow-0.0.1-SNAPSHOT.jar
```

Penting: gunakan `-Pproduction` saat membuat JAR Vaadin agar dependency/tooling development tidak diperlukan saat aplikasi dijalankan.

## Pengujian

```powershell
.\mvnw.cmd -B test
.\mvnw.cmd -B clean verify
```

Laporan JaCoCo tersedia di `target/site/jacoco/index.html` setelah `verify`.

## Struktur Clean Architecture

```text
src/main/java/org/delcom/ifs25059pbo2026springbootcashflow/
├── App.java
├── DotEnv.java
├── config/AppConfig.java
├── domain/
│   ├── entity/                 # Transaction, TransactionType, SortOption
│   ├── exception/              # DomainException dan turunannya
│   └── repository/              # IRepository<T, ID>, ITransactionRepository
├── usecase/FinanceUseCase.java
├── adapter/
│   ├── repository/              # InMemoryRepository, TransactionRepository
│   └── presenter/               # FinancePresenter
└── framework/view/FinanceView.java
```

## CI/CD Delcom — hal yang wajib diperhatikan

Panduan `delcom-cicd-pbo.html` menyatakan Jenkins harus memakai:

1. Job dengan nama yang sama dengan repository GitHub.
2. Credential GitHub bertipe **Username with password**, username `dloopemx`, ID `github-dloopemx`, dan password diisi GitHub Personal Access Token (PAT). Jangan commit PAT ke ZIP/repository.
3. Environment/properties pada Jenkins diisi dari konfigurasi website Delcom.
4. Trigger `GitHub hook trigger for GITScm polling`.
5. Pipeline definition `Pipeline script from SCM`, SCM Git, branch sesuai branch default, dan Script Path `Jenkinsfile`.
6. Webhook GitHub ke `https://jenkins.delcom.org/github-webhook/`.

**Penting untuk nilai submission:** Jenkinsfile di ZIP ini adalah pipeline pemeriksaan Build/Task dan pengarsipan JAR, bukan pengganti Jenkinsfile resmi yang diunduh dari konten kursus **Jenkins Grading**. Tahap Deploy Delcom membutuhkan konfigurasi/prosedur resmi dari kursus dan website Delcom; jangan menganggap arsip JAR sebagai deployment website. Sebelum sinkronisasi hasil grading, ganti `Jenkinsfile` ini dengan file resmi submission dan pertahankan `pom.xml`, `App.java`, serta `AppTest.java` sesuai instruksi modul.

File `.env` di dalam ZIP hanya berisi `APP_PORT=8080` untuk pengembangan lokal. Jangan memasukkan token GitHub, password, atau credential ke repository.
