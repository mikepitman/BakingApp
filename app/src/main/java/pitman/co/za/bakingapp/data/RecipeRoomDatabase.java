package pitman.co.za.bakingapp.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;

import pitman.co.za.bakingapp.domainObjects.Ingredient;
import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.domainObjects.RecipeStep;

/*
* Copied from codelabs - link below
* https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#6
* */

@Database(entities = {Recipe.class, RecipeStep.class, Ingredient.class}, version = 1)
public abstract class RecipeRoomDatabase extends RoomDatabase {

    public abstract RecipeDao recipeDao();

    private static RecipeRoomDatabase INSTANCE;

    static RecipeRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecipeRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecipeRoomDatabase.class, "recipe_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final RecipeDao mDao;

        PopulateDbAsync(RecipeRoomDatabase db) {
            mDao = db.recipeDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
//            mDao.deleteAll();

//            Word word = new Word("Hello");
//            mDao.insert(word);
//            word = new Word("World");
//            mDao.insert(word);

            // todo: put the database populating code in here

            return null;
        }
    }
}
