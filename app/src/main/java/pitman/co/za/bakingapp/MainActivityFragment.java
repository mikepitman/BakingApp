package pitman.co.za.bakingapp;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pitman.co.za.bakingapp.data.RecipeViewModel;
import pitman.co.za.bakingapp.domainObjects.Ingredient;
import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.domainObjects.RecipeStep;
import pitman.co.za.bakingapp.jsonDeserialisation.IngredientDeserialiser;
import pitman.co.za.bakingapp.jsonDeserialisation.RecipeDeserialiser;
import pitman.co.za.bakingapp.jsonDeserialisation.StepDeserialiser;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private Context mContext;
    private RecyclerView mRecipeRecyclerView;
    private Callbacks mCallbacks;
    private RecipeViewModel mRecipeViewModel;
    private boolean recipesUpdated = false;
    public static RecipeCardAdapter mAdapter;

    public MainActivityFragment() {
    }

    //// Callbacks-related code //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("recipesUpdated", recipesUpdated);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "3. onCreateView()");

        if (savedInstanceState != null) {
            recipesUpdated = savedInstanceState.getBoolean("recipesUpdated");
        }

        // Get bundle arguments from MainActivity
        boolean isTablet = false;
        Bundle arguments = this.getArguments();
        if (arguments != null) {
            isTablet = arguments.getBoolean("isTablet");
        }

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_cards_rv);
        mRecipeRecyclerView.setLayoutManager(isTablet ? new GridLayoutManager(getActivity(), 2) : new LinearLayoutManager(getActivity()));
        mRecipeRecyclerView.setAdapter(mAdapter);

        if (!recipesUpdated) {
            if (isNetworkAvailable()) {
//            new FetchRecipesTask(this).execute();     // deprecated by change to Volley for network requests
                queryNetworkForRecipes();
            } else {
                appMessageToast("No network connection available! Displaying recipes from memory.");
            }
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

    // previously called from async FetchRecipesTask that retrieves recipe data from internet, to populate adapter
    // now called from queryNetworkForRecipes()
    public void generateRecipeAdapterWithData(ArrayList<Recipe> retrievedRecipes) {
        if (retrievedRecipes != null) {
            mRecipeViewModel.insert(retrievedRecipes);
        }
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
            View recipeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card_recipe_name, parent, false);
            return new ViewHolder(recipeView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindRecipe(mRecipeListing.get(position));
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public void swapData(List<Recipe> recipes) {
            int numberOfOldEntries = mRecipeListing == null ? 0 : mRecipeListing.size();
            if (mRecipeListing != null) {
                mRecipeListing.clear();
            } else {
                mRecipeListing = new ArrayList<Recipe>();
            }
            mRecipeListing.addAll(recipes);
            notifyDataSetChanged();
        }
    }

    /* Built using example at http://www.mkyong.com/android/android-gridview-example/ */
    private class RecipeGridAdapter extends BaseAdapter {

        private List<Recipe> mRecipeListing;
        private String LOG_TAG = RecipeGridAdapter.class.getSimpleName();

        public RecipeGridAdapter(List<Recipe> recipes) {
            this.mRecipeListing = recipes;
        }

        @Override
        public int getCount() {
            return mRecipeListing.size();
        }

        @Override
        public Object getItem(int position) {
            return mRecipeListing.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View gridview;

            if (convertView == null) {
                gridview = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card_recipe_name, parent, false);
            } else {
                gridview = (View) convertView;
            }

            return gridview;
        }

        public void swapData(List<Recipe> recipeListing) {
            if (mRecipeListing != null) {
                mRecipeListing.clear();
            } else {
                mRecipeListing = new ArrayList<Recipe>();
            }
            mRecipeListing.addAll(recipeListing);
            notifyDataSetChanged();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Recipe mRecipe;
        private TextView recipeListTextView;
        private ImageView recipeImageView;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            recipeListTextView = (TextView) itemView.findViewById(R.id.recipe_card);
            recipeImageView = (ImageView) itemView.findViewById(R.id.recipe_image);
        }

        @Override
        public void onClick(View view) {
            mCallbacks.onRecipeSelected(mRecipe);
        }

        public void bindRecipe(Recipe recipe) {
            this.mRecipe = recipe;
            this.recipeListTextView.setText(mRecipe.getName());

            if (!mRecipe.getImage().isEmpty()) {
                Uri imageUri = Uri.parse(mRecipe.getImage());
                this.recipeImageView.setImageURI(imageUri);
            } else {
                this.recipeImageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Parse returned JSON-encoded movie data and populate objects.
     * Reviewer suggested using library (GSON) for parsing input. Resources referenced:
     * https://github.com/google/gson
     * http://www.javacreed.com/gson-deserialiser-example/
     */
    private ArrayList<Recipe> parseRecipesFromJson(String jsonRecipes) throws JsonParseException {

        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Recipe.class, new RecipeDeserialiser());
        builder.registerTypeAdapter(RecipeStep.class, new StepDeserialiser());
        builder.registerTypeAdapter(Ingredient.class, new IngredientDeserialiser());
        Gson gson = builder.create();

        Recipe[] parsedRecipesArray = gson.fromJson(jsonRecipes, Recipe[].class);
        return new ArrayList<>(Arrays.asList(parsedRecipesArray));
    }

    /*
    * Another suggestion by reviewer - switch from very manual network query, to using Volley (or retrofit).
    * Significantly faster and easier to implement, after some reading
    * Some of the resources consulted:
    * https://developer.android.com/training/volley/request-custom
    * https://dzone.com/articles/android-%E2%80%93-volley-library
    */
    private void queryNetworkForRecipes() {
        String url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Deserialise the json recipe information into Java objects
                        try {
                            generateRecipeAdapterWithData(parseRecipesFromJson(response));
                            recipesUpdated = true;
                        } catch (JsonParseException e) {
                            Log.e(LOG_TAG, "JsonParseException caught: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "Error encountered when querying recipes listing online. " + error.getLocalizedMessage());
                appMessageToast("There was an error querying the online recipes repository!");
            }
        });
        requestQueue.add(request);
    }
}
