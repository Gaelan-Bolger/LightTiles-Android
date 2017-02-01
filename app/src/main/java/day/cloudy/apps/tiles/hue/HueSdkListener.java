package day.cloudy.apps.tiles.hue;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

import java.util.List;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public class HueSdkListener implements PHSDKListener {
    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
    }

    @Override
    public void onBridgeConnected(PHBridge phBridge, String s) {
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list) {
    }

    @Override
    public void onError(int i, String s) {
    }

    @Override
    public void onConnectionResumed(PHBridge phBridge) {
    }

    @Override
    public void onConnectionLost(PHAccessPoint phAccessPoint) {
    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> list) {
    }
}
