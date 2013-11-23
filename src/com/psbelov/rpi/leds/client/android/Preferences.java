package com.psbelov.rpi.leds.client.android;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String PREF_NAME = "com.psbelov.rpi.leds.client.android.preference.name";
    private static final String PREF_PORT = "com.psbelov.rpi.leds.client.android.preference.port";
    private static final String PREF_PASS = "com.psbelov.rpi.leds.client.android.preference.pass";

    public static String getName(Context context) {
        SharedPreferences pref = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String result = pref.getString(PREF_NAME, "");
        return result;
    }

    public static void setName(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        pref.edit().putString(PREF_NAME, value).commit();
    }

    public static int getPort(Context context) {
        SharedPreferences pref = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        int result = pref.getInt(PREF_PORT, -1);
        return result;
    }

    public static void setPort(Context context, int value) {
        SharedPreferences pref = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        pref.edit().putInt(PREF_PORT, value).commit();
    }

    public static String getPass(Context context) {
        SharedPreferences pref = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String result = pref.getString(PREF_PASS, "");
        return result;
    }

    public static void setPass(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        pref.edit().putString(PREF_PASS, value).commit();
    }
}
