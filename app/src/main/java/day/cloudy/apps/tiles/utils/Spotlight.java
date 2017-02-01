package day.cloudy.apps.tiles.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.wooplr.spotlight.SpotlightView;

import day.cloudy.apps.tiles.R;

/**
 * Created by Gaelan Bolger on 12/28/2016.
 */
public class Spotlight {

    private SpotlightView.Builder mBuilder;

    private Spotlight(Activity activity, View target, String heading) {
        mBuilder = new SpotlightView.Builder(activity)
                .setConfiguration(SpotlightConfig.get(activity))
                .target(target)
                .headingTvText(heading)
                .showTargetArc(false);
    }

    public static Spotlight shine(Activity activity, View target, String heading) {
        return new Spotlight(activity, target, heading);
    }

    public Spotlight subHeading(String subHeading) {
        mBuilder.subHeadingTvText(subHeading);
        return this;
    }

    public Spotlight usageId(String usageId) {
        mBuilder.usageId(usageId);
        return this;
    }

    public Spotlight performClick(boolean performClick) {
        mBuilder.performClick(performClick);
        return this;
    }

    public void show() {
        mBuilder.show();
    }

    private static class SpotlightConfig {

        public static com.wooplr.spotlight.SpotlightConfig get(Context context) {
            com.wooplr.spotlight.SpotlightConfig config = new com.wooplr.spotlight.SpotlightConfig();
            config.setRevealAnimationEnabled(true);
            config.setIntroAnimationDuration(400);
            config.setFadingTextDuration(400);
            config.setMaskColor(Color.parseColor("#cc000000"));
            config.setLineAnimationDuration(400);
            config.setLineStroke(4);
            config.setLineAndArcColor(context.getColor(R.color.colorPrimary));
            config.setHeadingTvColor(context.getColor(R.color.colorPrimary));
            config.setHeadingTvSize(28);
            config.setSubHeadingTvColor(Color.WHITE);
            config.setSubHeadingTvSize(16);
            config.setPerformClick(false);
            config.setDismissOnTouch(true);
            config.setDismissOnBackpress(true);
            config.setShowTargetArc(false);
            return config;
        }
    }
}
