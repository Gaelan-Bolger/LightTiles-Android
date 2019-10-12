package day.cloudy.apps.tiles.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import day.cloudy.apps.tiles.R;
import day.cloudy.apps.tiles.adapter.OnItemClickListener;
import day.cloudy.apps.tiles.adapter.PHLightAdapter;
import day.cloudy.apps.tiles.adapter.TileIconsAdapter;
import day.cloudy.apps.tiles.model.LightModel;
import day.cloudy.apps.tiles.model.LightTile;
import day.cloudy.apps.tiles.model.TileComponent;
import day.cloudy.apps.tiles.settings.AppPrefs;
import day.cloudy.apps.tiles.utils.Bundler;
import day.cloudy.apps.tiles.utils.DispUtils;
import day.cloudy.apps.tiles.utils.ResourceUtils;

import static butterknife.ButterKnife.bind;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 * Edit light tile screen
 */
public class EditTileActivity extends DebugDrawerActivity {

    public static final String EXTRA_TILE_COMPONENT = "tile_component";
    private static final String KEY_ICON_NAME = "iconName";
    private static final String KEY_LABEL = "label";
    private static final String KEY_LIGHTS = "lights";

    @BindView(R.id.toolbar)
    Toolbar vToolbar;
    @BindView(R.id.image_view_tile_icon)
    ImageView vIcon;
    @BindView(R.id.edit_text_tile_label)
    EditText vLabel;
    @BindView(R.id.recycler_view)
    RecyclerView vLights;

    private AppPrefs mPrefs;
    private TileComponent mTileComponent;
    private PHLightAdapter mLightAdapter;
    private PopupWindow mIconsPopup;

    private int[] mIconResIds;
    private String mIconName;
    private String mLabel;
    private List<LightModel> mLights;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_TILE_COMPONENT, mTileComponent.name());
        outState.putString(KEY_ICON_NAME, mIconName);
        outState.putString(KEY_LABEL, mLabel);
        if (null != mLights)
            outState.putParcelableArrayList(KEY_LIGHTS, new ArrayList<>(mLights));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = AppPrefs.getInstance(this);
        mIconResIds = ResourceUtils.getTileIconIds(this);

        if (null == savedInstanceState) {
            String tileComponentName = getIntent().getStringExtra(EXTRA_TILE_COMPONENT);
            mTileComponent = TileComponent.valueOf(tileComponentName);
            mIconName = ResourceUtils.getResourceName(this, mIconResIds[0]);
            LightTile tile = AppPrefs.getInstance(this).getLightTile(mTileComponent.getKey());
            if (null != tile) {
                mIconName = tile.getIconName();
                mLabel = tile.getLabel();
                mLights = tile.getLightModels();
            }
        } else {
            mTileComponent = TileComponent.valueOf(savedInstanceState.getString(EXTRA_TILE_COMPONENT));
            mIconName = savedInstanceState.getString(KEY_ICON_NAME, mIconName);
            mLabel = savedInstanceState.getString(KEY_LABEL, mLabel);
            if (savedInstanceState.containsKey(KEY_LIGHTS))
                mLights = savedInstanceState.getParcelableArrayList(KEY_LIGHTS);
        }

        PHHueSDK hueSDK = PHHueSDK.getInstance();
        PHBridge bridge = hueSDK.getSelectedBridge();
        if (null != bridge) {
            mLightAdapter = new PHLightAdapter(this, null);
            mLightAdapter.setShowIcons(false);
            mLightAdapter.setIsMultiSelect(true);
            List<PHLight> allLights = bridge.getResourceCache().getAllLights();
            Collections.sort(allLights, new Comparator<PHLight>() {
                        @Override
                        public int compare(PHLight lhs, PHLight rhs) {
                            return lhs.getName().compareToIgnoreCase(rhs.getName());
                        }
                    }
            );
            mLightAdapter.setLights(allLights);
            if (null != mLights)
                for (LightModel light : mLights)
                    mLightAdapter.setLightSelected(light.getIdentifier());
        } else {
            Toast.makeText(this, R.string.bridge_not_connected, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_edit_tile);
        bind(this);

        setSupportActionBar(vToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            actionBar.setTitle(mTileComponent.getTitleResId());
        }

        vIcon.setImageDrawable(ResourceUtils.getDrawableForName(this, mIconName));

        if (!TextUtils.isEmpty(mLabel)) {
            vLabel.setText(mLabel);
            vLabel.setSelection(mLabel.length());
        }

        vLights.setHasFixedSize(true);
        vLights.setLayoutManager(new LinearLayoutManager(this));
        vLights.setAdapter(mLightAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mIconsPopup && mIconsPopup.isShowing())
            mIconsPopup.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tile_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.item_done:
                onDonePressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged(R.id.edit_text_tile_label)
    public void onLabelChanged(CharSequence s, int start, int before, int count) {
        mLabel = String.valueOf(s);
    }

    @OnClick(R.id.image_view_tile_icon)
    public void onSelectIcon(View view) {
        View parent = (View) view.getParent();
        int popupWidth = parent.getWidth() - parent.getPaddingStart() - parent.getPaddingEnd();
        int iconSize = getResources().getDimensionPixelSize(R.dimen.tile_icon_size);
        int columns = popupWidth / iconSize;
        int rows = mIconResIds.length / columns;
        RecyclerView recyclerView = (RecyclerView) View.inflate(this, R.layout.popup_icons, null);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        recyclerView.setAdapter(new TileIconsAdapter(this, mIconResIds, new OnItemClickListener<Integer>() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, Integer item) {
                mIconsPopup.dismiss();
                vIcon.setImageResource(item);
                mIconName = ResourceUtils.getResourceName(EditTileActivity.this, item);
            }
        }));
        mIconsPopup = new PopupWindow(this);
        mIconsPopup.setBackgroundDrawable(ResourceUtils.getDrawable(this, R.drawable.popup_bg));
        mIconsPopup.setFocusable(true);
        mIconsPopup.setWidth(popupWidth);
        mIconsPopup.setHeight(iconSize * Math.min(columns, rows));
        mIconsPopup.setElevation(DispUtils.dp(8));
        mIconsPopup.setContentView(recyclerView);
        mIconsPopup.showAsDropDown(view);
    }

    private void onDonePressed() {
        if (TextUtils.isEmpty(mLabel)) {
            vLabel.setError(getString(R.string.required));
            return;
        }

        List<PHLight> lights = mLightAdapter.getSelection();
        if (lights.size() == 0) {
            Toast.makeText(this, R.string.no_lights_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        List<LightModel> models = new ArrayList<>();
        for (PHLight light : lights)
            models.add(new LightModel(light.getIdentifier(), light.getName(),
                    light.getModelNumber(), light.getVersionNumber()));
        LightTile tile = new LightTile(mLabel, mIconName, models);
        mPrefs.putLightTile(mTileComponent.getKey(), tile);

        Bundler bundler = new Bundler();
        for (LightModel model : models) {
            bundler.with("light_model", model.getModelNumber());
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TILE_COMPONENT, mTileComponent.name());
        setResult(RESULT_OK, intent); // result handled in LightTilesFragment
        finish();
    }
}
