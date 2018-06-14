package pitman.co.za.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.domainObjects.RecipeStep;

/**
 * Created by Michael on 2018/02/13.
 */

public class RecipeSkeletonActivityFragment extends Fragment {

    private static String LOG_TAG = RecipeSkeletonActivityFragment.class.getSimpleName();
    private Callbacks mCallbacks;
    private Recipe selectedRecipe;
    private Context mContext;
    private View rootView;
    public static ArrayAdapter<String> recipeSkeletonAdapter;
    private int selectedSkeletonPosition = 0;

    public RecipeSkeletonActivityFragment() {
        Log.d(LOG_TAG, "RecipeSkeletonActivityFragment() constructor called");
    }

    public interface Callbacks {
        void onSkeletonItemSelected(int selectedSkeletonPosition);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        mContext = this.getActivity().getApplicationContext();
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("selectedRecipe", selectedRecipe);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_recipe_skeleton, container, false);
        Log.d(LOG_TAG, "onCreateView() in RecipeSkeletonActivityFragment called");

        if (savedInstanceState != null) {
            selectedRecipe = savedInstanceState.getParcelable("selectedRecipe");
            Log.d(LOG_TAG, "recipe retrieved from savedInstanceState");

        } else {
            Intent intent = getActivity().getIntent();
            if ((intent != null)) {
                // Recipe object is parcelable, no need to separately add step or ingredient objects
                selectedRecipe = intent.getParcelableExtra("selectedRecipe");
                Log.d(LOG_TAG, "In RecipeSkeletonActivityFragment, selectedRecipe is null? " + (selectedRecipe == null));
            }
        }

        recipeSkeletonAdapter = new ArrayAdapter<String>(
                this.getActivity().getApplicationContext(),
                R.layout.recipe_skeleton_textview,
                R.id.recipe_skeleton_textview,
                new ArrayList<String>());
        recipeSkeletonAdapter.clear();

        recipeSkeletonAdapter.add("Ingredients");
        for (RecipeStep step : selectedRecipe.getRecipeSteps()) {
            String recipeCardText = step.getShortDescription();
            recipeSkeletonAdapter.add(recipeCardText);
        }

        ListView listView = (ListView) rootView.findViewById(R.id.recipe_skeleton_list);
        listView.setAdapter(recipeSkeletonAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSkeletonPosition = position;
                mCallbacks.onSkeletonItemSelected(selectedSkeletonPosition);
            }
        });

        return rootView;
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
