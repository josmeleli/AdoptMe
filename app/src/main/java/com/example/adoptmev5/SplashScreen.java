package com.example.adoptmev5;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView logo;
    private Handler handler;
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        progressBar = findViewById(R.id.progressBar_carga);
        logo = findViewById(R.id.imageView_encabezado_completo);
        handler = new Handler();

        // Animación del logo
        startLogoAnimation();

        // Iniciar animación de la barra de progreso
        startProgressBarAnimation();
    }

    private void startLogoAnimation() {
        // Animación de fade in para el logo
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeIn.setDuration(1000);
        logo.startAnimation(fadeIn);
    }

    private void startProgressBarAnimation() {
        // Thread para actualizar la barra de progreso
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;

                    // Actualizar la barra de progreso en el hilo principal
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });

                    try {
                        // Controla la velocidad del progreso (30ms = rápido y fluido)
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Cuando termine la carga, ir a LoginActivity
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Pequeña pausa antes de cambiar de actividad
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                                startActivity(intent);
                                finish(); // Cerrar el splash screen
                            }
                        }, 300);
                    }
                });

            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}