package tmedia.ir.melkeurmia.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

/**
 * Created by tmedia on 7/17/2017.
 */

public class AppSharedPref {
    private static SharedPreferences mSharedPref;
    private static SharedPreferences.Editor prefsEditor;

    private AppSharedPref() {

    }

    public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            prefsEditor = mSharedPref.edit();

    }

    public static boolean check(String key) {
        boolean is_found = mSharedPref.contains(key);
        return is_found;
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }

    public static boolean saveArray(JsonArray items, String arrayName) {
        Gson gson = new Gson();
        String json = gson.toJson(items);
        prefsEditor.putString(arrayName, json);
        return  prefsEditor.commit();
    }

    public static JsonArray loadArray(String arrayName) {
        Gson gson = new Gson();
        String json = mSharedPref.getString(arrayName, "");
        JsonArray items = gson.fromJson(json, JsonArray.class);
        return items;
    }

}