package day.cloudy.apps.tiles.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.OnLongClick;
import day.cloudy.apps.tiles.R;
import day.cloudy.apps.tiles.activity.EditTileActivity;
import day.cloudy.apps.tiles.dialog.ConfirmActionDialog;
import day.cloudy.apps.tiles.model.LightTile;
import day.cloudy.apps.tiles.model.TileComponent;
import day.cloudy.apps.tiles.settings.AppPrefs;
import day.cloudy.apps.tiles.utils.PackageUtils;
import day.cloudy.apps.tiles.utils.RootHelper;
import day.cloudy.apps.tiles.utils.Spotlight;
import day.cloudy.apps.tiles.view.LightTileView;

import static butterknife.ButterKnife.bind;
import static butterknife.ButterKnife.findById;
import static day.cloudy.apps.tiles.utils.PackageUtils.isComponentEnabled;
import static day.cloudy.apps.tiles.utils.PackageUtils.setComponentEnabled;
import static day.cloudy.apps.tiles.utils.ResourceUtils.getDrawable;
import static day.cloudy.apps.tiles.utils.ResourceUtils.getDrawableForName;

/**
 * Created by Gaelan Bolger on 12/26/2016.
 */
public class LightTilesFragment extends Fragment {

    public static final int REQ_EDIT_TILE = 1014;

    @BindViews({R.id.light_tile_1, R.id.light_tile_2, R.id.light_tile_3, R.id.light_tile_4, R.id.light_tile_5,
            R.id.light_tile_6, R.id.light_tile_7, R.id.light_tile_8, R.id.light_tile_9, R.id.light_tile_10,
            R.id.light_tile_11, R.id.light_tile_12})
    List<LightTileView> vLightTiles;

    private AppPrefs mPrefs;
    private Map<TileComponent, LightTileView> mTileViews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = AppPrefs.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_light_tiles, container, false);
        bind(this, view);

        TileComponent[] tileComponents = TileComponent.values();
        if (tileComponents.length != vLightTiles.size())
            throw new IllegalStateException("Must provide a tile view for every tile component");

        mTileViews = new HashMap<>(vLightTiles.size());
        for (int i = 0; i < vLightTiles.size(); i++) {
            TileComponent tileComponent = tileComponents[i];
            LightTileView tileView = vLightTiles.get(i);
            tileView.setTag(tileComponent);
            mTileViews.put(tileComponent, tileView);
            refreshTileView(tileComponent);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Spotlight.shine(getActivity(), vLightTiles.get(0), getString(R.string.light_tile))
                .subHeading(getString(R.string.spotlight_create_light_tile))
                .performClick(true)
                .usageId("create_light_tile")
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EDIT_TILE) {
            if (resultCode == Activity.RESULT_OK) {
                String tileComponentName = data.getStringExtra(EditTileActivity.EXTRA_TILE_COMPONENT);
                TileComponent tileComponent = TileComponent.valueOf(tileComponentName);
                ComponentName cn = new ComponentName(getActivity(), tileComponent.getServiceClass());
                if (!isComponentEnabled(getPackageManager(), cn)) {
                    setComponentEnabled(getPackageManager(), cn, true);
                    Snackbar.make(findById(getActivity(), R.id.coordinator_layout), R.string.tile_enabled, Snackbar.LENGTH_SHORT).show();
                }
                refreshTileView(tileComponent);
                addQsTile(tileComponent);
                Spotlight.shine(getActivity(), mTileViews.get(tileComponent), getString(R.string.light_tile))
                        .subHeading(getString(R.string.spotlight_delete_light_tile))
                        .performClick(false)
                        .usageId("delete_light_tile")
                        .show();
            }
        }
    }

    @OnClick({R.id.light_tile_1, R.id.light_tile_2, R.id.light_tile_3, R.id.light_tile_4, R.id.light_tile_5,
            R.id.light_tile_6, R.id.light_tile_7, R.id.light_tile_8, R.id.light_tile_9, R.id.light_tile_10,
            R.id.light_tile_11, R.id.light_tile_12})
    public void onTileClick(LightTileView tileView) {
        TileComponent tileComponent = (TileComponent) tileView.getTag();
        Intent intent = new Intent(getActivity(), EditTileActivity.class);
        intent.putExtra(EditTileActivity.EXTRA_TILE_COMPONENT, tileComponent.name());
        startActivityForResult(intent, REQ_EDIT_TILE);
    }

    @OnLongClick({R.id.light_tile_1, R.id.light_tile_2, R.id.light_tile_3, R.id.light_tile_4, R.id.light_tile_5,
            R.id.light_tile_6, R.id.light_tile_7, R.id.light_tile_8, R.id.light_tile_9, R.id.light_tile_10,
            R.id.light_tile_11, R.id.light_tile_12})
    public boolean onTileLongClick(LightTileView tileView) {
        final TileComponent tileComponent = (TileComponent) tileView.getTag();
        LightTile tile = mPrefs.getLightTile(tileComponent.getKey());
        if (null == tile)
            return false;
        ConfirmActionDialog.newInstance(getString(R.string.confirm_delete_light_tile), new ConfirmActionDialog.OnActionConfirmedListener() {
            @Override
            public void onActionConfirmed() {
                if (mPrefs.removeLightTile(tileComponent.getKey())) {
                    ComponentName cn = new ComponentName(getActivity(), tileComponent.getServiceClass());
                    if (isComponentEnabled(getPackageManager(), cn)) {
                        setComponentEnabled(getPackageManager(), cn, false);
                        Snackbar.make(findById(getActivity(), R.id.coordinator_layout), R.string.tile_disabled, Snackbar.LENGTH_SHORT).show();
                    }
                    refreshTileView(tileComponent);
                    removeQsTile(tileComponent);
                } else {
                    Snackbar.make(findById(getActivity(), R.id.coordinator_layout), R.string.cant_delete_tile, Snackbar.LENGTH_SHORT).show();
                }
            }
        }).show(getChildFragmentManager(), "confirm_action");
        return true;
    }

    private void refreshTileView(TileComponent tileComponent) {
        LightTileView tileView = mTileViews.get(tileComponent);
        LightTile tile = mPrefs.getLightTile(tileComponent.getKey());
        if (null == tile) {
            tileView.setIconDrawable(getDrawable(getActivity(), R.drawable.ic_block_white_24dp));
            tileView.setLabelText(getString(tileComponent.getTitleResId()));
        } else {
            tileView.setIconDrawable(getDrawableForName(getActivity(), tile.getIconName()));
            tileView.setLabelText(tile.getLabel());
        }
        ComponentName cn = new ComponentName(getActivity(), tileComponent.getServiceClass());
        tileView.setEnabled(PackageUtils.isComponentEnabled(getPackageManager(), cn));
    }

    private void addQsTile(final TileComponent tileComponent) {
        if (RootHelper.isRooted(getActivity()) && mPrefs.getBoolean(AppPrefs.KEY_AUTO_ADD_TILES, false)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RootHelper.addQsTile(getActivity(), tileComponent);
                }
            }).start();
        }
    }

    private void removeQsTile(final TileComponent tileComponent) {
        if (RootHelper.isRooted(getActivity()) && mPrefs.getBoolean(AppPrefs.KEY_AUTO_REMOVE_TILES, false)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RootHelper.removeQsTile(getActivity(), tileComponent);
                }
            }).start();
        }
    }

    public PackageManager getPackageManager() {
        return getActivity().getPackageManager();
    }
}
