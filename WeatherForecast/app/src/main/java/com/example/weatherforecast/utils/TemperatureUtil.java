package com.example.weatherforecast.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class TemperatureUtil {
    public static String convertTemperature(Context context, double temperature) {
        SharedPreferences prefs = context.getSharedPreferences("WeatherlyPreferences", Context.MODE_PRIVATE);
        String unit = prefs.getString("temperatureUnit", "K");

        if ("C".equals(unit)) {
            return String.format(Locale.US, "%.1f C", temperature - 273.15);
        } else if ("F".equals(unit)) {
            return String.format(Locale.US, "%.1f F", (temperature - 273.15) * 9 / 5 + 32);
        } else {
            return String.format(Locale.US, "%.1f K", temperature);
        }
    }
}
