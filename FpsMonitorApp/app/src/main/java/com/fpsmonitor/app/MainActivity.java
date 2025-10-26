package com.fpsmonitor.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import rikka.shizuku.Shizuku;

public class MainActivity extends AppCompatActivity implements Shizuku.OnRequestPermissionResultListener {

    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 1001;
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1002;

    private Button btnToggleMonitoring;
    private TextView tvStatus;
    private TextView tvShizukuStatus;
    private ShizukuHelper shizukuHelper;
    private boolean isMonitoring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupShizuku();
        checkPermissions();
    }

    private void initializeViews() {
        btnToggleMonitoring = findViewById(R.id.btnToggleMonitoring);
        tvStatus = findViewById(R.id.tvStatus);
        tvShizukuStatus = findViewById(R.id.tvShizukuStatus);

        btnToggleMonitoring.setOnClickListener(this::toggleMonitoring);
        updateUI();
    }

    private void setupShizuku() {
        shizukuHelper = new ShizukuHelper();
        Shizuku.addRequestPermissionResultListener(this);
        updateShizukuStatus();
    }

    private void checkPermissions() {
        // Check overlay permission
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission();
        }

        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 
                    REQUEST_CODE_NOTIFICATION_PERMISSION);
            }
        }

        // Check Shizuku permission
        if (shizukuHelper.checkShizukuPermission() != PackageManager.PERMISSION_GRANTED) {
            if (shizukuHelper.shouldShowRequestPermissionRationale()) {
                // Show explanation
                Toast.makeText(this, "Shizuku permission diperlukan untuk monitoring FPS", 
                    Toast.LENGTH_LONG).show();
            }
            shizukuHelper.requestPermission();
        }
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
    }

    private void toggleMonitoring(View view) {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Permission overlay diperlukan", Toast.LENGTH_SHORT).show();
            requestOverlayPermission();
            return;
        }

        if (shizukuHelper.checkShizukuPermission() != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Shizuku diperlukan", Toast.LENGTH_SHORT).show();
            shizukuHelper.requestPermission();
            return;
        }

        if (isMonitoring) {
            stopMonitoring();
        } else {
            startMonitoring();
        }
    }

    private void startMonitoring() {
        Intent serviceIntent = new Intent(this, FpsMonitorService.class);
        serviceIntent.setAction(FpsMonitorService.ACTION_START_MONITORING);
        startForegroundService(serviceIntent);
        isMonitoring = true;
        updateUI();
        Toast.makeText(this, "FPS Monitoring dimulai", Toast.LENGTH_SHORT).show();
    }

    private void stopMonitoring() {
        Intent serviceIntent = new Intent(this, FpsMonitorService.class);
        serviceIntent.setAction(FpsMonitorService.ACTION_STOP_MONITORING);
        startService(serviceIntent);
        isMonitoring = false;
        updateUI();
        Toast.makeText(this, "FPS Monitoring dihentikan", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        if (isMonitoring) {
            btnToggleMonitoring.setText("Stop Monitoring");
            tvStatus.setText("Status: Monitoring Aktif");
        } else {
            btnToggleMonitoring.setText("Start Monitoring");
            tvStatus.setText("Status: Tidak Aktif");
        }
    }

    private void updateShizukuStatus() {
        if (shizukuHelper.isShizukuAvailable()) {
            if (shizukuHelper.checkShizukuPermission() == PackageManager.PERMISSION_GRANTED) {
                tvShizukuStatus.setText("Shizuku: Tersedia dan Diizinkan");
            } else {
                tvShizukuStatus.setText("Shizuku: Tersedia, butuh permission");
            }
        } else {
            tvShizukuStatus.setText("Shizuku: Tidak tersedia");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Permission overlay diberikan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission overlay ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission notifikasi diberikan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, int grantResult) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Shizuku diberikan", Toast.LENGTH_SHORT).show();
            updateShizukuStatus();
        } else {
            Toast.makeText(this, "Permission Shizuku ditolak", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Shizuku.removeRequestPermissionResultListener(this);
    }
}
