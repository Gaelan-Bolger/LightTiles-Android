package day.cloudy.apps.tiles.settings;

import android.content.Context;
import android.util.AttributeSet;

import day.cloudy.apps.tiles.R;

/**
 * Created by Gaelan Bolger on 12/29/2016.
 */
public class PreferenceCategory extends android.support.v7.preference.PreferenceCategory {

    public PreferenceCategory(Context context) {
        this(context, null);
    }

    public PreferenceCategory(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_category_layout);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }
}
