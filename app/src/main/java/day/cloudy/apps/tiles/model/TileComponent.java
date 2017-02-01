package day.cloudy.apps.tiles.model;

import day.cloudy.apps.tiles.R;
import day.cloudy.apps.tiles.service.LightTile10Service;
import day.cloudy.apps.tiles.service.LightTile11Service;
import day.cloudy.apps.tiles.service.LightTile12Service;
import day.cloudy.apps.tiles.service.LightTile1Service;
import day.cloudy.apps.tiles.service.LightTile2Service;
import day.cloudy.apps.tiles.service.LightTile3Service;
import day.cloudy.apps.tiles.service.LightTile4Service;
import day.cloudy.apps.tiles.service.LightTile5Service;
import day.cloudy.apps.tiles.service.LightTile6Service;
import day.cloudy.apps.tiles.service.LightTile7Service;
import day.cloudy.apps.tiles.service.LightTile8Service;
import day.cloudy.apps.tiles.service.LightTile9Service;
import day.cloudy.apps.tiles.service.LightTileService;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public enum TileComponent {

    TILE_1(R.string.tile_1, LightTile1Service.class),
    TILE_2(R.string.tile_2, LightTile2Service.class),
    TILE_3(R.string.tile_3, LightTile3Service.class),
    TILE_4(R.string.tile_4, LightTile4Service.class),
    TILE_5(R.string.tile_5, LightTile5Service.class),
    TILE_6(R.string.tile_6, LightTile6Service.class),
    TILE_7(R.string.tile_7, LightTile7Service.class),
    TILE_8(R.string.tile_8, LightTile8Service.class),
    TILE_9(R.string.tile_9, LightTile9Service.class),
    TILE_10(R.string.tile_10, LightTile10Service.class),
    TILE_11(R.string.tile_11, LightTile11Service.class),
    TILE_12(R.string.tile_12, LightTile12Service.class);

    private final int titleResId;
    private final Class<? extends LightTileService> serviceClass;

    TileComponent(int titleResId, Class<? extends LightTileService> serviceClass) {
        this.titleResId = titleResId;
        this.serviceClass = serviceClass;
    }

    public String getKey() {
        return name().toLowerCase();
    }

    public int getTitleResId() {
        return titleResId;
    }

    public Class<? extends LightTileService> getServiceClass() {
        return serviceClass;
    }
}
