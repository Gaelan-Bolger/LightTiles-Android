package day.cloudy.apps.tiles.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public class ColorUtils {

    private static Random mRandom = new Random();

    public static int getRandomColor() {
        return Color.rgb(mRandom.nextInt(254), mRandom.nextInt(254), mRandom.nextInt(254));
    }
}
