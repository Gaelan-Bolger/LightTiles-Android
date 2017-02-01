package day.cloudy.apps.tiles.utils;

import android.content.Context;
import android.provider.Settings;

import com.scottyab.rootbeer.RootBeer;

import day.cloudy.apps.tiles.model.TileComponent;
import eu.chainfire.libsuperuser.Shell;
import timber.log.Timber;

/**
 * Created by Gaelan Bolger on 12/29/2016.
 */
public class RootHelper {

    private static RootHelper mInstance;
    private RootBeer mRootBeer;

    private RootHelper(Context context) {
        mRootBeer = new RootBeer(context);
        mRootBeer.setLogging(false);
    }

    public static RootHelper get(Context context) {
        if (null == mInstance)
            mInstance = new RootHelper(context);
        return mInstance;
    }

    private boolean isRooted() {
        return mRootBeer.isRooted();
    }

    /**
     * Checks if device is rooted without showing a su prompt
     *
     * @param context context
     * @return rooted
     */
    public static boolean isRooted(Context context) {
        return get(context).isRooted();
    }

    /**
     * Request root permission on a background thread
     */
    public static void requestRootPermission() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Shell.SU.available();
            }
        }).start();
    }

    /**
     * Add a QS tile on the current thread
     *
     * @param context       context
     * @param tileComponent tileComponent
     */
    public static void addQsTile(Context context, TileComponent tileComponent) {
        String serviceName = tileComponent.getServiceClass().getSimpleName();
        String qs = getSysUiQsTiles(context);
        if (qs.contains("day.cloudy.apps.tiles/.service." + serviceName))
            return;

        if (!isRooted(context))
            return;

        Shell.SU.run("settings put secure sysui_qs_tiles " + "\"" + qs +
                ",custom(day.cloudy.apps.tiles/.service." + serviceName + ")\"");
        Timber.d("addQsTile: Added QS Tile: %s", tileComponent.getKey());
    }

    /**
     * Remove a QS tile on the current thread
     *
     * @param context       context
     * @param tileComponent tileComponent
     */
    public static void removeQsTile(Context context, TileComponent tileComponent) {
        String serviceName = tileComponent.getServiceClass().getSimpleName();
        String qs = getSysUiQsTiles(context);
        if (!qs.contains("day.cloudy.apps.tiles/.service." + serviceName))
            return;

        if (!isRooted(context))
            return;

        StringBuilder sb = new StringBuilder("settings put secure sysui_qs_tiles \"");
        for (String tile : qs.split(","))
            if (!tile.contains("day.cloudy.apps.tiles/.service." + serviceName))
                sb.append(tile).append(",");
        sb.append("\"");

        Shell.SU.run(sb.toString());
        Timber.d("removeQsTile: Removed QS Tile: %s", tileComponent.getKey());
    }

    /**
     * Remove all QS tiles on the current thread
     *
     * @param context context
     */
    public static void removeAllQsTiles(Context context) {
        if (!isRooted(context))
            return;

        String qs = getSysUiQsTiles(context);
        StringBuilder sb = new StringBuilder("settings put secure sysui_qs_tiles \"");
        for (String tile : qs.split(","))
            if (!tile.contains("day.cloudy.apps.tiles/.service."))
                sb.append(tile).append(",");
        sb.append("\"");

        Shell.SU.run(sb.toString());
        Timber.d("removeAllQsTiles: Removed all QS Tiles");
    }

    /**
     * Remove all QS tiles on a background thread
     *
     * @param context context
     */
    public static void removeAllQsTilesBackground(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                removeAllQsTiles(context);
            }
        }).start();
    }

    private static String getSysUiQsTiles(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "sysui_qs_tiles");
    }
}
