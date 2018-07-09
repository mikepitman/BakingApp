package pitman.co.za.bakingapp.jsonDeserialisation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import pitman.co.za.bakingapp.domainObjects.Ingredient;

public class IngredientDeserialiser implements JsonDeserializer {

    final String JSON_INGREDIENT_QUANTITY = "quantity";
    final String JSON_INGREDIENT_MEASURE = "measure";
    final String JSON_INGREDIENT_INGREDIENT = "ingredient";

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject ingredient_obj = json.getAsJsonObject();

        return new Ingredient(
                ingredient_obj.get(JSON_INGREDIENT_QUANTITY).getAsString(),
                ingredient_obj.get(JSON_INGREDIENT_MEASURE).getAsString(),
                ingredient_obj.get(JSON_INGREDIENT_INGREDIENT).getAsString());
    }
}
