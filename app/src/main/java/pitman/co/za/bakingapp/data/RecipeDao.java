package pitman.co.za.bakingapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import pitman.co.za.bakingapp.domainObjects.Recipe;

@Dao
public interface RecipeDao {

    @Insert
    void insert(Recipe recipe);

    @Query("select * from recipe order by recipeName")
    LiveData<List<Recipe>> getAllRecipes();
}
