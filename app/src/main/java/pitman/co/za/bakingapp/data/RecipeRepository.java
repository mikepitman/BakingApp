package pitman.co.za.bakingapp.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import pitman.co.za.bakingapp.domainObjects.Recipe;

/*
* https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#8
* */

public class RecipeRepository {

    private RecipeDao mRecipeDao;
    private LiveData<List<Recipe>> mAllRecipes;

    RecipeRepository(Application application) {
        RecipeRoomDatabase db = RecipeRoomDatabase.getDatabase(application);
        mRecipeDao = db.recipeDao();
        mAllRecipes = mRecipeDao.getAllRecipes();
    }

    LiveData<List<Recipe>> getAllRecipes() {
        return mAllRecipes;
    }


    public void insert(Recipe recipe) {
        new insertAsyncTask(mRecipeDao).execute(recipe);
    }

    private static class insertAsyncTask extends AsyncTask<Recipe, Void, Void> {

        private RecipeDao mAsyncTaskDao;

        insertAsyncTask(RecipeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Recipe... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
