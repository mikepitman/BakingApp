package pitman.co.za.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import pitman.co.za.bakingapp.data.RecipeViewModel;
import pitman.co.za.bakingapp.domainObjects.Recipe;

/**
 * Created by Michael on 2018/02/13.
 */

public class RecipeSkeletonActivity extends AppCompatActivity implements RecipeSkeletonActivityFragment.Callbacks {

    private static String LOG_TAG = RecipeSkeletonActivity.class.getSimpleName();
    private Recipe selectedRecipe;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("selectedRecipe", selectedRecipe);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "onCreate() in RecipeSkeletonActivity");
        setContentView(R.layout.activity_recipe_skeleton);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.recipe_skeleton_container);

        if (fragment == null) {
            fragment = new RecipeSkeletonActivityFragment();
            fm.beginTransaction().add(R.id.recipe_skeleton_container, fragment).commit();
            Log.d(LOG_TAG, "new fragment added to fragmentManager");
        }
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        // todo: in tablet mode, selected recipe should be in master-detail view - skeleton master, recipe step/Ingredients in detail fragment
    }

//    @Override
//    protected boolean onOptionsItemSelected(MenuItem item) {
//
//    }

    public void onSkeletonItemSelected(int selectedItemPosition) {          // callbacks method to launch intent to view recipe ingredients or selected recipe step
        Log.d(LOG_TAG, "position returned: " + selectedItemPosition);

        Intent intent = getIntent();
        selectedRecipe = intent.getParcelableExtra("selectedRecipe");
        Log.d(LOG_TAG, "selected RecipeName " + selectedRecipe.getRecipeName());

        if (selectedItemPosition == 0) {        // View recipe ingredients
            Intent recipeIngredientsIntent = new Intent(this, RecipeIngredientsActivity.class);
            recipeIngredientsIntent.putExtra("recipe", selectedRecipe);
            startActivity(recipeIngredientsIntent);

        } else {                                // View selected recipe step
            Intent recipeStepIntent = new Intent(this, RecipeStepActivity.class);
            recipeStepIntent.putExtra("recipe", selectedRecipe);
            recipeStepIntent.putExtra("selectedStep", (selectedItemPosition - 1));  // ID of selected step
            startActivity(recipeStepIntent);
        }

        RecipeViewModel mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        mRecipeViewModel.setSelectedRecipe(selectedRecipe);
    }


//  Lifecycle events overridden to help follow flow-of-execution where necessary
    @Override
    public void onResume() {
        super.onResume();
//        Log.d(LOG_TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.d(LOG_TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.d(LOG_TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(LOG_TAG, "onDestroy()");
    }
}
