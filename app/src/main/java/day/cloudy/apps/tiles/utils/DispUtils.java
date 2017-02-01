package day.cloudy.apps.tiles.utils;

import android.content.res.Resources;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public class DispUtils {

    public static int dp(int i) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * i);
    }
}
