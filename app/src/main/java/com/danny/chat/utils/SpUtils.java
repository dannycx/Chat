package com.danny.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by danny on 3/23/18.
 */

public class SpUtils {
    private static final String PATH = "video_call";

    public static void put(Context context, String key, Object obj) {
        SharedPreferences sp = context.getSharedPreferences(PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (obj instanceof String) {
            editor.putString(key, (String) obj);
        } else if (obj instanceof Integer) {
            editor.putInt(key, (Integer) obj);
        } else if (obj instanceof Boolean) {
            editor.putBoolean(key, (Boolean) obj);
        } else if (obj instanceof Float) {
            editor.putFloat(key, (Float) obj);
        } else if (obj instanceof Long) {
            editor.putLong(key, (Long) obj);
        }
        editor.commit();
    }

    public static Object get(Context context, String key, Object def) {
        SharedPreferences sp = context.getSharedPreferences(PATH, Context.MODE_PRIVATE);
        if (def instanceof String) {
            return sp.getString(key, (String) def);
        } else if (def instanceof Integer) {
            return sp.getInt(key, (Integer) def);
        } else if (def instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) def);
        } else if (def instanceof Float) {
            return sp.getFloat(key, (Float) def);
        } else if (def instanceof Long) {
            return sp.getLong(key, (Long) def);
        }
        return null;
    }
}
