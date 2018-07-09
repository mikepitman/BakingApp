package pitman.co.za.bakingapp.domainObjects;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Michael on 2018/01/24.
 * Modified to utilise 'room', a hibernate-type database abstraction tool
 */
@Entity(tableName = "recipe")
public class Recipe implements Parcelable {

    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @Ignore
    private ArrayList<Ingredient> ingredients;
    @Ignore
    private ArrayList<RecipeStep> steps;

    @ColumnInfo(name = "servings")
    private String servings;

    @ColumnInfo(name = "image")
    private String image;

    public Recipe(@NonNull String id, @NonNull String name, String servings, String image) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
    }

    @Ignore
    public Recipe() {}

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<RecipeStep> steps) {
        this.steps = steps;
    }

    public String getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.name);
        parcel.writeString(this.servings);
        parcel.writeString(this.image);
        parcel.writeList(ingredients);
        parcel.writeList(steps);
    }

//    public Recipe() {}

    protected Recipe(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.servings = in.readString();
        this.image = in.readString();
        this.ingredients = in.readArrayList(Ingredient.class.getClassLoader());
        this.steps = in.readArrayList(RecipeStep.class.getClassLoader());
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
