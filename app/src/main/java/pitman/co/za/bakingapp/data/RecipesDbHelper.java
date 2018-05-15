package pitman.co.za.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Michael on 2018/03/14.
 * MoviesDbHelper from my PopularMovies submission used as template
 */

/*  Note to self: this is like a liquibase config, outlining database structure for stored information in the app */

public class RecipesDbHelper extends SQLiteOpenHelper {

    // todo: increment this value each time there's a change in DB schema
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "recipes.db";

    // Constructor
    public RecipesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + RecipesContract.RecipeTable.TABLE_NAME + " (" +
                RecipesContract.RecipeTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +   // database key independent of recipe key

                RecipesContract.RecipeTable.COLUMN_RECIPE_ID + " TEXT NOT NULL, " +  // UNIQUE constraint left off - IDs may be duplicated if retrieved from multiple sources
                RecipesContract.RecipeTable.COLUMN_RECIPE_NAME + " TEXT NOT NULL)";

        final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " + RecipesContract.RecipeIngredientTable.TABLE_NAME + " (" +
                RecipesContract.RecipeIngredientTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                RecipesContract.RecipeIngredientTable.COLUMN_INGREDIENT_ID + " TEXT NOT NULL, " +
                RecipesContract.RecipeIngredientTable.COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
                RecipesContract.RecipeIngredientTable.COLUMN_INGREDIENT_MEASURE + " TEXT NOT NULL, " +
                RecipesContract.RecipeIngredientTable.COLUMN_INGREDIENT_QUANTITY + " TEXT NOT NULL, " +
                RecipesContract.RecipeIngredientTable.COLUMN_INGREDIENT_RECIPE_KEY + " INTEGER NOT NULL, " +

                // Foreign key ref from ingredient to linked recipe
                "FOREIGN KEY (" + RecipesContract.RecipeIngredientTable.COLUMN_INGREDIENT_RECIPE_KEY + ") REFERENCES " +
                RecipesContract.RecipeTable.TABLE_NAME + " (" + RecipesContract.RecipeTable._ID + "))";

        final String SQL_CREATE_STEPS_TABLE = "CREATE TABLE " + RecipesContract.RecipeStepTable.TABLE_NAME + " (" +
                RecipesContract.RecipeStepTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                RecipesContract.RecipeStepTable.COLUMN_STEP_ID + " TEXT NOT NULL, " +
                RecipesContract.RecipeStepTable.COLUMN_STEP_DESCRIPTION + " TEXT NOT NULL, " +
                RecipesContract.RecipeStepTable.COLUMN_STEP_SHORT_DESCRIPTION + "TEXT NOT NULL, " +
                RecipesContract.RecipeStepTable.COLUMN_STEP_VIDEO_URL + " TEXT NOT NULL, " +
                RecipesContract.RecipeStepTable.COLUMN_STEP_THUMBNAIL_URL + " TEXT NOT NULL, " +
                RecipesContract.RecipeStepTable.COLUMN_STEP_RECIPE_KEY + " INTEGER NOT NULL, " +

                "FOREIGN KEY (" + RecipesContract.RecipeStepTable.COLUMN_STEP_RECIPE_KEY + ") REFERENCES " +
                RecipesContract.RecipeTable.TABLE_NAME + " (" + RecipesContract.RecipeTable._ID + "))";

        db.execSQL(SQL_CREATE_RECIPE_TABLE);
        db.execSQL(SQL_CREATE_STEPS_TABLE);
        db.execSQL(SQL_CREATE_INGREDIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipesContract.RecipeTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecipesContract.RecipeStepTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecipesContract.RecipeIngredientTable.TABLE_NAME);
    }
}
