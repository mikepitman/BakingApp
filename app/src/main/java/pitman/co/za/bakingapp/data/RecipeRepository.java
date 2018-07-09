package pitman.co.za.bakingapp.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import pitman.co.za.bakingapp.domainObjects.Ingredient;
import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.domainObjects.RecipeStep;

/*
 * https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#8
 * */

public class RecipeRepository {

    private static String LOG_TAG = RecipeRepository.class.getSimpleName();
    private RecipeDao mRecipeDao;
    private LiveData<List<Recipe>> mAllRecipes;

    RecipeRepository(Application application) {
        RecipeRoomDatabase db = RecipeRoomDatabase.getDatabase(application);
        mRecipeDao = db.recipeDao();
        mAllRecipes = mRecipeDao.getAllRecipes();
    }

    // Stored recipes are static, but list may have recipes added as new recipes added to json listing
    LiveData<List<Recipe>> getAllRecipes() {
        return mAllRecipes;
    }

    public void getRecipeAttributes(final Recipe recipe) {
        /* Example used for off-thread retrieval of ingredients and steps for a selected recipe
        * https://medium.freecodecamp.org/room-sqlite-beginner-tutorial-2e725e47bfab
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                recipe.setSteps((ArrayList<RecipeStep>) mRecipeDao.getRecipeSteps(recipe.getName()));
                recipe.setIngredients((ArrayList<Ingredient>) mRecipeDao.getRecipeIngredients(recipe.getName()));
            }
        }).start();
    }

    public void insert(ArrayList<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            new insertRecipeAsyncTask(mRecipeDao).execute(recipe);
        }
    }

    private static class insertRecipeAsyncTask extends AsyncTask<Recipe, Void, Void> {

        private RecipeDao mAsyncTaskDao;

        insertRecipeAsyncTask(RecipeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Recipe... params) throws SQLiteConstraintException {
            Recipe recipe = params[0];
            // Only add recipe if it isn't already in the database (recipe name used as unique primary key)
            if (mAsyncTaskDao.getRecipe(recipe.getName()) == null) {
                mAsyncTaskDao.saveRecipe(recipe);
                mAsyncTaskDao.saveRecipeSteps(recipe.getSteps());
                mAsyncTaskDao.saveIngredients(recipe.getIngredients());
            }
            return null;
        }
    }
}
