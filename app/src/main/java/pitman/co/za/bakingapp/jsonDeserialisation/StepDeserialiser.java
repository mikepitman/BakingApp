package pitman.co.za.bakingapp.jsonDeserialisation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import pitman.co.za.bakingapp.domainObjects.RecipeStep;

public class StepDeserialiser implements JsonDeserializer {

    final String JSON_STEP_ID = "id";
    final String JSON_STEP_SHORT_DESC = "shortDescription";
    final String JSON_STEP_DESCRIPTION = "description";
    final String JSON_STEP_VIDEO_URL = "videoURL";
    final String JSON_STEP_THUMBNAIL_URL = "thumbnailURL";

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject steps_obj = json.getAsJsonObject();

        return new RecipeStep(
                steps_obj.get(JSON_STEP_SHORT_DESC).getAsString(),
                steps_obj.get(JSON_STEP_ID).getAsInt(),
                steps_obj.get(JSON_STEP_DESCRIPTION).getAsString(),
                steps_obj.get(JSON_STEP_VIDEO_URL).getAsString(),
                steps_obj.get(JSON_STEP_THUMBNAIL_URL).getAsString());
    }
}
