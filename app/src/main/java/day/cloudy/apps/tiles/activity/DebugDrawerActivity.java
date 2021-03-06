package day.cloudy.apps.tiles.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.wooplr.spotlight.prefs.PreferencesManager;

import day.cloudy.apps.tiles.BuildConfig;
import day.cloudy.apps.tiles.settings.AppPrefs;
import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.actions.ActionsModule;
import io.palaima.debugdrawer.actions.ButtonAction;
import io.palaima.debugdrawer.base.DebugModule;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.NetworkModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.location.LocationModule;
import io.palaima.debugdrawer.scalpel.ScalpelModule;
import io.palaima.debugdrawer.timber.TimberModule;

/**
 * Created by Gaelan Bolger on 9/10/2016.
 * <p>
 * Right side {@link DebugDrawer} in debug builds only,
 * for accessing application settings, device state and settings,
 * and {@link timber.log.Timber} logs
 * </p>
 */
public abstract class DebugDrawerActivity extends AppCompatActivity {

    private DebugDrawer mDebugDrawer;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (isDrawerEnabled()) {
            initDebugDrawer();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != mDebugDrawer)
            mDebugDrawer.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mDebugDrawer)
            mDebugDrawer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mDebugDrawer)
            mDebugDrawer.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mDebugDrawer)
            mDebugDrawer.onStop();
    }

    @Override
    public void onBackPressed() {
        if (null != mDebugDrawer && mDebugDrawer.isDrawerOpen()) {
            mDebugDrawer.closeDrawer();
            return;
        }
        super.onBackPressed();
    }

    /**
     * Initiate the debug drawer
     */
    private void initDebugDrawer() {
        mDebugDrawer = new DebugDrawer.Builder(this)
                .modules(getDebugModules())
                .build();
    }

    /**
     * Get modules, if location permission has been granted location and network modules are included
     *
     * @return an array of debug modules
     */
    @NonNull
    private DebugModule[] getDebugModules() {
        ButtonAction finishAction = new ButtonAction("Finish", new ButtonAction.Listener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        ButtonAction resetSpotlightsAction = new ButtonAction("Reset Spotlight", new ButtonAction.Listener() {
            @Override
            public void onClick() {
                new PreferencesManager(DebugDrawerActivity.this).resetAll();
                Toast.makeText(DebugDrawerActivity.this, "Spotlights reset", Toast.LENGTH_SHORT).show();
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return new DebugModule[]{new ActionsModule(finishAction, resetSpotlightsAction), new TimberModule(), new ScalpelModule(this),
                    new DeviceModule(this), new BuildModule(this), new SettingsModule(this),
                    new LocationModule(this), new NetworkModule(this)};
        return new DebugModule[]{new ActionsModule(finishAction, resetSpotlightsAction), new TimberModule(), new ScalpelModule(this),
                new DeviceModule(this), new BuildModule(this), new SettingsModule(this)};
    }

    /**
     * Whether or not to show the debug drawer, should be disabled in production
     *
     * @return only allowed in debug builds
     */
    private boolean isDrawerEnabled() {
        return BuildConfig.DEBUG && AppPrefs.getInstance(this).getBoolean(AppPrefs.KEY_DEBUG_DRAWER_ENABLED, false);
    }

}