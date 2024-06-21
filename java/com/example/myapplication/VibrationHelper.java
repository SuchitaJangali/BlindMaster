package com.example.myapplication;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationHelper {
    // Method to vibrate the phone for a specified duration
    public static void vibrate(Context context, long milliseconds) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Deprecated in API 26 (Oreo)
                vibrator.vibrate(milliseconds);
            }
        }
    }

    // Method to vibrate the phone with a pattern
    public static void vibratePattern(Context context, long[] pattern, int repeat) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat));
            } else {
                // Deprecated in API 26 (Oreo)
                vibrator.vibrate(pattern, repeat);
            }
        }
    }
}
