package day.cloudy.apps.tiles.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import day.cloudy.apps.tiles.R;
import day.cloudy.apps.tiles.settings.AppPrefs;

import static butterknife.ButterKnife.bind;

/**
 * Created by Gaelan Bolger on 12/26/2016.
 * Main tiles screen
 */
public class MainActivity extends DebugDrawerActivity {

    private static final int REQ_SETTINGS = 5642;

    @BindView(R.id.toolbar)
    Toolbar vToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind(this);

        setSupportActionBar(vToolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SETTINGS) {
            if (resultCode == RESULT_OK && data.getBooleanExtra(AppPrefs.KEY_FORGET_BRIDGE, false)) {
                // The current bridge has been forgotten, reset flag and finish to LaunchActivity
                AppPrefs.getInstance(this).putBoolean(AppPrefs.KEY_FORGET_BRIDGE, false);
                startActivity(new Intent(this, LaunchActivity.class));
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), REQ_SETTINGS);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
