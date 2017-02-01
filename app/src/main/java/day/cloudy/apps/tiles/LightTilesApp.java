package day.cloudy.apps.tiles;

import android.app.Application;
import android.util.Log;

import com.philips.lighting.hue.sdk.PHHueSDK;

import io.palaima.debugdrawer.timber.data.LumberYard;
import timber.log.Timber;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public class LightTilesApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            LumberYard lumberYard = LumberYard.getInstance(this);
            lumberYard.cleanUp();
            Timber.plant(lumberYard.tree());
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }

        PHHueSDK hueSDK = PHHueSDK.create();
        hueSDK.setAppName(getString(R.string.app_name));
        hueSDK.setDeviceName(android.os.Build.MODEL);
    }

    class ReleaseTree extends Timber.DebugTree {

        @Override
        protected boolean isLoggable(int priority) {
            return priority == Log.ERROR || priority == Log.WARN;
        }
    }

}
