package pitman.co.za.bakingapp.utility;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import pitman.co.za.bakingapp.MainActivityFragment;
import pitman.co.za.bakingapp.domainObjects.Ingredient;
import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.domainObjects.RecipeStep;
import pitman.co.za.bakingapp.jsonDeserialisation.IngredientDeserialiser;
import pitman.co.za.bakingapp.jsonDeserialisation.RecipeDeserialiser;
import pitman.co.za.bakingapp.jsonDeserialisation.StepDeserialiser;

/**
 * Created by Michael on 2018/02/07.
 */

public class FetchRecipesTask extends AsyncTask<String, Void, ArrayList<Recipe>> {

    private String LOG_TAG = FetchRecipesTask.class.getSimpleName();
    private MainActivityFragment mMainActivityFragment;

    public FetchRecipesTask(MainActivityFragment mainActivityFragment) {
        this.mMainActivityFragment = mainActivityFragment;
    }

    @Override
    protected void onPostExecute(ArrayList<Recipe> recipes) {
        super.onPostExecute(recipes);
        mMainActivityFragment.generateRecipeAdapterWithData(recipes);
    }

    @Override
    protected ArrayList<Recipe> doInBackground(String... strings) {
        ArrayList<Recipe> recipesArray = null;

        Uri.Builder builder = new Uri.Builder();
        // https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json
        builder.scheme("http")
                .authority("d17h27t6h515a5.cloudfront.net")
                .appendPath("topher")
                .appendPath("2017")
                .appendPath("May")
                .appendPath("59121517_baking")
                .appendPath("baking.json");
        String myUrl = builder.build().toString();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String recipeListing = null;

        try {
            URL url = new URL(myUrl);

            // Create the request to page with recipe, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();


            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;    // Nothing to do.
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newli ne isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;    // Stream was empty.  No point in parsing.
            }
            recipeListing = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException occured: Error ", e);
            return null;        // If the code didn't successfully get the recipe data, there's no point in attempting to parse it.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        if (recipeListing == null || recipeListing.isEmpty()) {
            Log.d(LOG_TAG, "recipeListing string is empty");
        }

        // Deserialise the json recipe information into Java objects
        try {
            recipesArray = parseRecipesFromJson(recipeListing);
        } catch (JsonParseException e) {
            Log.e(LOG_TAG, "JsonParseException caught: " + e.getMessage());
        }

        return recipesArray;
    }

    /**
     * Parse returned JSON-encoded movie data and populate objects.
     * Reviewer suggested using library (GSON) for parsing input. Resources referenced:
     * https://github.com/google/gson
     * http://www.javacreed.com/gson-deserialiser-example/
     */
    private ArrayList<Recipe> parseRecipesFromJson(String jsonRecipes) throws JsonParseException {

        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Recipe.class, new RecipeDeserialiser());
        builder.registerTypeAdapter(RecipeStep.class, new StepDeserialiser());
        builder.registerTypeAdapter(Ingredient.class, new IngredientDeserialiser());
        Gson gson = builder.create();

        Recipe[] parsedRecipesArray = gson.fromJson(jsonRecipes, Recipe[].class);

        return new ArrayList<>(Arrays.asList(parsedRecipesArray));
    }
}
