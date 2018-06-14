
package pitman.co.za.bakingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import pitman.co.za.bakingapp.domainObjects.Recipe;


/**
 * Created by Michael on 2018/02/13.
 */

public class RecipeStepActivity extends AppCompatActivity implements RecipeStepActivityFragment.Callbacks {

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
    public void onRecipeStepChange(Recipe selectedRecipe, int newStepNumber) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("selectedRecipe", selectedRecipe);
        bundle.putInt("selectedItemPosition", (newStepNumber));
        bundle.putBoolean("isTablet", getResources().getBoolean(R.bool.is_tablet));

        FragmentManager fm = getSupportFragmentManager();

        // https://stackoverflow.com/questions/15857760/removing-fragments-from-an-activity
        if (fm.findFragmentById(R.id.recipe_step_container) != null) {
            fm.beginTransaction().remove(fm.findFragmentById(R.id.recipe_step_container)).commit();
        }

        Fragment fragment = new RecipeStepActivityFragment();
        fragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.recipe_step_container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(LOG_TAG, "onBackPressed in RecipeStepActivity");
    }

    public void previousStep() {
        Log.d(LOG_TAG, "button for previous step pressed");
    }

    public void nextStep() {
        Log.d(LOG_TAG, "button for next step pressed");
    }
}
