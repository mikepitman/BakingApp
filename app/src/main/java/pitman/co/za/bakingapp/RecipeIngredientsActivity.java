package pitman.co.za.bakingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Michael on 2018/02/13.
 */

public class RecipeIngredientsActivity extends AppCompatActivity {

    private static String LOG_TAG = RecipeIngredientsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "onCreate() in RecipeIngredientsActivity");
        setContentView(R.layout.activity_ingredients_list);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.recipe_ingredients_list_container);

        if (fragment == null) {
            fragment = new RecipeIngredientsActivityFragment();
            fm.beginTransaction().add(R.id.recipe_ingredients_list_container, fragment).commit();
            Log.d(LOG_TAG, "new fragment added to fragmentManager");
        }
    }
}
