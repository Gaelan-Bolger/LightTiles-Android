package day.cloudy.apps.tiles.model;

import java.util.List;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public class LightTile {

    private String label;
    private String iconName;
    private List<LightModel> lightModels;

    public LightTile() {
    }

    public LightTile(String label, String iconName, List<LightModel> lightModels) {
        this.label = label;
        this.iconName = iconName;
        this.lightModels = lightModels;
    }

    public String getLabel() {
        return label;
    }

    public String getIconName() {
        return iconName;
    }

    public List<LightModel> getLightModels() {
        return lightModels;
    }
}
