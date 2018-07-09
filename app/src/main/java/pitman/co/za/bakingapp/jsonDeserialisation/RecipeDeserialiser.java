package pitman.co.za.bakingapp.jsonDeserialisation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import pitman.co.za.bakingapp.domainObjects.Ingredient;
import pitman.co.za.bakingapp.domainObjects.Recipe;
import pitman.co.za.bakingapp.domainObjects.RecipeStep;

public class RecipeDeserialiser implements JsonDeserializer {

    final String JSON_RECIPE_ID = "id";
    final String JSON_RECIPE_NAME = "name";
    final String JSON_RECIPE_SERVINGS = "servings";
    final String JSON_RECIPE_IMAGE = "image";

    final String JSON_RECIPE_STEPS_ARRAY = "steps";
    final String JSON_RECIPE_INGREDIENTS_ARRAY = "ingredients";

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject recipe_obj = json.getAsJsonObject();

        Recipe recipe = new Recipe(
                recipe_obj.get(JSON_RECIPE_ID).getAsString(),
                recipe_obj.get(JSON_RECIPE_NAME).getAsString(),
                recipe_obj.get(JSON_RECIPE_SERVINGS).getAsString(),
                recipe_obj.get(JSON_RECIPE_IMAGE).getAsString());

        RecipeStep[] steps = context.deserialize(recipe_obj.get(JSON_RECIPE_STEPS_ARRAY), RecipeStep[].class);
        Ingredient[] ingredients = context.deserialize(recipe_obj.get(JSON_RECIPE_INGREDIENTS_ARRAY), Ingredient[].class);

        recipe.setSteps(new ArrayList<>(Arrays.asList(steps)));
        recipe.setIngredients(new ArrayList<>(Arrays.asList(ingredients)));

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.setParentRecipe(recipe.getName());
        }
        for (RecipeStep step : recipe.getSteps()) {
            step.setParentRecipe(recipe.getName());
        }

        return recipe;
    }
}
