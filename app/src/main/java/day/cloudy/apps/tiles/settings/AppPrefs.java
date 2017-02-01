package day.cloudy.apps.tiles.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import day.cloudy.apps.tiles.model.LightTile;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public class AppPrefs {

    private static final String LAST_CONNECTED_USERNAME = "last_connected_username";
    private static final String LAST_CONNECTED_IP = "last_connected_ip";
    private static final String LAST_CONNECTED_MAC = "last_connected_mac";

    public static final String KEY_FORGET_BRIDGE = "forget_bridge";
    public static final String KEY_AUTO_ADD_TILES = "auto_add_tiles";
    public static final String KEY_AUTO_REMOVE_TILES = "auto_remove_tiles";
    public static final String KEY_DEBUG_DRAWER_ENABLED = "debug_drawer_enabled";

    private static AppPrefs instance = null;

    private SharedPreferences mSharedPreferences = null;
    private Gson mGson = new Gson();

    public static AppPrefs getInstance(Context ctx) {
        if (instance == null) {
            instance = new AppPrefs(ctx);
        }
        return instance;
    }

    private AppPrefs(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean putBoolean(String key, boolean value) {
        return mSharedPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return mSharedPreferences.getBoolean(key, defaultVal);
    }

    public String getUsername() {
        return mSharedPreferences.getString(LAST_CONNECTED_USERNAME, "");
    }

    public boolean setUsername(String username) {
        return mSharedPreferences.edit().putString(LAST_CONNECTED_USERNAME, username).commit();
    }

    public String getLastConnectedIPAddress() {
        return mSharedPreferences.getString(LAST_CONNECTED_IP, "");
    }

    public boolean setLastConnectedIPAddress(String ipAddress) {
        return mSharedPreferences.edit().putString(LAST_CONNECTED_IP, ipAddress).commit();
    }

    public boolean setLastConnectedMacAddress(String macAddress) {
        return mSharedPreferences.edit().putString(LAST_CONNECTED_MAC, macAddress).commit();
    }

    public boolean putLightTile(String tileKey, LightTile lightTile) {
        String json = mGson.toJson(lightTile);
        return mSharedPreferences.edit().putString(tileKey, json).commit();
    }

    public boolean removeLightTile(String key) {
        return mSharedPreferences.edit().putString(key, "").commit();
    }

    public LightTile getLightTile(String tileKey) {
        String json = mSharedPreferences.getString(tileKey, "");
        if (TextUtils.isEmpty(json))
            return null;
        return mGson.fromJson(json, LightTile.class);
    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
