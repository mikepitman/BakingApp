package pitman.co.za.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pitman.co.za.bakingapp.domainObjects.Ingredient;
import pitman.co.za.bakingapp.domainObjects.Recipe;

/**
 * Created by Michael on 2018/02/13.
 */

public class RecipeIngredientsActivityFragment extends Fragment {

    private static String LOG_TAG = RecipeIngredientsActivityFragment.class.getSimpleName();
    private RecipeIngredientsActivityFragment.IngredientCardAdapter mAdapter;
    private RecyclerView mRecipeRecyclerView;
    private Recipe selectedRecipe;
    private List<Ingredient> ingredients;
    private View rootView;

    public RecipeIngredientsActivityFragment() {
        Log.d(LOG_TAG, "RecipeIngredientsActivityFragment() constructor called");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selectedRecipe", selectedRecipe);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ingredients_list, container, false);
        mRecipeRecyclerView = (RecyclerView) rootView.findViewById(R.id.ingredients_list);
        mRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            selectedRecipe = savedInstanceState.getParcelable("selectedRecipe");

        // Fragment instantiated from RecipeSkeletonActivity, in master-detail layout
        } else if (this.getArguments() != null) {
            Bundle arguments = this.getArguments();
            selectedRecipe = arguments.getParcelable("selectedRecipe");

        // Fragment instantiated by RecipeIngredientsActivity, as stand-along fragment
        } else {
            Intent intent = getActivity().getIntent();
            if ((intent != null)) {
                selectedRecipe = intent.getParcelableExtra("recipe");
                ingredients = selectedRecipe.getIngredients();
                Log.d(LOG_TAG, "number of ingredients: " + ingredients.size());
            }
        }
        mAdapter = new RecipeIngredientsActivityFragment.IngredientCardAdapter(selectedRecipe.getIngredients());
        mRecipeRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private class IngredientCardAdapter extends RecyclerView.Adapter<RecipeIngredientsActivityFragment.ViewHolder> {

        private String LOG_TAG = RecipeIngredientsActivityFragment.IngredientCardAdapter.class.getSimpleName();
        private List<Ingredient> mIngredientsListing;

        private IngredientCardAdapter(List<Ingredient> ingredientsListing) {
            mIngredientsListing = ingredientsListing;
        }

        @Override
        public int getItemCount() {
            return mIngredientsListing.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card_ingredient, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecipeIngredientsActivityFragment.ViewHolder holder, int position) {
            holder.bindIngredient(mIngredientsListing.get(position));
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView ingredientNameTextView;
        private TextView ingredientMeasureTextView;
        private TextView ingredientQuantityTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ingredientNameTextView = (TextView) itemView.findViewById(R.id.ingredient_description);
            ingredientMeasureTextView = (TextView) itemView.findViewById(R.id.ingredient_measure);
            ingredientQuantityTextView = (TextView) itemView.findViewById(R.id.ingredient_quantity);
        }

        public void bindIngredient(Ingredient ingredient) {
            this.ingredientNameTextView.setText(ingredient.getIngredient());
            this.ingredientMeasureTextView.setText(ingredient.getMeasure());
            String quantityString = ingredient.getQuantity() + " ";
            this.ingredientQuantityTextView.setText(quantityString);
        }
    }
}
