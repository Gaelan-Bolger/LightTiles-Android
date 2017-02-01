package day.cloudy.apps.tiles.service;

import android.support.annotation.NonNull;

import day.cloudy.apps.tiles.model.TileComponent;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public class LightTile3Service extends LightTileService {

    private static final String TAG = LightTile3Service.class.getSimpleName();

    @NonNull
    protected TileComponent getTileComponent() {
        return TileComponent.TILE_3;
    }
}
