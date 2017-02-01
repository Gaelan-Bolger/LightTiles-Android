package day.cloudy.apps.tiles.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHBridgeResourcesCache;

import day.cloudy.apps.tiles.hue.HueSdkListener;
import day.cloudy.apps.tiles.settings.AppPrefs;
import timber.log.Timber;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public class LaunchActivity extends Activity {

    private AppPrefs mPrefs;
    private PHHueSDK mHueSdk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHueSdk = PHHueSDK.getInstance();
        mPrefs = AppPrefs.getInstance(this);
        String ipAddress = mPrefs.getLastConnectedIPAddress();
        String username = mPrefs.getUsername();
        if (!TextUtils.isEmpty(ipAddress) && !TextUtils.isEmpty(username)) {
            PHAccessPoint accessPoint = new PHAccessPoint();
            accessPoint.setIpAddress(ipAddress);
            accessPoint.setUsername(username);
            if (mHueSdk.isAccessPointConnected(accessPoint)) {
                Timber.d("onCreate: Access point connected");
                startMainActivity();
            } else {
                Timber.d("onCreate: Access point not connected");
                tryReconnect(accessPoint);
            }
        } else {
            Timber.d("onCreate: Access point unknown");
            startIntroActivity();
        }
    }

    private void tryReconnect(PHAccessPoint accessPoint) {
        mHueSdk.getNotificationManager().registerSDKListener(new HueSdkListener() {

            @Override
            public void onBridgeConnected(PHBridge bridge, String username) {
                Timber.d("onBridgeConnected: ");
                mHueSdk.getNotificationManager().unregisterSDKListener(this);

                PHBridgeResourcesCache resourceCache = bridge.getResourceCache();
                PHBridgeConfiguration configuration = resourceCache.getBridgeConfiguration();
                String ipAddress = configuration.getIpAddress();
                String macAddress = configuration.getMacAddress();

                mHueSdk.setSelectedBridge(bridge);
                mHueSdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
                mHueSdk.getLastHeartbeat().put(ipAddress, System.currentTimeMillis());

                mPrefs.setLastConnectedIPAddress(ipAddress);
                mPrefs.setLastConnectedMacAddress(macAddress);
                mPrefs.setUsername(username);

                startMainActivity();
            }

            @Override
            public void onError(int code, final String message) {
                Timber.e("onError: %s", message);
                mHueSdk.getNotificationManager().unregisterSDKListener(this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LaunchActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
                startIntroActivity();
            }
        });
        mHueSdk.connect(accessPoint);
    }

    private void startIntroActivity() {
        startActivity(new Intent(this, IntroActivity.class));
        finish();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
