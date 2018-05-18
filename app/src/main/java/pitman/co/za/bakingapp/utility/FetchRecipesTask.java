package pitman.co.za.bakingapp.utility;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import pitman.co.za.bakingapp.MainActivityFragment;
import pitman.co.za.bakingapp.domainObjects.Ingredient;
import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.domainObjects.RecipeStep;

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
        Log.d(LOG_TAG, "passing arrayList of results back to mainActivityFragment");
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
        Log.d(LOG_TAG, "built URL for querying recipe: " + myUrl);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String recipeListing = null;

        try {
            URL url = new URL(myUrl);

            // Create the request to page with recipe, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            Log.d(LOG_TAG, "network query being made");
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

        try {
            recipesArray = parseRecipesFromJson(recipeListing);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException caught: " + e.getMessage());
        }

        return recipesArray;
    }

    /**
     * Parse returned JSON-encoded movie data and populate objects.
     * Based on Udacity-supplied JSON gist from Developing Android Apps: Fundamentals 'Project Sunshine' app
     */
    private ArrayList<Recipe> parseRecipesFromJson(String jsonRecipes)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String JSON_RECIPE_ID = "id";
        final String JSON_RECIPE_NAME = "name";
        final String JSON_RECIPE_STEPS_ARRAY = "steps";
        final String JSON_RECIPE_INGREDIENTS_ARRAY = "ingredients";

        final String JSON_INGREDIENT_QUANTITY = "quantity";
        final String JSON_INGREDIENT_MEASURE = "measure";
        final String JSON_INGREDIENT_INGREDIENT = "ingredient";

        final String JSON_STEP_ID = "id";
        final String JSON_STEP_SHORT_DESC = "shortDescription";
        final String JSON_STEP_DESCRIPTION = "description";
        final String JSON_STEP_VIDEO_URL = "videoURL";
        final String JSON_STEP_THUMBNAIL_URL = "thumbnailURL";

        JSONArray recipesArray = new JSONArray(jsonRecipes);

        ArrayList<Recipe> parsedRecipes = new ArrayList<>();
        for (int i = 0; i < recipesArray.length(); i++) {

            JSONObject recipe_obj = recipesArray.getJSONObject(i);
            Recipe recipe = new Recipe(
                    recipe_obj.getString(JSON_RECIPE_ID),
                    recipe_obj.getString(JSON_RECIPE_NAME));

            Log.d(LOG_TAG, "id, name: " + recipe.getRecipeId() + ", " + recipe.getRecipeName());

            JSONArray ingredientsArray = recipe_obj.getJSONArray(JSON_RECIPE_INGREDIENTS_ARRAY);
            if (ingredientsArray != null) {
                ArrayList<Ingredient> ingredientArrayList = new ArrayList<>();

                for (int j = 0; j < ingredientsArray.length(); j++) {
                    JSONObject ingredient_obj = ingredientsArray.getJSONObject(j);
                    Ingredient ingredient = new Ingredient(
                            recipe.getRecipeName(),
                            ingredient_obj.getString(JSON_INGREDIENT_QUANTITY),
                            ingredient_obj.getString(JSON_INGREDIENT_MEASURE),
                            ingredient_obj.getString(JSON_INGREDIENT_INGREDIENT));
                    ingredientArrayList.add(ingredient);
                }
                recipe.setIngredients(ingredientArrayList);
//                Log.d(LOG_TAG, "number of ingredients " + recipe.getIngredients().size());
            }

            JSONArray recipeStepsArray = recipe_obj.getJSONArray(JSON_RECIPE_STEPS_ARRAY);
            if (recipeStepsArray != null) {
                ArrayList<RecipeStep> stepArrayList = new ArrayList<>();

                for (int k = 0; k < recipeStepsArray.length(); k++) {
                    JSONObject recipeStep_obj = recipeStepsArray.getJSONObject(k);
                    RecipeStep recipeStep = new RecipeStep(
                            recipe.getRecipeName(),
                            recipeStep_obj.getString(JSON_STEP_SHORT_DESC),
                            recipeStep_obj.getString(JSON_STEP_ID),
                            recipeStep_obj.getString(JSON_STEP_DESCRIPTION),
                            recipeStep_obj.getString(JSON_STEP_VIDEO_URL),
                            recipeStep_obj.getString(JSON_STEP_THUMBNAIL_URL));

                    stepArrayList.add(recipeStep);
                }
                recipe.setRecipeSteps(stepArrayList);
//                Log.d(LOG_TAG, "number of steps " + recipe.getRecipeSteps().size());
            }
            parsedRecipes.add(recipe);
        }

        return parsedRecipes;
    }
}
