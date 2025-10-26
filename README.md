# Android Notes App 📝

Aplikasi catatan sederhana untuk Android dengan fitur CRUD lengkap, database SQLite, dan UI Material Design.

## ✨ Fitur

- ✅ **Tambah Catatan**: Buat catatan baru dengan judul dan konten
- ✏️ **Edit Catatan**: Edit catatan yang sudah ada
- 🗑️ **Hapus Catatan**: Hapus catatan dengan swipe atau tombol
- 🔍 **Cari Catatan**: Cari catatan berdasarkan judul atau konten
- 📱 **UI Modern**: Material Design 3 dengan warna purple yang menarik
- 🗄️ **Database SQLite**: Penyimpanan lokal yang aman dan cepat
- ⏰ **Timestamp**: Otomatis menyimpan waktu created dan updated
- 📦 **APK Build**: Otomatis build APK via GitHub Actions

## 🛠️ Teknologi

- **Bahasa**: Kotlin
- **UI Framework**: Android Views + Material Design Components
- **Database**: SQLite dengan Room-like pattern
- **Build System**: Gradle 8.3
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **CI/CD**: GitHub Actions untuk otomatis build APK

## 🚀 Instalasi

### Download APK
1. Pergi ke tab **Actions** di repository GitHub
2. Pilih workflow **Build APK** yang sudah selesai
3. Download artifact `app-release.apk`
4. Install di device Android

### Build dari Source Code
1. Clone repository ini:
   ```bash
   git clone https://github.com/moahaassy-design/android-notes-app.git
   cd android-notes-app
   ```

2. Buka di Android Studio

3. Sync project dengan Gradle

4. Build APK:
   ```bash
   ./gradlew assembleRelease
   ```

## 📱 Cara Menggunakan

1. **Tambah Catatan**: Tap tombol + (Floating Action Button)
2. **Edit Catatan**: Tap pada catatan yang ingin diedit
3. **Cari Catatan**: Gunakan SearchView di action bar
4. **Hapus Catatan**: Tap dan tahan kartu catatan atau gunakan menu

## 📁 Struktur Project

```
app/src/main/java/com/notes/android/
├── MainActivity.kt          # Activity utama untuk menampilkan list
├── AddEditNoteActivity.kt   # Activity untuk tambah/edit catatan
├── Note.kt                 # Data model untuk catatan
├── DatabaseHelper.kt       # Helper untuk operasi SQLite
└── NoteAdapter.kt          # Adapter untuk RecyclerView

app/src/main/res/
├── layout/
│   ├── activity_main.xml           # Layout activity utama
│   ├── activity_add_edit_note.xml  # Layout tambah/edit
│   └── item_note.xml              # Layout item catatan
├── values/
│   ├── colors.xml                 # Warna aplikasi
│   ├── strings.xml                # String resources
│   └── themes.xml                 # Tema aplikasi
├── menu/
│   ├── menu_main.xml              # Menu action bar utama
│   └── menu_add_edit_note.xml     # Menu add/edit activity
└── xml/
    ├── backup_rules.xml           # Rules backup Android
    └── data_extraction_rules.xml  # Rules data extraction
```

## 🔧 Konfigurasi Build

- **Gradle Version**: 8.3
- **Kotlin Version**: 1.9.20
- **Android Gradle Plugin**: 8.1.4
- **Compile SDK**: 34
- **Min SDK**: 24
- **Target SDK**: 34

## 📊 Database Schema

Table: `notes`
- `id` (INTEGER PRIMARY KEY AUTOINCREMENT)
- `title` (TEXT NOT NULL)
- `content` (TEXT)
- `created_at` (INTEGER NOT NULL)
- `updated_at` (INTEGER NOT NULL)

## 🤝 Kontribusi

1. Fork repository ini
2. Buat feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push ke branch (`git push origin feature/AmazingFeature`)
5. Buat Pull Request

## 📄 License

Project ini menggunakan MIT License - lihat file [LICENSE](LICENSE) untuk detail.

## 🐛 Bug Reports

Jika menemukan bug, silakan buat issue di repository ini dengan format:
- **Bug**: Deskripsi bug
- **Reproduction**: Langkah untuk reproduce bug
- **Expected**: Apa yang diharapkan
- **Actual**: Apa yang sebenarnya terjadi

## 📞 Support

Untuk bantuan atau pertanyaan, silakan buat issue di repository GitHub ini.

---

**Dikembangkan dengan ❤️ oleh MiniMax Agent**