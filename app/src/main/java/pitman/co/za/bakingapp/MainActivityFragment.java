package pitman.co.za.bakingapp;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pitman.co.za.bakingapp.data.RecipeViewModel;
import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.utility.FetchRecipesTask;

/**
 * A placeholder fragment containing a simple view.
 */
//public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
public class MainActivityFragment extends Fragment {

    private static String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private Context mContext;
    private RecyclerView mRecipeRecyclerView;
    private Callbacks mCallbacks;
    private RecipeViewModel mRecipeViewModel;
    public static RecipeCardAdapter mAdapter;

    public MainActivityFragment() {
        Log.d(LOG_TAG, "MainActivityFragment constructor called");
    }

    //// Callbacks-related code //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public interface Callbacks {
        void onRecipeSelected(Recipe recipe);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
//// Callbacks-related code //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.setHasOptionsMenu(true);
        Log.d(LOG_TAG, "2. onCreate()");

        // https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#13
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        mRecipeViewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable final List<Recipe> recipes) {
                mAdapter.swapData(recipes);
                for (Recipe recipe : recipes) {
                    mRecipeViewModel.loadRecipeAttributes(recipe);
                }
            }
        });
        mAdapter = new RecipeCardAdapter(mRecipeViewModel.getAllRecipes().getValue());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "3. onCreateView()");

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_cards_rv);
        mRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecipeRecyclerView.setAdapter(mAdapter);

        if (isNetworkAvailable()) {
            new FetchRecipesTask(this).execute();
        } else {
            appMessageToast("No network connection available! Displaying recipes from memory.");
        }
        return view;
    }

    // Applicable app status messages passed to user in form on toast
    private void appMessageToast(String message) {
        Context context = getActivity();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    // determines whether network connection is available.
    private boolean isNetworkAvailable() {
        /* http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html */
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connectivity = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
        if (!connectivity) {
            Log.d(LOG_TAG, "No network connection!");
            appMessageToast("No network connection! You need to be online!");
        }
        return connectivity;
    }

    // called from async FetchRecipesTask that retrieves recipe data from internet, to populate adapter
    public void generateRecipeAdapterWithData(ArrayList<Recipe> retrievedRecipes) {
        mRecipeViewModel.insert(retrievedRecipes);
    }

    private class RecipeCardAdapter extends RecyclerView.Adapter<ViewHolder> {

        private String LOG_TAG = RecipeCardAdapter.class.getSimpleName();
        private List<Recipe> mRecipeListing;

        private RecipeCardAdapter(List<Recipe> recipeListing) {
            mRecipeListing = recipeListing;
        }

        @Override
        public int getItemCount() {
            if (mRecipeListing == null) {
                Log.d(LOG_TAG, "number of items is 0, this should not happen!");
                return 0;
            }
            return mRecipeListing.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card_recipe_name, parent, false);
            Log.d(LOG_TAG, "onCreateViewHolder - viewHolder created");
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.d(LOG_TAG, "calling onBindViewHolder");
            holder.bindRecipe(mRecipeListing.get(position));
        }

        public void swapData(List<Recipe> recipes) {
            int numberOfOldEntries = mRecipeListing == null ? 0 : mRecipeListing.size();
            if (mRecipeListing != null) {
                mRecipeListing.clear();
            } else {
                mRecipeListing = new ArrayList<Recipe>();
            }
            mRecipeListing.addAll(recipes);
            Log.d(LOG_TAG, "data swapped out! " + numberOfOldEntries + " items removed, items added: " + recipes.size());
            notifyDataSetChanged();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Recipe mRecipe;
        private TextView recipeListTextView;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            recipeListTextView = (TextView) itemView.findViewById(R.id.recipe_card);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), this.recipeListTextView.getText() + " selected", Toast.LENGTH_SHORT).show();
            mCallbacks.onRecipeSelected(mRecipe);
        }

        public void bindRecipe(Recipe recipe) {
            this.mRecipe = recipe;
            this.recipeListTextView.setText(mRecipe.getRecipeName());
        }
    }
}
