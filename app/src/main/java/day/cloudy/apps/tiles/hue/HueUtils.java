package day.cloudy.apps.tiles.hue;

import day.cloudy.apps.tiles.R;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 * Utilities related to Philips Hue
 */
public class HueUtils {

    public static int getLightIconResources(String modelNumber) {
        switch (modelNumber) {
            case "LCT002":
                return R.drawable.ic_phlight_br30;
            case "LCT011":
            case "LTW011":
                return R.drawable.ic_phlight_br30_slim;
            case "LCT001":
            case "LCT007":
            case "LCT010":
            case "LCT014":
            case "LTW010":
            case "LTW001":
            case "LTW004":
            case "LTW015":
            case "LWB004":
            case "LWB006":
                return R.drawable.ic_phlight_white_and_color_e27_b22;
            case "LWB010":
            case "LWB014":
            default:
                return R.drawable.ic_phlight_white_e27_b22;
        }
    }
}
