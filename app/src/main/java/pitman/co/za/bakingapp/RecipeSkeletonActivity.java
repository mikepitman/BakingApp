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

public class RecipeSkeletonActivity extends AppCompatActivity implements RecipeSkeletonActivityFragment.Callbacks, RecipeStepActivityFragment.Callbacks {

    private static String LOG_TAG = RecipeSkeletonActivity.class.getSimpleName();
    private Recipe selectedRecipe;
    private boolean mIsTablet;
    private int selectedItemPosition = 0;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("selectedRecipe", selectedRecipe);
        outState.putBoolean("isTablet", mIsTablet);
        outState.putInt("selectedItemPosition", selectedItemPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            this.selectedRecipe = savedInstanceState.getParcelable("selectedRecipe");
            this.mIsTablet = savedInstanceState.getBoolean("isTablet");
            this.selectedItemPosition = savedInstanceState.getInt("selectedItemPosition");

        } else {
            Intent intent = getIntent();
            mIsTablet = intent.getBooleanExtra("isTablet", false);
            selectedRecipe = intent.getParcelableExtra("selectedRecipe");
            Log.d(LOG_TAG, "selected RecipeName " + selectedRecipe.getRecipeName());
        }

        setContentView(R.layout.activity_recipe_skeleton);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.recipe_skeleton_container);

        if (fragment == null) {
            fragment = new RecipeSkeletonActivityFragment();
            fm.beginTransaction().add(R.id.recipe_skeleton_container, fragment).commit();
            Log.d(LOG_TAG, "new fragment added to fragmentManager");
        }

        if (mIsTablet) {
            if (savedInstanceState == null) {
                if (selectedItemPosition == 0) {
                    Fragment ingredientsFragment = new RecipeIngredientsActivityFragment();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("selectedRecipe", selectedRecipe);
                    ingredientsFragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.selected_recipe_field, ingredientsFragment, "RECIPE_INGREDIENTS_TAG")
                            .commit();
                } else {
                    Fragment recipeStepFragment = new RecipeStepActivityFragment();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("selectedRecipe", selectedRecipe);
                    bundle.putInt("selectedItemPosition", (selectedItemPosition - 1));
                    recipeStepFragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_step_container, recipeStepFragment, "RECIPE_STEP_TAG")
                            .commit();
                }
            }
        }
    }

//    @Override
//    protected boolean onOptionsItemSelected(MenuItem item) {
//
//    }

    public void onSkeletonItemSelected(int selectedItemPosition) {          // callbacks method to launch intent to view recipe ingredients or selected recipe step
        Log.d(LOG_TAG, "position returned: " + selectedItemPosition);

        if (mIsTablet) {
            Fragment recipeStepFragment = new RecipeStepActivityFragment();

            Bundle bundle = new Bundle();
            bundle.putParcelable("selectedRecipe", selectedRecipe);
            bundle.putInt("selectedItemPosition", (selectedItemPosition - 1));
            recipeStepFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.selected_recipe_field, recipeStepFragment, "RECIPE_STEP_TAG")
                    .commit();

        } else {
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
        }

        RecipeViewModel mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        mRecipeViewModel.setSelectedRecipe(selectedRecipe);
    }

    // This should never be called
    @Override
    public void onRecipeStepChange(Recipe selectedRecipe, int newStepNumber) {}

//  Lifecycle events overridden to help follow flow-of-execution where necessary
    @Override
    public void onResume() {
        super.onResume();
//        Log.d(LOG_TAG, "onResume()");

        // todo: from Sunshine, put method on RecipeIngredientsFragmentActivity to set ingredients list, called from this lifecycle method
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
