package pitman.co.za.bakingapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import pitman.co.za.bakingapp.domainObjects.Ingredient;
import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.domainObjects.RecipeStep;

@Dao
public interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveRecipe(Recipe recipeEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveIngredients(List<Ingredient> ingredient);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveRecipeSteps(List<RecipeStep> recipeStep);

    @Transaction
    @Query("select * from Recipe order by name")
    LiveData<List<Recipe>> getAllRecipes();

    @Query("select * from Recipe where name = :recipeName")
    Recipe getRecipe(String recipeName);

    @Query("select * from recipe_ingredient where parentRecipe = :parentRecipe")
    List<Ingredient> getRecipeIngredients(String parentRecipe);

    @Query("select * from recipe_step where parentRecipe = :parentRecipe order by id")
    List<RecipeStep> getRecipeSteps(String parentRecipe);
}
