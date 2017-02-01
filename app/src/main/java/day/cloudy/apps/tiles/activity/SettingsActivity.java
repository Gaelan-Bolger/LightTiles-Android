package day.cloudy.apps.tiles.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;

import butterknife.BindView;
import day.cloudy.apps.tiles.BuildConfig;
import day.cloudy.apps.tiles.R;
import day.cloudy.apps.tiles.dialog.ConfirmActionDialog;
import day.cloudy.apps.tiles.model.TileComponent;
import day.cloudy.apps.tiles.settings.AppPrefs;
import day.cloudy.apps.tiles.utils.PackageUtils;
import day.cloudy.apps.tiles.utils.RootHelper;
import de.psdev.licensesdialog.LicensesDialog;

import static butterknife.ButterKnife.bind;

/**
 * Created by Gaelan Bolger on 12/29/2016.
 * App settings screen
 */
public class SettingsActivity extends DebugDrawerActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.toolbar)
    Toolbar vToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bind(this);

        setSupportActionBar(vToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppPrefs.getInstance(this).registerListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppPrefs.getInstance(this).unregisterListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ((AppPrefs.KEY_AUTO_ADD_TILES.equals(key) || AppPrefs.KEY_AUTO_REMOVE_TILES.equals(key))
                && AppPrefs.getInstance(this).getBoolean(key, false)) {
            RootHelper.requestRootPermission();
        } else if (AppPrefs.KEY_FORGET_BRIDGE.equals(key)) {
            onForgetBridge();
        }
    }

    private void onForgetBridge() {
        if (RootHelper.isRooted(this))
            RootHelper.removeAllQsTilesBackground(this);

        AppPrefs.getInstance(this).setLastConnectedIPAddress("");
        AppPrefs.getInstance(this).setLastConnectedMacAddress("");
        AppPrefs.getInstance(this).setUsername("");
        for (TileComponent component : TileComponent.values()) {
            AppPrefs.getInstance(this).removeLightTile(component.getKey());
            ComponentName cn = new ComponentName(this, component.getServiceClass());
            if (PackageUtils.isComponentEnabled(getPackageManager(), cn))
                PackageUtils.setComponentEnabled(getPackageManager(), cn, false);
        }

        PHHueSDK hueSDK = PHHueSDK.getInstance();
        PHBridge bridge = hueSDK.getSelectedBridge();
        if (hueSDK.isHeartbeatEnabled(bridge))
            hueSDK.disableHeartbeat(bridge);
        hueSDK.disconnect(bridge);

        Intent intent = new Intent();
        intent.putExtra(AppPrefs.KEY_FORGET_BRIDGE, true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.prefs);

            if (RootHelper.isRooted(getActivity()))
                addPreferencesFromResource(R.xml.prefs_root);

            addPreferencesFromResource(R.xml.prefs_about);

            if (BuildConfig.DEBUG)
                addPreferencesFromResource(R.xml.prefs_debug);

            findPreference(AppPrefs.KEY_FORGET_BRIDGE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ConfirmActionDialog.newInstance(getString(R.string.confirm_forget_bridge),
                            new ConfirmActionDialog.OnActionConfirmedListener() {
                                @Override
                                public void onActionConfirmed() {
                                    AppPrefs.getInstance(getActivity()).putBoolean(AppPrefs.KEY_FORGET_BRIDGE, true);
                                }
                            }).show(getChildFragmentManager(), "confirm_action");
                    return true;
                }
            });

            findPreference("open_source_software").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new LicensesDialog.Builder(getActivity())
                            .setNotices(R.raw.notices)
                            .setNoticesCssStyle(R.string.licenses_dialog_css)
                            .setTitle(R.string.open_source_software)
                            .build().showAppCompat();
                    return true;
                }
            });
            findPreference("app_version").setSummary(getString(R.string.app_version, BuildConfig.VERSION_NAME));
        }
    }
}
