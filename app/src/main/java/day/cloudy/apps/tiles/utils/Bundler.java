package day.cloudy.apps.tiles.utils;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Gaelan on 8/25/2016.
 * {@link Bundle} creater with a builder pattern
 */
public class Bundler {

    private final Bundle bundle;

    public Bundler() {
        bundle = new Bundle();
    }

    public Bundler with(String key, boolean val) {
        bundle.putBoolean(key, val);
        return this;
    }

    public Bundler with(String key, int val) {
        bundle.putInt(key, val);
        return this;
    }

    public Bundler with(String key, String val) {
        bundle.putString(key, val);
        return this;
    }

    public Bundler with(String key, String[] val) {
        bundle.putStringArray(key, val);
        return this;
    }

    public Bundler with(String key, ArrayList<String> val) {
        bundle.putStringArrayList(key, val);
        return this;
    }

    public Bundle bundle() {
        return bundle;
    }
}