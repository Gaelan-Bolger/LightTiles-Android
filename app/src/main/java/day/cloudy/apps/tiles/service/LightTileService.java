package day.cloudy.apps.tiles.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.text.TextUtils;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.exception.PHHueException;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.ArrayList;
import java.util.List;

import day.cloudy.apps.tiles.R;
import day.cloudy.apps.tiles.activity.LaunchActivity;
import day.cloudy.apps.tiles.hue.HueLightListener;
import day.cloudy.apps.tiles.hue.HueSdkListener;
import day.cloudy.apps.tiles.model.LightModel;
import day.cloudy.apps.tiles.model.LightTile;
import day.cloudy.apps.tiles.model.TileComponent;
import day.cloudy.apps.tiles.settings.AppPrefs;
import day.cloudy.apps.tiles.utils.Bundler;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static day.cloudy.apps.tiles.utils.ResourceUtils.getDrawableIdForName;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 * Base Hue Light TileService
 */
public abstract class LightTileService extends TileService {

    private static LightTileService mService;
    private static AppPrefs mPrefs;
    private static PHHueSDK mHueSDK;
    private static PHBridge mHueBridge;
    private static boolean mHueConnecting;
    private static boolean mWifiConnected;
    private static BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("onReceive: Wifi state changed");
            ConnectivityManager manager = (ConnectivityManager) mService.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            mWifiConnected = null != info && info.getType() == ConnectivityManager.TYPE_WIFI;
        }
    };

    private LightTile mLightTile;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("onCreate: ");
        mService = this;
        mPrefs = AppPrefs.getInstance(this);
        mLightTile = mPrefs.getLightTile(getTileComponent().getKey());
        mHueSDK = PHHueSDK.getInstance();
        mHueBridge = mHueSDK.getSelectedBridge();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Timber.d("onTileAdded: ");
        updateTileViews();
        updateTileState();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Timber.d("onTileRemoved: ");
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Timber.d("onStartListening: ");
        registerWifiReceiver();

        mLightTile = mPrefs.getLightTile(getTileComponent().getKey());
        updateTileViews();

        mHueBridge = mHueSDK.getSelectedBridge();
        if (null == mHueBridge)
            tryReconnect();
        else
            updateTileState();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        Timber.d("onStopListening: ");
        unregisterWifiListener();
    }

    @Override
    public void onClick() {
        super.onClick();
        Timber.d("onClick: ");
        if (null == mHueBridge) {
            Timber.d("onClick: PHBridge == null");
            tryReconnect();
            return;
        }
        if (null == mLightTile) {
            Timber.d("onClick: LightTile == null");
            showTileNotInitializedDialog();
            return;
        }

        List<PHLight> targetLights = getTargetLights();
        PHLightState state = new PHLightState();
        state.setOn(getDominateOnOffState(targetLights) < 0);
        GroupUpdateListener listener = new GroupUpdateListener(targetLights.size());
        for (PHLight light : targetLights)
            mHueBridge.updateLightState(light, state, listener);
    }

    private void tryReconnect() {
        Timber.d("tryReconnect: ");
        if (mHueConnecting)
            return;

        String ipAddress = mPrefs.getLastConnectedIPAddress();
        String username = mPrefs.getUsername();
        if (!TextUtils.isEmpty(ipAddress) && !TextUtils.isEmpty(username)) {
            PHAccessPoint accessPoint = new PHAccessPoint();
            accessPoint.setIpAddress(ipAddress);
            accessPoint.setUsername(username);
            if (!mHueSDK.isAccessPointConnected(accessPoint)) {
                getObservableHueListenerWrapper()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<PHBridge>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e("onError: %s", e.getMessage());
                                mHueConnecting = false;
                                showConnectionFailedDialog();
                            }

                            @Override
                            public void onNext(PHBridge bridge) {
                                Timber.d("onNext: ");
                                mHueConnecting = false;
                                mHueSDK.setSelectedBridge(bridge);
                                mHueBridge = bridge;
                                updateTileState();
                            }
                        });
                mHueConnecting = true;
                mHueSDK.connect(accessPoint);
            } else {
                showConnectionFailedDialog();
            }
        } else {
            showConnectionFailedDialog();
        }
    }

    public Observable<PHBridge> getObservableHueListenerWrapper() {
        Timber.d("getObservableHueListenerWrapper: ");
        return Observable.create(new Observable.OnSubscribe<PHBridge>() {
            @Override
            public void call(final Subscriber<? super PHBridge> subscriber) {
                Timber.d("call: ");
                HueSdkListener listener = new HueSdkListener() {
                    @Override
                    public void onBridgeConnected(PHBridge phBridge, String s) {
                        Timber.d("onBridgeConnected: ");
                        mHueSDK.getNotificationManager().unregisterSDKListener(this);
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(phBridge);
                            subscriber.unsubscribe();
                        }
                    }

                    @Override
                    public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
                        Timber.d("onAuthenticationRequired: ");
                        mHueSDK.getNotificationManager().unregisterSDKListener(this);
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(new IllegalStateException("Authentication required"));
                            subscriber.unsubscribe();
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Timber.e("onError: %s", s);
                        mHueSDK.getNotificationManager().unregisterSDKListener(this);
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(new PHHueException(s));
                            subscriber.unsubscribe();
                        }
                    }
                };
                mHueSDK.getNotificationManager().registerSDKListener(listener);
            }
        });
    }

    @NonNull
    private List<PHLight> getTargetLights() {
        PHBridgeResourcesCache resourcesCache = mHueBridge.getResourceCache();
        List<PHLight> allLights = resourcesCache.getAllLights();
        List<LightModel> lightModels = mLightTile.getLightModels();
        List<PHLight> targetLights = new ArrayList<>();
        for (PHLight light : allLights)
            for (LightModel model : lightModels)
                if (light.getIdentifier().equals(model.getIdentifier()))
                    targetLights.add(light);
        return targetLights;
    }

    private int getDominateOnOffState(List<PHLight> lights) {
        int dominateState = 0;
        for (PHLight light : lights)
            if (light.getLastKnownLightState().isOn())
                dominateState++;
            else
                dominateState--;
        return dominateState;
    }

    private void updateTileViews() {
        Tile qsTile = getQsTile();
        if (null != qsTile) {
            qsTile.setIcon(Icon.createWithResource(this, null == mLightTile
                    ? R.drawable.ic_block_white_24dp : getDrawableIdForName(this, mLightTile.getIconName())));
            qsTile.setLabel(null == mLightTile
                    ? getString(getTileComponent().getTitleResId()) : mLightTile.getLabel());
            qsTile.updateTile();
        }
    }

    private void updateTileState() {
        Tile qsTile = getQsTile();
        if (null != qsTile) {
            int state = Tile.STATE_UNAVAILABLE;
            if (mWifiConnected && null != mLightTile && null != mHueBridge)
                state = getDominateOnOffState(getTargetLights()) > 0
                        ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;
            qsTile.setState(state);
            qsTile.updateTile();
        }
    }

    private void registerWifiReceiver() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mWifiReceiver, intentFilter);
    }

    private void unregisterWifiListener() {
        unregisterReceiver(mWifiReceiver);
    }

    private void showTileNotInitializedDialog() {
        try {
            showDialog(R.string.tile_not_initialized, R.string.confirm_initialize_tile_now);
        } catch (WindowManager.BadTokenException e) {
            Timber.w("showConnectionFailedDialog: %s", e.getMessage());
        }
    }

    private void showConnectionFailedDialog() {
        try {
            showDialog(R.string.bridge_not_connected, R.string.confirm_connect_bridge_now);
        } catch (WindowManager.BadTokenException e) {
            Timber.w("showConnectionFailedDialog: %s", e.getMessage());
        }
    }

    private void showDialog(@StringRes int title, @StringRes int message) {
        showDialog(new AlertDialog.Builder(LightTileService.this, R.style.AppAlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(LightTileService.this, LaunchActivity.class));
                    }
                })
                .create());
    }

    protected abstract TileComponent getTileComponent();

    class GroupUpdateListener extends HueLightListener {

        private final int numLights;
        private int respCount;

        GroupUpdateListener(int numLights) {
            this.numLights = numLights;
            this.respCount = 0;
        }

        @Override
        public void onSuccess() {
            Timber.d("onSuccess: ");
            respCount++;
            if (respCount >= numLights)
                updateTileState();
        }

        @Override
        public void onError(int i, String s) {
            Timber.e("onError: %s", s);
            updateTileState();
        }
    }
}
