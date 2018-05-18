package pitman.co.za.bakingapp.domainObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

/**
 * Created by Michael on 2018/01/24.
 *
 * Note to self: room doesn't allow entity objects to reference each other
 * https://developer.android.com/training/data-storage/room/defining-data
 */
@Entity(tableName = "recipe_step",
        primaryKeys = {"parentRecipe", "stepNumber"},
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "recipeName",
                childColumns = "parentRecipe"))
public class RecipeStep {

    @NonNull
    private String parentRecipe;
    private String shortDescription;
    @NonNull
    private String stepNumber;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;

    public RecipeStep(@NonNull String parentRecipe, String shortDescription, @NonNull String stepNumber, String description, String videoUrl, String thumbnailUrl) {
        this.parentRecipe = parentRecipe;
        this.shortDescription = shortDescription;
        this.stepNumber = stepNumber;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    @NonNull
    public String getParentRecipe() {
        return parentRecipe;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    @NonNull
    public String getStepNumber() {
        return stepNumber;
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
}
