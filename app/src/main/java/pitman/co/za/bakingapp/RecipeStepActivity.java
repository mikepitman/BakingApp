
package pitman.co.za.bakingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


/**
 * Created by Michael on 2018/02/13.
 */

public class RecipeStepActivity extends AppCompatActivity {

    private static String LOG_TAG = RecipeStepActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "onCreate() in RecipeStepActivity");
        setContentView(R.layout.activity_recipe_step);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.recipe_step_container);

        if (fragment == null) {
            fragment = new RecipeStepActivityFragment();
            fm.beginTransaction().add(R.id.recipe_step_container, fragment).commit();
            Log.d(LOG_TAG, "new fragment added to fragmentManager");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(LOG_TAG, "onBackPressed in RecipeStepActivity");
    }
}
