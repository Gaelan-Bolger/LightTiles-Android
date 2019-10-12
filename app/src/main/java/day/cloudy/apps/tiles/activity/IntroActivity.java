package day.cloudy.apps.tiles.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.paolorotolo.appintro.AppIntro2;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.utilities.impl.PHLog;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

import java.util.List;

import day.cloudy.apps.tiles.R;
import day.cloudy.apps.tiles.activity.slide.AccessPointSlide;
import day.cloudy.apps.tiles.activity.slide.BridgeLinkSlide;
import day.cloudy.apps.tiles.activity.slide.GenericSlide;
import day.cloudy.apps.tiles.settings.AppPrefs;
import timber.log.Timber;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 * Hue bridge selection wizard
 */
public class IntroActivity extends AppIntro2 implements AccessPointSlide.OnAccessPointSelectedListener {

    private AppPrefs mPrefs;
    private boolean mLastSearchWasIPScan;
    private PHHueSDK mHueSdk;
    private HueListener mSdkListener = new HueListener();

    private AccessPointSlide mAccessPointSlide;
    private BridgeLinkSlide mBridgeLinkSlide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = AppPrefs.getInstance(this);
        mHueSdk = PHHueSDK.getInstance();

        PHLog.setSdkLogLevel(PHLog.SUPPRESS);

        setWizardMode(true);
        showSkipButton(false);
        addSlide(GenericSlide.newInstance(getString(R.string.app_name), getString(R.string.slide_welcome_blurb), R.drawable.slide_welcome));
        addSlide(GenericSlide.newInstance(getString(R.string.slide_add_to_qs_title), getString(R.string.slide_add_to_qs_blurb), R.drawable.slide_add_to_qs));
        addSlide(mAccessPointSlide = AccessPointSlide.newInstance());
        addSlide(mBridgeLinkSlide = BridgeLinkSlide.newInstance());
        addSlide(GenericSlide.newInstance(getString(R.string.slide_ready_title), getString(R.string.slide_ready_blurb), R.drawable.ic_check_circle_white_48px_animated));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mController.selectPosition(0);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int prevPage;

