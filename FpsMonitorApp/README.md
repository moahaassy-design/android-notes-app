# FPS Monitor Android App

Aplikasi monitoring FPS real-time untuk Android yang menggunakan Shizuku API untuk akses sistem tingkat tinggi.

## Fitur

- **Real-time FPS Monitoring**: Menampilkan FPS saat ini, rata-rata, dan frame time
- **Overlay yang Dapat Dipindah**: Overlay floating yang bisa digeser ke posisi manapun di layar
- **Integrasi Shizuku**: Menggunakan Shizuku API untuk akses sistem level yang diperlukan
- **Customizable**: Tema, ukuran, transparansi overlay dapat disesuaikan
- **Background Service**: Monitoring berjalan di background dengan notifikasi
- **Auto-save Position**: Posisi overlay tersimpan otomatis

## Prasyarat

1. **Shizuku** - Aplikasi ini memerlukan Shizuku untuk berjalan dengan benar
   - Install Shizuku dari Google Play Store atau GitHub
   - Aktifkan Shizuku melalui ADB atau root access
   
2. **Android 6.0+ (API Level 23+)**

3. **Permission yang Dibutuhkan**:
   - System Alert Window (Overlay)
   - Shizuku API access
   - Notification (Android 13+)

## Instalasi

1. Clone repository ini
2. Buka project di Android Studio
3. Build dan install aplikasi ke perangkat Android
4. Pastikan Shizuku sudah terinstall dan berjalan
5. Berikan permission yang diperlukan

## Cara Penggunaan

1. **Setup Shizuku**:
   - Install aplikasi Shizuku
   - Aktifkan Shizuku service melalui ADB: `adb shell sh /sdcard/Android/data/moe.shizuku.privileged.api/start.sh`
   
2. **Gunakan Aplikasi**:
   - Buka FPS Monitor
   - Berikan permission overlay dan Shizuku
   - Tekan "Start Monitoring"
   - Overlay FPS akan muncul di layar
   - Geser overlay ke posisi yang diinginkan

3. **Pengaturan**:
   - Akses menu Settings untuk kustomisasi
   - Atur tema, ukuran, interval update, dll

## Struktur Project

```
FpsMonitorApp/
├── app/
│   ├── src/main/java/com/fpsmonitor/app/
│   │   ├── MainActivity.java          # Activity utama
│   │   ├── FpsMonitorService.java     # Background service untuk monitoring
│   │   ├── ShizukuHelper.java         # Helper untuk integrasi Shizuku
│   │   ├── OverlayView.java          # Overlay UI untuk menampilkan FPS
│   │   └── SettingsActivity.java      # Activity pengaturan
│   ├── src/main/res/
│   │   ├── layout/                    # Layout files
│   │   ├── values/                    # String, color, style resources
│   │   ├── drawable/                  # Drawable resources
│   │   └── xml/                       # Preferences dan configuration
│   └── src/main/AndroidManifest.xml   # Manifest dengan permissions
└── build.gradle                       # Dependencies termasuk Shizuku API
```

## Dependencies Utama

- **Shizuku API**: `dev.rikka.shizuku:api:13.1.5`
- **AndroidX**: AppCompat, Material Design, ConstraintLayout
- **Preferences**: Untuk pengaturan aplikasi

## Catatan Pengembangan

### Monitoring FPS
Aplikasi ini menggunakan pendekatan berikut untuk monitoring FPS:
1. **Shizuku API** untuk akses sistem level
2. **Simulasi FPS** (dalam implementasi saat ini untuk demonstrasi)
3. **Dapat diperluas** untuk membaca data FPS real dari sistem

### Implementasi Real FPS Monitoring
Untuk implementasi FPS monitoring yang sesungguhnya, bisa menggunakan:
- `/sys/class/graphics/fb0/fps`
- `dumpsys SurfaceFlinger`
- WindowManager frame callbacks
- GraphicsStats API (dengan permission yang tepat)

### Keamanan
- Aplikasi memerlukan Shizuku untuk akses sistem
- Shizuku menyediakan cara aman untuk akses privileged tanpa root
- Semua permission diminta secara eksplisit kepada user

## Troubleshooting

### Shizuku tidak terdeteksi
- Pastikan Shizuku service berjalan
- Restart Shizuku service
- Periksa koneksi ADB jika menggunakan wireless debugging

### Overlay tidak muncul
- Pastikan permission "Display over other apps" sudah diberikan
- Periksa pengaturan overlay di system settings

### FPS tidak akurat
- Implementasi saat ini menggunakan data simulasi
- Untuk akurasi penuh, perlu implementasi pembacaan data sistem yang lebih mendalam

## Kontribusi

Kontribusi sangat diterima! Silakan buat issue atau pull request untuk:
- Implementasi FPS monitoring yang lebih akurat
- Peningkatan UI/UX
- Optimasi performa
- Bug fixes

## Lisensi

Project ini menggunakan lisensi MIT. Lihat file LICENSE untuk detail.

## Credits

- **Shizuku API** oleh RikkaApps untuk menyediakan akses sistem yang aman
- **Material Design** untuk komponen UI
- Inspirasi dari aplikasi TakoStats dan FPS monitor lainnya
