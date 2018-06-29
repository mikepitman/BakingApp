package pitman.co.za.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.ButterKnife;
import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.widget.RecipeWidgetProvider;


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

        ButterKnife.bind(this);

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
        Intent recipeDetailIntent = new Intent(this, RecipeSkeletonActivity.class);
        recipeDetailIntent.putExtra("selectedRecipe", recipe);
        recipeDetailIntent.putExtra("isTablet", mIsTablet);
        startActivity(recipeDetailIntent);

        // Once recipe is selected, update the widget with ingredients for the newly selected recipe
        // https://stackoverflow.com/questions/3455123/programmatically-update-widget-from-activity-service-receiver
        Intent updateWidgetIntent = new Intent(this, RecipeWidgetProvider.class);
        updateWidgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        SharedPreferences sharedPreferences = this.getSharedPreferences("pitman.co.za.bakingapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("selectedRecipe", recipe.getRecipeName()).apply();

        this.sendBroadcast(updateWidgetIntent);
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
        public void onResume () {
            super.onResume();
            Log.d(LOG_TAG, "onResume()");
        }

        @Override
        public void onPause () {
            super.onPause();
            Log.d(LOG_TAG, "onPause()");
        }

        @Override
        public void onStop () {
            super.onStop();
            Log.d(LOG_TAG, "onStop()");
        }

        @Override
        public void onDestroy () {
            super.onDestroy();
            Log.d(LOG_TAG, "onDestroy()");
        }
    }
