package com.fpsmonitor.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

public class FpsMonitorService extends Service {

    public static final String ACTION_START_MONITORING = "START_MONITORING";
    public static final String ACTION_STOP_MONITORING = "STOP_MONITORING";
    
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "FPS_MONITOR_CHANNEL";
    private static final int UPDATE_INTERVAL = 1000; // 1 second

    private ShizukuHelper shizukuHelper;
    private OverlayView overlayView;
    private Handler updateHandler;
    private Runnable updateRunnable;
    private boolean isMonitoring = false;

    @Override
    public void onCreate() {
        super.onCreate();
        shizukuHelper = new ShizukuHelper();
        overlayView = new OverlayView(this);
        updateHandler = new Handler(Looper.getMainLooper());
        
        createNotificationChannel();
        setupUpdateRunnable();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_START_MONITORING:
                    startMonitoring();
                    break;
                case ACTION_STOP_MONITORING:
                    stopMonitoring();
                    break;
            }
        }
        return START_STICKY;
    }

    private void startMonitoring() {
        if (isMonitoring) return;
        
        isMonitoring = true;
        
        // Start foreground service
        Notification notification = createNotification("FPS Monitoring aktif");
        startForeground(NOTIFICATION_ID, notification);
        
        // Show overlay
        overlayView.show();
        
        // Start FPS updates
        updateHandler.post(updateRunnable);
    }

    private void stopMonitoring() {
        if (!isMonitoring) return;
        
        isMonitoring = false;
        
        // Hide overlay
        overlayView.hide();
        
        // Stop updates
        updateHandler.removeCallbacks(updateRunnable);
        
        // Stop foreground service
        stopForeground(true);
        stopSelf();
    }

    private void setupUpdateRunnable() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isMonitoring) return;
                
                updateFpsDisplay();
                updateHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
    }

    private void updateFpsDisplay() {
        if (shizukuHelper.hasPermission()) {
            ShizukuHelper.GraphicsStats stats = shizukuHelper.getGraphicsStats();
            if (stats != null) {
                overlayView.updateFps(stats.currentFps, stats.averageFps, stats.frameTime);
                
                // Update notification
                String notificationText = String.format("FPS: %d | Avg: %d", 
                    stats.currentFps, stats.averageFps);
                Notification notification = createNotification(notificationText);
                
                NotificationManager notificationManager = 
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        } else {
            overlayView.updateFps(-1, -1, 0);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "FPS Monitor Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("FPS monitoring background service");
            channel.setShowBadge(false);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(String contentText) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent stopIntent = new Intent(this, FpsMonitorService.class);
        stopIntent.setAction(ACTION_STOP_MONITORING);
        PendingIntent stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FPS Monitor")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_fps_monitor)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMonitoring();
        if (overlayView != null) {
            overlayView.destroy();
        }
    }
}
