package pitman.co.za.bakingapp.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import pitman.co.za.bakingapp.domainObjects.Recipe;

public class RecipeViewModel extends AndroidViewModel {

    private RecipeRepository mRepository;
    private LiveData<List<Recipe>> mAllRecipes;
    private Recipe selectedRecipe;

    // Constructor never called in examples I could find, hence second setting of the mAllRecipes attribute in getAllRecipes() method
    public RecipeViewModel(Application application) {
        super(application);
        mRepository = new RecipeRepository(application);
        mAllRecipes = mRepository.getAllRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        mAllRecipes = mRepository.getAllRecipes();
        return mAllRecipes;
    }

    public void loadRecipeAttributes(Recipe parentRecipe) {
        mRepository.getRecipeAttributes(parentRecipe);
    }

    public void insert(ArrayList<Recipe> recipes) {
        mRepository.insert(recipes);
    }

    public void setSelectedRecipe(Recipe recipe) {
        this.selectedRecipe = recipe;
    }

    public Recipe retrieveSelectedRecipe() {
        return selectedRecipe;
    }
}
