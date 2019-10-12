package day.cloudy.apps.tiles.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;

import day.cloudy.apps.tiles.R;

/**
 * Created by Gaelan Bolger on 12/27/2016.
 */
public class ResourceUtils {

    public static String getResourceName(Context context, int id) {
        return getResources(context).getResourceEntryName(id);
    }

    @DrawableRes
    public static int getDrawableIdForName(Context context, String name) {
        return getResources(context).getIdentifier(name, "drawable", context.getPackageName());
    }

    public static Drawable getDrawableForName(Context context, String name) {
        return getDrawable(context, getDrawableIdForName(context, name));
    }

    public static Drawable getDrawable(Context context, int id) {
        return getResources(context).getDrawable(id, null);
    }

    public static int[] getTileIconIds(Context context) {
        String[] iconNames = getResources(context).getStringArray(R.array.tile_icon_names);
        int[] iconResIds = new int[iconNames.length];
        for (int i = 0; i < iconNames.length; i++) {
            String name = iconNames[i];
            iconResIds[i] = getDrawableIdForName(context, name);
        }
        return iconResIds;
    }

    private static Resources getResources(Context context) {
        return context.getResources();
    }
}
