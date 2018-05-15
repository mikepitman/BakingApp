package pitman.co.za.bakingapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Michael on 2018/02/06.
 *
 * Based on contentProvider from my own PopularMovies submission, which is based on
 * contentProvider-related code in WeatherContract from Udacity Sunshine example app
 */

/*  Note to self: this is like an API / interface declaration, defining format data is returned in, and the method calls available for accessing data
* */

public class RecipesContract {

    private static String LOG_TAG = RecipesContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "pitman.co.za.bakingapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECIPES = "recipes";      // what does this do?
    public static final String PATH_STEPS = "steps";      // what does this do?
    public static final String PATH_INGREDIENTS = "ingredients";      // what does this do?

    public static final class RecipeTable implements BaseColumns {

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";

        public static final String TABLE_NAME = "recipe";

        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_RECIPE_NAME = "recipe_name";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
        public static Uri buildRecipeUri(Long id) {
            return ContentUris.withAppendedId(BASE_CONTENT_URI, id);
        }
    }

    public static final class RecipeIngredientTable implements BaseColumns {
        public static final String TABLE_NAME = "recipe_ingredient";

        public static final String COLUMN_INGREDIENT_ID = "ingredient_id";
        public static final String COLUMN_INGREDIENT_NAME = "ingredient_ingredient";
        public static final String COLUMN_INGREDIENT_QUANTITY = "ingredient_quantity";
        public static final String COLUMN_INGREDIENT_MEASURE = "ingredient_measure";
        public static final String COLUMN_INGREDIENT_RECIPE_KEY = "ingredient_recipe";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();
    }

    public static final class RecipeStepTable implements BaseColumns {
        public static final String TABLE_NAME = "recipe_step";

        public static final String COLUMN_STEP_ID = "step_id";
        public static final String COLUMN_STEP_SHORT_DESCRIPTION = "step_shortDesciption";
        public static final String COLUMN_STEP_DESCRIPTION = "step_description";
        public static final String COLUMN_STEP_VIDEO_URL = "step_video_url";
        public static final String COLUMN_STEP_THUMBNAIL_URL = "step_thumbnail_url";
        public static final String COLUMN_STEP_RECIPE_KEY = "step_recipe";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STEPS).build();
    }

    public static Uri getRecipesList() {
        Uri returnedUri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
        Log.d(LOG_TAG, "getRecipesUri() constructed: " + returnedUri);
        return returnedUri;
    }

    public static Uri getRecipe(String recipeId) {
        Uri returnedUri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).appendPath(recipeId).build();
        Log.d(LOG_TAG, "getRecipe() constructed: " + returnedUri);
        return returnedUri;
    }

    public static String getRecipeId(Uri uri) {
        String recipeId = uri.getPathSegments().get(0);
        Log.d(LOG_TAG, "getRecipeId() constructed: " + recipeId);
        return recipeId;
    }
}
