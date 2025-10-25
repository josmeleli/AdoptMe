package com.example.adoptmev5;

import android.app.Application;

import com.example.adoptmev5.ui.menu.ThemeActivity;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 🔥 Aplicar tema global al iniciar la app
        ThemeActivity.applyStoredThemeGlobal(this);
    }
}
