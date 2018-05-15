package pitman.co.za.bakingapp.domainObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 2018/01/24.
 */

public class Recipe implements Parcelable {

    private String recipeId;
    private String recipeName;
    private ArrayList<Ingredient> mIngredients;
    private ArrayList<RecipeStep> mRecipeSteps;

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public List<Ingredient> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        mIngredients = ingredients;
    }

    public List<RecipeStep> getRecipeSteps() {
        return mRecipeSteps;
    }

    public void setRecipeSteps(ArrayList<RecipeStep> recipeSteps) {
        mRecipeSteps = recipeSteps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.recipeId);
        parcel.writeString(this.recipeName);
        parcel.writeList(mIngredients);
        parcel.writeList(mRecipeSteps);
    }

    public Recipe() {
    }

    protected Recipe(Parcel in) {
        this.recipeId = in.readString();
        this.recipeName= in.readString();
        this.mIngredients = in.readArrayList(Ingredient.class.getClassLoader());
        this.mRecipeSteps = in.readArrayList(RecipeStep.class.getClassLoader());
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
