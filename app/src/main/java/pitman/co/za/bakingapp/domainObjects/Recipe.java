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

    public Recipe(@NonNull String recipeId, @NonNull String recipeName) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.recipeId);
        parcel.writeString(this.recipeName);
        parcel.writeList(ingredients);
        parcel.writeList(recipeSteps);
    }

//    public Recipe() {}

    protected Recipe(Parcel in) {
        this.recipeId = in.readString();
        this.recipeName= in.readString();
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
