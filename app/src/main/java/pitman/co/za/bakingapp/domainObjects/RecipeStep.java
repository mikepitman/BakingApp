package pitman.co.za.bakingapp.domainObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 2018/01/24.
 */

public class RecipeStep implements Parcelable {

    private String id;
    private String shortDesciption;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortDesciption() {
        return shortDesciption;
    }

    public void setShortDesciption(String shortDesciption) {
        this.shortDesciption = shortDesciption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.shortDesciption);
        parcel.writeString(this.description);
        parcel.writeString(this.videoUrl);
        parcel.writeString(this.thumbnailUrl);
    }

    public RecipeStep() {
    }

    protected RecipeStep(Parcel in) {
        this.id = in.readString();
        this.shortDesciption = in.readString();
        this.description = in.readString();
        this.videoUrl = in.readString();
        this.thumbnailUrl = in.readString();
    }

    public static final Parcelable.Creator<RecipeStep> CREATOR = new Parcelable.Creator<RecipeStep>() {
        public RecipeStep createFromParcel(Parcel source) {
            return new RecipeStep(source);
        }

        public RecipeStep[] newArray(int size) {
            return new RecipeStep[size];
        }
    };
}
