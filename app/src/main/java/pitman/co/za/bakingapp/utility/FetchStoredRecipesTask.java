package pitman.co.za.bakingapp.utility;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import pitman.co.za.bakingapp.MainActivityFragment;
import pitman.co.za.bakingapp.domainObjects.Recipe;

public class FetchStoredRecipesTask extends AsyncTask<Void, Void, ArrayList<Recipe>> {

    private String LOG_TAG = FetchStoredRecipesTask.class.getSimpleName();
    private MainActivityFragment mMainActivityFragment;


    public FetchStoredRecipesTask(MainActivityFragment mainActivityFragment) {
        this.mMainActivityFragment = mainActivityFragment;
    }

    @Override
    protected void onPostExecute(ArrayList<Recipe> recipes) {
        super.onPostExecute(recipes);
        Log.d(LOG_TAG, "passing arrayList of recipes from room database back to mainActivityFragment");
        // todo: pass this to a MainActivityFragment method that handles returned information properly
        mMainActivityFragment.generateRecipeAdapterWithData(recipes);
    }

    @Override
    protected ArrayList<Recipe> doInBackground(Void... params) {

        return new ArrayList<Recipe>();
    }
}