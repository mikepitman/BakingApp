package pitman.co.za.bakingapp.domainObjects;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 2018/01/24.
 * Modified to utilise 'room', a hibernate-type database abstraction tool
 */
@Entity(tableName = "recipe")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "recipeId")
    private String recipeId;

    @NonNull
    @ColumnInfo(name = "recipeName")
    private String recipeName;
    private ArrayList<Ingredient> mIngredients;
    private ArrayList<RecipeStep> mRecipeSteps;

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
}
