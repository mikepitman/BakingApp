package pitman.co.za.bakingapp.domainObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Michael on 2018/01/24.
 *
 * Note to self: room doesn't allow entity objects to reference each other
 * https://developer.android.com/training/data-storage/room/defining-data
 */
@Entity(tableName = "recipe_step",
        primaryKeys = {"parentRecipe", "id"},
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "name",
                childColumns = "parentRecipe"))
public class RecipeStep implements Parcelable {

    @NonNull
    private String parentRecipe;
    private String shortDescription;
    private int id;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;

    public RecipeStep(String shortDescription, int id, String description, String videoUrl, String thumbnailUrl) {
        this.shortDescription = shortDescription;
        this.id = id;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    @Ignore
    public RecipeStep() {}

    public void setParentRecipe(@NonNull String parentRecipe) {
        this.parentRecipe = parentRecipe;
    }

    @NonNull
    public String getParentRecipe() {
        return parentRecipe;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.parentRecipe);
        parcel.writeString(this.shortDescription);
        parcel.writeInt(this.id);
        parcel.writeString(this.description);
        parcel.writeString(this.videoUrl);
        parcel.writeString(this.thumbnailUrl);
    }

    public RecipeStep(Parcel in) {
        this.parentRecipe = in.readString();
        this.shortDescription = in.readString();
        this.id = in.readInt();
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
