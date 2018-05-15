package pitman.co.za.bakingapp;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import pitman.co.za.bakingapp.data.RecipesContract;
import pitman.co.za.bakingapp.domainObjects.Ingredient;
import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.domainObjects.RecipeStep;
import pitman.co.za.bakingapp.utility.FetchRecipesTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private Context mContext;
    private ArrayList<Recipe> mRecipeListData;
    private RecyclerView mRecipeRecyclerView;
    private Callbacks mCallbacks;
    //    public View view;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "3. onCreateView()");

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_cards_rv);
        mRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (isNetworkAvailable()) {
            new FetchRecipesTask(this).execute();
        } else {
            appMessageToast("No network connection available! Displaying recipes from memory.");
            // this would then retrieve data from database
//            populateRecipesAdapterWithOfflineData();

            Uri recipesListing = RecipesContract.getRecipesList();
            Log.d(LOG_TAG, "retrieving from database using uri: " + recipesListing);
            Cursor cursor = getActivity().getContentResolver().query(recipesListing, null, null, null, null);
            if (cursor != null) {
                getLoaderManager().restartLoader(1, null, MainActivityFragment.this);
                cursor.close();
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

    // called from async task that retrieves recipe data from internet, to populate adapter
    public void generateRecipeAdapterWithData(List<Recipe> retrievedRecipes) {
        if (mAdapter == null) {
            mAdapter = new RecipeCardAdapter(retrievedRecipes);
            mRecipeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.swapData(retrievedRecipes);
        }

        // call to method to update database in background
//        if (retrievedRecipes != null) {
//            updateDatabaseWithRetrievedRecipes(retrievedRecipes);
//        }
    }

    /* Use ContentProviderOperation object to save the recipe to database
    https://stuff.mit.edu/afs/sipb/project/android/docs/reference/android/provider/ContactsContract.RawContacts.html
    https://www.grokkingandroid.com/better-performance-with-contentprovideroperation/
    */
    // todo: this needs logic to only insert if not already present in the database
    public void updateDatabaseWithRetrievedRecipes(List<Recipe> retrievedRecipes) {
        ContentResolver resolver = getActivity().getContentResolver();

        for (Recipe recipe : retrievedRecipes) {

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            int rawRecipeInsertIndex = ops.size();
            ops.add(ContentProviderOperation.newInsert(RecipesContract.RecipeTable.CONTENT_URI)
                    .withValue(RecipesContract.RecipeTable.COLUMN_RECIPE_ID, recipe.getRecipeId())
                    .withValue(RecipesContract.RecipeTable.COLUMN_RECIPE_NAME, recipe.getRecipeName())
                    .build());

            for (RecipeStep step : recipe.getRecipeSteps()) {
                ops.add(ContentProviderOperation.newInsert(RecipesContract.RecipeStepTable.CONTENT_URI)
                        .withValueBackReference(RecipesContract.RecipeStepTable.COLUMN_STEP_RECIPE_KEY, rawRecipeInsertIndex)
                        .withValue(RecipesContract.RecipeStepTable.COLUMN_STEP_DESCRIPTION, step.getDescription())
                        .withValue(RecipesContract.RecipeStepTable.COLUMN_STEP_SHORT_DESCRIPTION, step.getShortDesciption())
                        .withValue(RecipesContract.RecipeStepTable.COLUMN_STEP_VIDEO_URL, step.getVideoUrl())
                        .withValue(RecipesContract.RecipeStepTable.COLUMN_STEP_THUMBNAIL_URL, step.getThumbnailUrl())
                        .build());
            }

            for (Ingredient ingredient : recipe.getIngredients()) {
                ops.add(ContentProviderOperation.newInsert(RecipesContract.RecipeIngredientTable.CONTENT_URI)
                        .withValueBackReference(RecipesContract.RecipeIngredientTable.COLUMN_INGREDIENT_RECIPE_KEY, rawRecipeInsertIndex)
                        .withValue(RecipesContract.RecipeIngredientTable.COLUMN_INGREDIENT_QUANTITY, ingredient.getQuantity())
                        .withValue(RecipesContract.RecipeIngredientTable.COLUMN_INGREDIENT_NAME, ingredient.getIngredient())
                        .withValue(RecipesContract.RecipeIngredientTable.COLUMN_INGREDIENT_MEASURE, ingredient.getMeasure())
                        .build());
            }

            try {
                resolver.applyBatch(RecipesContract.CONTENT_AUTHORITY, ops);
                Log.d(LOG_TAG, "resolver.applyBatch() run by this point");
            } catch (RemoteException e) {
                Log.d(LOG_TAG, e.getMessage());
            } catch (OperationApplicationException e) {
                Log.d(LOG_TAG, e.getMessage());
            }

//            ArrayList<ContentValues> contentValuesArrayList = new ArrayList<>();
//
//            ContentValues recipeContentValuesObject = new ContentValues();
//            recipeContentValuesObject.put(RecipesContract.RecipeTable.COLUMN_RECIPE_ID, recipe.getRecipeId());
//            recipeContentValuesObject.put(RecipesContract.RecipeTable.COLUMN_RECIPE_NAME, recipe.getRecipeName());
//            contentValuesArrayList.add(recipeContentValuesObject);

            // todo: check if recipe exists already, and don't add it if it does
//            Log.d(LOG_TAG, "adding recipe to database");
//            Uri uri = resolver.insert(RecipesContract.RecipeTable.CONTENT_URI, recipeContentValuesObject);
//            if (uri == null) {
//                Log.d(LOG_TAG, "Uri was null - this shouldn't really happen(?)");
//            } else {
//                Log.d(LOG_TAG, "Recipe with id " + recipe.getRecipeName() + " was added to database. " + uri);
//            }
        }
    }

//    private void populateRecipesAdapterWithOfflineData() {
////        recipesAdapter = new ArrayAdapter<Recipe>(mContext, R.layout.recipe_list_recipe_title, R.id.recipe_list_recipe_title_textview, new ArrayList<Recipe>());
////        recipesAdapter.clear();
//
//        ArrayList<Ingredient> ingredients = new ArrayList<>();
//        Ingredient ing1 = new Ingredient();
//        ing1.setIngredient("Ing 1");
//        ing1.setMeasure("Meas 1");
//        ing1.setQuantity("1");
//
//        Ingredient ing2 = new Ingredient();
//        ing2.setIngredient("Ing 2");
//        ing2.setMeasure("Meas 2");
//        ing2.setQuantity("2");
//
//        ingredients.add(ing1);
//        ingredients.add(ing2);
//
//        ArrayList<RecipeStep> steps = new ArrayList<>();
//        RecipeStep step1 = new RecipeStep();
//        step1.setId("1");
//        step1.setShortDesciption("Step 1");
//        step1.setShortDesciption("Recipe Step 1");
//
//        RecipeStep step2 = new RecipeStep();
//        step2.setId("2");
//        step2.setShortDesciption("Step 2");
//        step2.setShortDesciption("Recipe Step 2");
//
//        RecipeStep step3 = new RecipeStep();
//        step3.setId("3");
//        step3.setShortDesciption("Step 3");
//        step3.setShortDesciption("Recipe Step 3");
//
//        RecipeStep step4 = new RecipeStep();
//        step4.setId("4");
//        step4.setShortDesciption("Step 4");
//        step4.setShortDesciption("Recipe Step 4");
//
//        steps.add(step1);
//        steps.add(step2);
//        steps.add(step3);
//        steps.add(step4);
//
//        Recipe recipe1 = new Recipe();
//        recipe1.setRecipeId("1");
//        recipe1.setRecipeName("Test Recipe ONE");
//        recipe1.setIngredients(ingredients);
//        recipe1.setRecipeSteps(steps);
//
//        Recipe recipe2 = new Recipe();
//        recipe2.setRecipeId("2");
//        recipe2.setRecipeName("Test Recipe TWO");
//        recipe2.setIngredients(ingredients);
//        recipe2.setRecipeSteps(steps);
//
//        Recipe recipe3 = new Recipe();
//        recipe3.setRecipeId("3");
//        recipe3.setRecipeName("Test Recipe THREE");
//        recipe3.setIngredients(ingredients);
//        recipe3.setRecipeSteps(steps);
//
//        List<Recipe> recipeList = new ArrayList<>();
//        recipeList.add(recipe1);
//        recipeList.add(recipe2);
//        recipeList.add(recipe3);
//
//        mAdapter = new RecipeCardAdapter(recipeList);
//        mRecipeRecyclerView.setAdapter(mAdapter);
//    }

    private class RecipeCardAdapter extends RecyclerView.Adapter<ViewHolder> {

        private String LOG_TAG = RecipeCardAdapter.class.getSimpleName();
        private List<Recipe> mRecipeListing;

        private RecipeCardAdapter(List<Recipe> recipeListing) {
            mRecipeListing = recipeListing;
//            Log.d(LOG_TAG, "recipeListing size: " + mRecipeListing.size());
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
            int numberOfOldEntries = mRecipeListing.size();
            mRecipeListing.clear();
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

    // Example code from Sunshine II app, and implementation guidance from https://github.com/Vane101/Vmovie
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.d(LOG_TAG, "OnCreateLoader called, to return new Loader");
        Uri recipeListingUri = RecipesContract.getRecipesList();
        return new CursorLoader(getActivity(),
                recipeListingUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Recipe> recipeList = new ArrayList<>();
        if (data.getCount() > 0) {//check if cursor not empty
            data.moveToFirst();
            do {
                Recipe recipe = new Recipe();
                recipe.setRecipeId(data.getString(1));
                recipe.setRecipeName(data.getString(2));

                Log.d(LOG_TAG, "loaded from database! "
                        + recipe.getRecipeId() + "  "
                        + recipe.getRecipeName());

                recipeList.add(recipe);
                data.moveToNext();
            } while (!data.isAfterLast());
        } else {
            // Cursor was empty - no saved recipes
            appMessageToast("There are no saved recipes available!");
        }

        mRecipeListData = recipeList;
        generateRecipeAdapterWithData(mRecipeListData);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}