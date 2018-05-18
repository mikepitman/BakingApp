package pitman.co.za.bakingapp.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;

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

        if (mAllRecipes.getValue() != null) {
            for (Recipe recipe : mAllRecipes.getValue()) {
                recipe.setRecipeSteps((ArrayList<RecipeStep>) mRecipeDao.getRecipeSteps(recipe.getRecipeName()));
                recipe.setIngredients((ArrayList<Ingredient>) mRecipeDao.getRecipeIngredients(recipe.getRecipeName()));
            }
            Log.d(LOG_TAG, "Recipes retrieved from database, with steps and ingredients");
        } else {
            Log.d(LOG_TAG, "list of recipes if null at this point");
        }
    }

    LiveData<List<Recipe>> getAllRecipes() {
        return mAllRecipes;
    }

    public void insert(ArrayList<Recipe> recipes) {

        for (Recipe recipe : recipes) {
            new insertRecipeAsyncTask(mRecipeDao).execute(recipe);
            new insertIngredientsAsyncTask(mRecipeDao).execute(recipe.getIngredients());
            new insertRecipeStepsAsyncTask(mRecipeDao).execute(recipe.getRecipeSteps());
        }
    }

    private static class insertRecipeAsyncTask extends AsyncTask<Recipe, Void, Void> {

        private RecipeDao mAsyncTaskDao;

        insertRecipeAsyncTask(RecipeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Recipe... params) throws SQLiteConstraintException {
            mAsyncTaskDao.saveRecipe(params[0]);
            return null;
        }
    }

    private static class insertIngredientsAsyncTask extends AsyncTask<ArrayList<Ingredient>, Void, Void> {

        private RecipeDao mAsyncTaskDao;

        insertIngredientsAsyncTask(RecipeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ArrayList<Ingredient>... params) throws SQLiteConstraintException {
            mAsyncTaskDao.saveIngredients(params[0]);
            return null;
        }
    }

    private static class insertRecipeStepsAsyncTask extends AsyncTask<ArrayList<RecipeStep>, Void, Void> {

        private RecipeDao mAsyncTaskDao;

        insertRecipeStepsAsyncTask(RecipeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ArrayList<RecipeStep>... params) throws SQLiteConstraintException {
            mAsyncTaskDao.saveRecipeSteps(params[0]);
            return null;
        }
    }
}
