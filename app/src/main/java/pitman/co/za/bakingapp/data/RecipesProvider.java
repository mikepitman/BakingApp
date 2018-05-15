package pitman.co.za.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Michael on 2018/03/14.
 * Attribution: Based largely on provider in submission for PopularMovies,
 * in turn based foundationally on WeatherProvider class in Sunshine weather app provided by Udacity
 */

public class RecipesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static String LOG_TAG = RecipesProvider.class.getSimpleName();
    private RecipesDbHelper mRecipesDbHelper;

    static final int RECIPE_ALL = 100;
    static final int RECIPE_SPECIFIC = 101;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipesContract.CONTENT_AUTHORITY;

        // https://developer.android.com/guide/topics/providers/content-provider-creating explains UriMatcher functionalit
        matcher.addURI(authority, RecipesContract.PATH_RECIPES + "/", RECIPE_ALL);
        matcher.addURI(authority, RecipesContract.PATH_RECIPES + "/#", RECIPE_SPECIFIC);

        return matcher;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// Implementations of parent-class abstract methods ////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreate() {
        mRecipesDbHelper = new RecipesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is, and query the database accordingly.
        Cursor returnCursor;
        int matchValue = buildUriMatcher().match(uri);

        switch (matchValue) {
            // "recipes/"
            case RECIPE_ALL: {
                Log.d(LOG_TAG, "RECIPE_ALL / 100 " + uri);
                returnCursor = mRecipesDbHelper.getReadableDatabase().query(
                        RecipesContract.RecipeTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            // "recipe/#"
            case RECIPE_SPECIFIC: {
                // get the selected recipe.
                Log.d(LOG_TAG, "RECIPE_SPECIFIC / 101");

                returnCursor = mRecipesDbHelper.getReadableDatabase().query(
                        RecipesContract.RecipeTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                Log.d(LOG_TAG, "something done fucked up: " + uri);
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    // Defines the tye of cursor returned by each URI - multiple results (directory) or single result
    @Nullable
    @Override
    public String getType(Uri uri) {

        final int uriMatch = sUriMatcher.match(uri);

        switch (uriMatch) {
            case RECIPE_ALL:
                return RecipesContract.RecipeTable.CONTENT_TYPE;
            case RECIPE_SPECIFIC:
                return RecipesContract.RecipeTable.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI!: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

/* Modified code example from Sunshine App */
        final SQLiteDatabase db = mRecipesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RECIPE_ALL: {
                Log.d(LOG_TAG, "inserting for URI matching 'RECIPE_SPECIFIC'");

                long _id = db.insert(RecipesContract.RecipeTable.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = RecipesContract.RecipeTable.buildRecipeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " match: " + match);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(LOG_TAG, "returnURI: " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Code from SunShine
        final SQLiteDatabase db = mRecipesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case RECIPE_SPECIFIC:
                rowsDeleted = db.delete(
                        RecipesContract.RecipeTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
