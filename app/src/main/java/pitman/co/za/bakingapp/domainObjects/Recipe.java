package pitman.co.za.bakingapp.domainObjects;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Michael on 2018/01/24.
 * Modified to utilise 'room', a hibernate-type database abstraction tool
 */
@Entity(tableName = "recipe")
public class Recipe implements Parcelable {

    @NonNull
    @ColumnInfo(name = "recipeId")
    private String recipeId;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "recipeName")
    private String recipeName;

    @Ignore
    private ArrayList<Ingredient> ingredients;
    @Ignore
    private ArrayList<RecipeStep> recipeSteps;

    @ColumnInfo(name = "recipeServings")
    private String recipeServings;

    @ColumnInfo(name = "recipeImage")
    private String recipeImage;

    public Recipe(@NonNull String recipeId, @NonNull String recipeName, String recipeServings, String recipeImage) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeServings = recipeServings;
        this.recipeImage = recipeImage;
    }

    @NonNull
    public String getRecipeId() {
        return recipeId;
    }

    @NonNull
    public String getRecipeName() {
        return recipeName;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<RecipeStep> getRecipeSteps() {
        return recipeSteps;
    }

    public void setRecipeSteps(ArrayList<RecipeStep> recipeSteps) {
        this.recipeSteps = recipeSteps;
    }

    public String getRecipeServings() {
        return recipeServings;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.recipeId);
        parcel.writeString(this.recipeName);
        parcel.writeString(this.recipeServings);
        parcel.writeString(this.recipeImage);
        parcel.writeList(ingredients);
        parcel.writeList(recipeSteps);
    }

//    public Recipe() {}

    protected Recipe(Parcel in) {
        this.recipeId = in.readString();
        this.recipeName= in.readString();
        this.recipeServings = in.readString();
        this.recipeImage = in.readString();
        this.ingredients = in.readArrayList(Ingredient.class.getClassLoader());
        this.recipeSteps = in.readArrayList(RecipeStep.class.getClassLoader());
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
