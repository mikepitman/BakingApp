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

    private String LOG_TAG = MainActivityFragment.class.getSimpleName();

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;      // returns de-referenced value, for different screen sizes
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "mainActivity being created");

        setContentView(getLayoutResId());
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new MainActivityFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    // Method called (via callbacks) from MainActivityFragment
    @Override
    public void onRecipeSelected(Recipe recipe) {

        /*Guidance in part from Android Programming 2nd Ed, The Big Nerd Ranch pg 307+,
        as well as a lot of other tutorial sites, which I failed to notarise at the time.*/
        if (findViewById(R.id.twopane_activity) == null) {                      // phone
            Log.d(LOG_TAG, "preparing to view recipe on phone");

            Intent recipeDetailIntent = new Intent(this, RecipeSkeletonActivity.class);
            recipeDetailIntent.putExtra("selectedRecipe", recipe);
            startActivity(recipeDetailIntent);
        } else {                                                                // tablet

            Log.d(LOG_TAG, "preparing to view recipe on tablet");
            Bundle arguments = new Bundle();
            arguments.putParcelable("selectedRecipe", recipe);

//            Fragment recipeDetailActivity = new RecipeSkeletonActivityFragment();
//            recipeDetailActivity.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, recipeDetailActivity).commit();
        }
    }
}