            @Override
            public void onPageSelected(int newPage) {
                List<Fragment> slides = getSlides();
                Fragment currentSlide = slides.get(newPage);
                if (currentSlide instanceof GenericSlide)
                    ((GenericSlide) currentSlide).startAnimations();

                if (newPage == 2 && prevPage == 1) {
                    mAccessPointSlide.doBridgeSearch();
                } else if (prevPage == 3) {
                    mHueSdk.stopPushlinkAuthentication();
                    mBridgeLinkSlide.stopTimer();
                }

                prevPage = newPage;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHueSdk.getNotificationManager().registerSDKListener(mSdkListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHueSdk.getNotificationManager().unregisterSDKListener(mSdkListener);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        startMainActivity();
    }

    @Override
    public void onAccessPointSelected(PHAccessPoint accessPoint) {
        PHBridge connectedBridge = mHueSdk.getSelectedBridge();
        if (connectedBridge != null) {
            PHBridgeResourcesCache resourceCache = connectedBridge.getResourceCache();
            PHBridgeConfiguration configuration = resourceCache.getBridgeConfiguration();
            String connectedIP = configuration.getIpAddress();
            if (connectedIP != null) {
                mHueSdk.disableHeartbeat(connectedBridge);
                mHueSdk.disconnect(connectedBridge);
            }
        }
        mHueSdk.connect(accessPoint);
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void goToSlide(int slide) {
        getPager().setCurrentItem(slide);
    }

    private void goBack() {
        getPager().setCurrentItem(getPager().getCurrentItem() - 1);
    }

    private void goNext() {
        getPager().setCurrentItem(getPager().getCurrentItem() + 1);
    }

    private class HueListener implements PHSDKListener {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> list) {
            if (null != list && list.size() > 0) {
                mHueSdk.getAccessPointsFound().clear();
                mHueSdk.getAccessPointsFound().addAll(list);

                runOnUiThread(() -> mAccessPointSlide.setAccessPoints(mHueSdk.getAccessPointsFound()));
            }
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            Timber.d("onAuthenticationRequired: ");
            mHueSdk.startPushlinkAuthentication(accessPoint);

            runOnUiThread(() -> {
                goToSlide(3);

                mBridgeLinkSlide.startTimer(() -> {
                    mHueSdk.stopPushlinkAuthentication();
                    mBridgeLinkSlide.stopTimer();

                    runOnUiThread(new BackStepRunnable(getString(R.string.operation_timed_out)));
                });
            });
        }

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {
            Timber.d("onBridgeConnected: ");
            PHBridgeResourcesCache resourceCache = bridge.getResourceCache();
            PHBridgeConfiguration bridgeConfiguration = resourceCache.getBridgeConfiguration();
            String ipAddress = bridgeConfiguration.getIpAddress();
            String macAddress = bridgeConfiguration.getMacAddress();

            mHueSdk.stopPushlinkAuthentication();
            mHueSdk.setSelectedBridge(bridge);
            mHueSdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
            mHueSdk.getLastHeartbeat().put(ipAddress, System.currentTimeMillis());

            mPrefs.setLastConnectedIPAddress(ipAddress);
            mPrefs.setLastConnectedMacAddress(macAddress);
            mPrefs.setUsername(username);

            runOnUiThread(new NextStepRunnable());
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            if (IntroActivity.this.isFinishing())
                return;
            String ipAddress = bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
            mHueSdk.getLastHeartbeat().put(ipAddress, System.currentTimeMillis());
            for (PHAccessPoint accessPoint : mHueSdk.getDisconnectedAccessPoint())
                if (accessPoint.getIpAddress().equals(ipAddress))
                    mHueSdk.getDisconnectedAccessPoint().remove(accessPoint);
        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            if (!mHueSdk.getDisconnectedAccessPoint().contains(accessPoint))
                mHueSdk.getDisconnectedAccessPoint().add(accessPoint);
        }

        @Override
        public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
            Timber.d("onCacheUpdated: ");
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> list) {
            Timber.d("onParsingErrors: ");
        }

        @Override
        public void onError(int code, final String message) {
            switch (code) {
                case PHHueError.NO_CONNECTION:
                    Timber.w("onError: No connection");
                    break;
                case PHHueError.AUTHENTICATION_FAILED:
                case PHMessageType.PUSHLINK_AUTHENTICATION_FAILED:
                    Timber.w("onError: Authentication failed ...");
                    mHueSdk.stopPushlinkAuthentication();
                    runOnUiThread(new BackStepRunnable(getString(R.string.authentication_failed)));
                    break;
                case PHHueError.BRIDGE_NOT_RESPONDING:
                    Timber.w("onError: Bridge not responding ...");
                    runOnUiThread(new BackStepRunnable(getString(R.string.bridge_not_responding)));
                    break;
                case PHMessageType.BRIDGE_NOT_FOUND:
                    if (!mLastSearchWasIPScan) {
                        mHueSdk = PHHueSDK.getInstance();
                        PHBridgeSearchManager sm = (PHBridgeSearchManager) mHueSdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                        sm.search(false, false, true);
                        mLastSearchWasIPScan = true;
                    } else {
                        Timber.w("onError: Bridge not found ...");
                        runOnUiThread(new BackStepRunnable(getString(R.string.bridge_not_found)));
                    }
                    break;
            }
        }
    }

    private class NextStepRunnable implements Runnable {

        @Override
        public void run() {
            if (pager.getCurrentItem() < mPagerAdapter.getCount())
                goNext();
        }

    }

    private class BackStepRunnable implements Runnable {

        private final String text;

        BackStepRunnable(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            Toast.makeText(IntroActivity.this, text, Toast.LENGTH_SHORT).show();
            if (pager.getCurrentItem() > 1)
                goBack();
        }
    }
}
