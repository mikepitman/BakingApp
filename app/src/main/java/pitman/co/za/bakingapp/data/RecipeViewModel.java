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

    public RecipeViewModel(Application application) {
        super(application);
        mRepository = new RecipeRepository(application);
        mAllRecipes = mRepository.getAllRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
//        return mAllRecipes;
        return mRepository.getAllRecipes();
    }

    public void getRecipeAttributes(Recipe parentRecipe) {
        mRepository.getRecipeAttributes(parentRecipe);
    }

//    public List<RecipeStep> getRecipeSteps(String parentRecipe) {
//        return mRepository.getRecipeSteps(parentRecipe);
//    }

    public void insert(ArrayList<Recipe> recipes) {
        mRepository.insert(recipes);
    }
}
