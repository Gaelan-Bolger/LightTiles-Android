package day.cloudy.apps.tiles.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 */
public class LightModel implements Parcelable {

    private String identifier;
    private String name;
    private String modelNumber;
    private String versionNumber;

    public LightModel() {
    }

    public LightModel(String identifier, String name, String modelNumber, String versionNumber) {
        this.identifier = identifier;
        this.name = name;
        this.modelNumber = modelNumber;
        this.versionNumber = versionNumber;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.identifier);
        dest.writeString(this.name);
        dest.writeString(this.modelNumber);
        dest.writeString(this.versionNumber);
    }

    protected LightModel(Parcel in) {
        this.identifier = in.readString();
        this.name = in.readString();
        this.modelNumber = in.readString();
        this.versionNumber = in.readString();
    }

    public static final Parcelable.Creator<LightModel> CREATOR = new Parcelable.Creator<LightModel>() {
        @Override
        public LightModel createFromParcel(Parcel source) {
            return new LightModel(source);
        }

        @Override
        public LightModel[] newArray(int size) {
            return new LightModel[size];
        }
    };
}
