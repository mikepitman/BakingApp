package pitman.co.za.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
    private RecyclerView mRecipeRecyclerView;
    public static RecipeSkeletonAdapter recipeSkeletonAdapter;
    private int selectedSkeletonPosition = 0;

    public RecipeSkeletonActivityFragment() {
        Log.d(LOG_TAG, "RecipeSkeletonActivityFragment() constructor called");
    }

    public interface Callbacks {
        void onSkeletonItemSelected(int selectedSkeletonPosition);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

        List<String> skeletonParts = new ArrayList<>();
        skeletonParts.add("Ingredients");
        for (RecipeStep step : selectedRecipe.getSteps()) {
            skeletonParts.add(step.getShortDescription());
        }
        recipeSkeletonAdapter = new RecipeSkeletonAdapter(skeletonParts);

        mRecipeRecyclerView = (RecyclerView) rootView.findViewById(R.id.recipe_skeleton_rv);
        mRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecipeRecyclerView.setAdapter(recipeSkeletonAdapter);

        return rootView;
    }

    private class RecipeSkeletonAdapter extends RecyclerView.Adapter<ViewHolder> {

        private String LOG_TAG = RecipeSkeletonActivityFragment.RecipeSkeletonAdapter.class.getSimpleName();
        private List<String> mRecipeSteps;

        private RecipeSkeletonAdapter(List<String> recipeRows) {
            this.mRecipeSteps = recipeRows;
        }

        @Override
        public int getItemCount() {
            if (mRecipeSteps == null) {
                return 0;
            }
            return mRecipeSteps.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View recipeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_skeleton_textview, parent, false);
            return new ViewHolder(recipeView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindText(mRecipeSteps.get(position), position); }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private int position;
        private TextView recipeListTextView;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            recipeListTextView = (TextView) itemView.findViewById(R.id.recipe_skeleton_textview);
        }

        @Override
        public void onClick(View view) {
            mCallbacks.onSkeletonItemSelected(position);
        }

        public void bindText(String recipeStep, int position) {
            this.recipeListTextView.setText(recipeStep);
            this.position = position;
        }
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
