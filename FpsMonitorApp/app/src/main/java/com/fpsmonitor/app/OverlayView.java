package com.fpsmonitor.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.preference.PreferenceManager;

public class OverlayView {
    
    private Context context;
    private WindowManager windowManager;
    private View overlayView;
    private WindowManager.LayoutParams params;
    
    private TextView tvCurrentFps;
    private TextView tvAvgFps;
    private TextView tvFrameTime;
    
    private boolean isShowing = false;
    private SharedPreferences preferences;
    
    // Touch handling for dragging
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;

    public OverlayView(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        
        setupOverlayView();
    }

    private void setupOverlayView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        overlayView = inflater.inflate(R.layout.overlay_fps, null);
        
        // Find views
        tvCurrentFps = overlayView.findViewById(R.id.tvCurrentFps);
        tvAvgFps = overlayView.findViewById(R.id.tvAvgFps);
        tvFrameTime = overlayView.findViewById(R.id.tvFrameTime);
        
        // Setup window params
        int layoutFlag;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }
        
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        
        // Set initial position from preferences
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = preferences.getInt("overlay_x", 100);
        params.y = preferences.getInt("overlay_y", 100);
        
        // Setup touch listener for dragging
        setupTouchListener();
        
        // Apply theme
        applyTheme();
    }

    private void setupTouchListener() {
        overlayView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                        
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        
                        if (isShowing) {
                            windowManager.updateViewLayout(overlayView, params);
                        }
                        return true;
                        
                    case MotionEvent.ACTION_UP:
                        // Save position to preferences
                        preferences.edit()
                                .putInt("overlay_x", params.x)
                                .putInt("overlay_y", params.y)
                                .apply();
                        return true;
                }
                return false;
            }
        });
    }

    private void applyTheme() {
        boolean darkTheme = preferences.getBoolean("dark_theme", true);
        LinearLayout container = overlayView.findViewById(R.id.overlayContainer);
        
        if (darkTheme) {
            container.setBackgroundColor(Color.argb(200, 0, 0, 0));
            tvCurrentFps.setTextColor(Color.WHITE);
            tvAvgFps.setTextColor(Color.LTGRAY);
            tvFrameTime.setTextColor(Color.LTGRAY);
        } else {
            container.setBackgroundColor(Color.argb(200, 255, 255, 255));
            tvCurrentFps.setTextColor(Color.BLACK);
            tvAvgFps.setTextColor(Color.DKGRAY);
            tvFrameTime.setTextColor(Color.DKGRAY);
        }
        
        // Apply corner radius
        float cornerRadius = preferences.getFloat("corner_radius", 8.0f);
        container.setBackground(createRoundedBackground(darkTheme, cornerRadius));
    }

    private android.graphics.drawable.GradientDrawable createRoundedBackground(boolean darkTheme, float cornerRadius) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setCornerRadius(cornerRadius * context.getResources().getDisplayMetrics().density);
        
        if (darkTheme) {
            drawable.setColor(Color.argb(200, 0, 0, 0));
            drawable.setStroke(2, Color.argb(100, 255, 255, 255));
        } else {
            drawable.setColor(Color.argb(200, 255, 255, 255));
            drawable.setStroke(2, Color.argb(100, 0, 0, 0));
        }
        
        return drawable;
    }

    public void show() {
        if (isShowing) return;
        
        try {
            windowManager.addView(overlayView, params);
            isShowing = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hide() {
        if (!isShowing) return;
        
        try {
            windowManager.removeView(overlayView);
            isShowing = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFps(int currentFps, int avgFps, float frameTime) {
        if (!isShowing) return;
        
        if (currentFps >= 0) {
            tvCurrentFps.setText(String.format("%d FPS", currentFps));
            tvAvgFps.setText(String.format("Avg: %d", avgFps));
            tvFrameTime.setText(String.format("%.1f ms", frameTime));
            
            // Color coding based on FPS
            int color;
            if (currentFps >= 55) {
                color = Color.GREEN;
            } else if (currentFps >= 30) {
                color = Color.YELLOW;
            } else {
                color = Color.RED;
            }
            tvCurrentFps.setTextColor(color);
        } else {
            tvCurrentFps.setText("-- FPS");
            tvAvgFps.setText("No Data");
            tvFrameTime.setText("-- ms");
            tvCurrentFps.setTextColor(Color.GRAY);
        }
    }

    public void updatePosition(int x, int y) {
        params.x = x;
        params.y = y;
        if (isShowing) {
            windowManager.updateViewLayout(overlayView, params);
        }
        
        // Save to preferences
        preferences.edit()
                .putInt("overlay_x", x)
                .putInt("overlay_y", y)
                .apply();
    }

    public void updateTheme() {
        applyTheme();
        if (isShowing) {
            windowManager.updateViewLayout(overlayView, params);
        }
    }

    public void destroy() {
        hide();
    }

    public boolean isShowing() {
        return isShowing;
    }
}
