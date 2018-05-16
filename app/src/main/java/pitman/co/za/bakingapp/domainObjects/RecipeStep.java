package pitman.co.za.bakingapp.domainObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Michael on 2018/01/24.
 */
@Entity(tableName = "recipe_step")
public class RecipeStep {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private Recipe recipe;

    @NonNull
    private String shortDesciption;

    private int stepNumber;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;

    public RecipeStep(@NonNull Recipe recipe, @NonNull String shortDesciption) {
        this.recipe = recipe;
        this.shortDesciption = shortDesciption;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public Recipe getRecipe() {
        return recipe;
    }

    @NonNull
    public String getShortDesciption() {
        return shortDesciption;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
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

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(this.id);
//        parcel.writeString(this.shortDesciption);
//        parcel.writeString(this.description);
//        parcel.writeString(this.videoUrl);
//        parcel.writeString(this.thumbnailUrl);
//    }
//
//    public RecipeStep() {
//    }
//
//    protected RecipeStep(Parcel in) {
//        this.id = in.readString();
//        this.shortDesciption = in.readString();
//        this.description = in.readString();
//        this.videoUrl = in.readString();
//        this.thumbnailUrl = in.readString();
//    }
//
//    public static final Parcelable.Creator<RecipeStep> CREATOR = new Parcelable.Creator<RecipeStep>() {
//        public RecipeStep createFromParcel(Parcel source) {
//            return new RecipeStep(source);
//        }
//
//        public RecipeStep[] newArray(int size) {
//            return new RecipeStep[size];
//        }
//    };
}
