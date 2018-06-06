package pitman.co.za.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import pitman.co.za.bakingapp.domainObjects.Recipe;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callbacks {

    private String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mIsTablet;

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_main;      // returns de-referenced value, for different screen sizes - not strictly necessary here
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "mainActivity being created");

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        int layoutResId = getLayoutResId();
        setContentView(layoutResId);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        mIsTablet = getResources().getBoolean(R.bool.is_tablet);

        if (fragment == null) {
            fragment = new MainActivityFragment();

            Bundle bundle = new Bundle();
            bundle.putBoolean("isTablet", mIsTablet);
            fragment.setArguments(bundle);

            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    // Method called (via callbacks) from MainActivityFragment
    @Override
    public void onRecipeSelected(Recipe recipe) {

        /*Guidance in part from Android Programming 2nd Ed, The Big Nerd Ranch pg 307+,
        as well as a lot of other tutorial sites, which I failed to notarise at the time.*/
//        if (!mIsTablet) {                                                     // phone
//            Log.d(LOG_TAG, "preparing to view recipe on phone");

            Intent recipeDetailIntent = new Intent(this, RecipeSkeletonActivity.class);
            recipeDetailIntent.putExtra("selectedRecipe", recipe);
            recipeDetailIntent.putExtra("isTablet", mIsTablet);
            startActivity(recipeDetailIntent);
//        } else {                                                                // tablet
//
//            Log.d(LOG_TAG, "preparing to view recipe on tablet");
//            Bundle arguments = new Bundle();
//            arguments.putParcelable("selectedRecipe", recipe);
//
//            Fragment recipeDetailActivity = new RecipeSkeletonActivityFragment();
//            recipeDetailActivity.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, recipeDetailActivity).commit();
//        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// Activity lifecycle methods for debugging/understanding/etc //////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(LOG_TAG, "onNewIntent()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy()");
    }
}
