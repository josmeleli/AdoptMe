package com.example.adoptmev5.ui.menu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.CheckBox;

public class AccessibilityHelper {

    private static final String PREFS_NAME = "accessibility_prefs";
    private static final String KEY_LARGE_TEXT = "large_text";
    private static final String KEY_HIGH_CONTRAST = "high_contrast";

    // Constantes para los tamaños de texto
    private static final int TEXT_SIZE_INCREASE = 10; // Aumentar 10sp como solicitaste
    private static final float MIN_TEXT_SIZE = 12f;
    private static final float BASE_TEXT_SIZE = 14f;

    /**
     * Aplica las configuraciones de accesibilidad a una actividad completa
     */
    public static void applyAccessibilitySettings(Activity activity) {
        if (activity == null) return;

        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean largeText = prefs.getBoolean(KEY_LARGE_TEXT, false);
        boolean highContrast = prefs.getBoolean(KEY_HIGH_CONTRAST, false);

        // Obtener la vista raíz de la actividad
        View rootView = activity.findViewById(android.R.id.content);
        if (rootView != null) {
            applySettingsToView(rootView, largeText, highContrast, activity);
        }
    }

    /**
     * Aplica las configuraciones de accesibilidad a una vista específica
     */
    public static void applyAccessibilitySettings(Context context, View view) {
        if (context == null || view == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean largeText = prefs.getBoolean(KEY_LARGE_TEXT, false);
        boolean highContrast = prefs.getBoolean(KEY_HIGH_CONTRAST, false);

        applySettingsToView(view, largeText, highContrast, context);
    }

    /**
     * Método principal que aplica recursivamente las configuraciones
     */
    private static void applySettingsToView(View view, boolean largeText, boolean highContrast, Context context) {
        if (view == null) return;

        // Aplicar configuraciones a TextView y sus subclases (Button, CheckBox, etc.)
        if (view instanceof TextView) {
            applyTextViewSettings((TextView) view, largeText, highContrast);
        }

        // Aplicar configuraciones a la vista contenedora
        applyViewSettings(view, highContrast);

        // Aplicar recursivamente a todas las vistas hijas
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                applySettingsToView(child, largeText, highContrast, context);
            }
        }
    }

    /**
     * Aplica configuraciones específicas a TextView
     */
    private static void applyTextViewSettings(TextView textView, boolean largeText, boolean highContrast) {
        // Configuración de texto grande y negrita
        if (largeText) {
            // Obtener el tamaño actual del texto
            float currentSize = textView.getTextSize();
            float currentSizeSp = currentSize / textView.getContext().getResources().getDisplayMetrics().scaledDensity;

            // Aumentar el tamaño en 10sp como solicitaste
            float newSize = currentSizeSp + TEXT_SIZE_INCREASE;
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);

            // Hacer todo el texto en NEGRITA
            Typeface currentTypeface = textView.getTypeface();
            if (currentTypeface != null) {
                textView.setTypeface(currentTypeface, Typeface.BOLD);
            } else {
                textView.setTypeface(Typeface.DEFAULT_BOLD);
            }
        } else {
            // Restaurar tamaño normal
            resetTextViewToNormal(textView);
        }

        // Configuración de contraste alto
        if (highContrast) {
            textView.setTextColor(0xFF000000); // Negro completo para máximo contraste
        } else {
            // Restaurar colores originales (puedes personalizar estos colores)
            restoreOriginalTextColor(textView);
        }
    }

    /**
     * Aplica configuraciones generales a la vista
     */
    private static void applyViewSettings(View view, boolean highContrast) {
        if (highContrast) {
            // Aplicar fondo de alto contraste si es necesario
            if (view.getBackground() != null) {
                view.setBackgroundColor(0xFFFFFFFF); // Fondo blanco para máximo contraste
            }
        }
    }

    /**
     * Restaura el TextView a su configuración normal
     */
    private static void resetTextViewToNormal(TextView textView) {
        // Obtener el tamaño actual
        float currentSize = textView.getTextSize();
        float currentSizeSp = currentSize / textView.getContext().getResources().getDisplayMetrics().scaledDensity;

        // Si el tamaño es mayor que el base + incremento, reducirlo
        if (currentSizeSp > BASE_TEXT_SIZE + TEXT_SIZE_INCREASE) {
            float normalSize = Math.max(currentSizeSp - TEXT_SIZE_INCREASE, MIN_TEXT_SIZE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, normalSize);
        }

        // Restaurar typeface normal
        textView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
    }

    /**
     * Restaura el color original del texto
     */
    private static void restoreOriginalTextColor(TextView textView) {
        // Colores por defecto según el tipo de vista
        if (textView instanceof Button) {
            textView.setTextColor(0xFF2196F3); // Azul para botones
        } else if (textView instanceof CheckBox) {
            textView.setTextColor(0xFF333333); // Gris oscuro para checkboxes
        } else {
            textView.setTextColor(0xFF333333); // Gris oscuro por defecto
        }
    }

    /**
     * Verifica si el texto grande está habilitado
     */
    public static boolean isLargeTextEnabled(Context context) {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_LARGE_TEXT, false);
    }

    /**
     * Verifica si el contraste alto está habilitado
     */
    public static boolean isHighContrastEnabled(Context context) {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_HIGH_CONTRAST, false);
    }

    /**
     * Obtiene el incremento de tamaño de texto configurado
     */
    public static int getTextSizeIncrease() {
        return TEXT_SIZE_INCREASE;
    }

    /**
     * Aplica configuraciones específicas solo al texto grande
     */
    public static void applyLargeTextOnly(Context context, View view) {
        if (context == null || view == null) return;

        boolean largeText = isLargeTextEnabled(context);
        applySettingsToView(view, largeText, false, context);
    }

    /**
     * Aplica configuraciones específicas solo al contraste alto
     */
    public static void applyHighContrastOnly(Context context, View view) {
        if (context == null || view == null) return;

        boolean highContrast = isHighContrastEnabled(context);
        applySettingsToView(view, false, highContrast, context);
    }
}