package com.example.smartpantrymanager.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Reads and writes user settings using SharedPreferences.
 * Settings are simple key-value pairs, so a database table is not needed.
 */
public final class Prefs {

    private static final String FILE = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_WARNING_DAYS = "warning_days";

    private Prefs() { }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean isExpiryAlertsEnabled(Context context) {
        return prefs(context).getBoolean(KEY_EXPIRY_ALERTS, true);   // on by default
    }

    public static void setExpiryAlertsEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    public static int getWarningDays(Context context) {
        return prefs(context).getInt(KEY_WARNING_DAYS, 3);           // 3 days by default
    }

    public static void setWarningDays(Context context, int days) {
        prefs(context).edit().putInt(KEY_WARNING_DAYS, days).apply();
    }
}
