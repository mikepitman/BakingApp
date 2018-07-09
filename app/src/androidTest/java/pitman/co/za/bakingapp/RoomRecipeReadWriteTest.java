package pitman.co.za.bakingapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import pitman.co.za.bakingapp.data.RecipeDao;
import pitman.co.za.bakingapp.data.RecipeRoomDatabase;
import pitman.co.za.bakingapp.domainObjects.Recipe;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/*
* Source example for this unit test:
* https://developer.android.com/training/data-storage/room/testing-db
* path /src/androidTest/java is the correct path - compile issues encountered with test under /src/test/
*/

@RunWith(AndroidJUnit4.class)
public class RoomRecipeReadWriteTest {

    private RecipeDao mRecipeDao;
    private RecipeRoomDatabase mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, RecipeRoomDatabase.class).build();
        mRecipeDao = mDb.recipeDao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void saveRecipeAndRetrieve() throws Exception {
        Recipe recipe = getRecipe();
        mRecipeDao.saveRecipe(recipe);

        Recipe recipeByName = mRecipeDao.getRecipe("Test Recipe");
        assertThat(recipeByName.getId(), equalTo(recipe.getId()));
        assertThat(recipeByName.getServings(), equalTo(recipe.getServings()));
    }

    private Recipe getRecipe() {
        return new Recipe("id", "Test Recipe", "8", "");
    }
}
