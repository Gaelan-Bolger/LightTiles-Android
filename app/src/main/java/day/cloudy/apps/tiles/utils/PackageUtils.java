package day.cloudy.apps.tiles.utils;

import android.content.ComponentName;
import android.content.pm.PackageManager;

/**
 * Created by Gaelan Bolger on 12/24/2016.
 */
public class PackageUtils {

    public static boolean isComponentEnabled(PackageManager pm, ComponentName cn) {
        int setting = pm.getComponentEnabledSetting(cn);
        return setting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    public static void setComponentEnabled(PackageManager pm, ComponentName cn, boolean enabled) {
        int newState = enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        pm.setComponentEnabledSetting(cn, newState, PackageManager.DONT_KILL_APP);
    }
}
