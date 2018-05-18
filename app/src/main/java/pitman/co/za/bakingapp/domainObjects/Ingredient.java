package pitman.co.za.bakingapp.domainObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

/**
 * Created by Michael on 2018/01/24.
 */
@Entity(tableName = "recipe_ingredient",
        primaryKeys = {"parentRecipe", "ingredient"},
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "recipeName",
                childColumns = "parentRecipe"))
public class Ingredient {

    @NonNull
    private String parentRecipe;
    @NonNull
    private String ingredient;
    private String quantity;
    private String measure;

    public Ingredient(@NonNull String parentRecipe, String quantity, String measure, @NonNull String ingredient) {
        this.parentRecipe = parentRecipe;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    @NonNull
    public String getParentRecipe() {
        return parentRecipe;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    @NonNull
    public String getIngredient() {
        return ingredient;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(this.quantity);
//        parcel.writeString(this.measure);
//        parcel.writeString(this.ingredient);
//    }

//    public Ingredient() {}

//    protected Ingredient(Parcel in) {
//        this.quantity = in.readString();
//        this.measure= in.readString();
//        this.ingredient= in.readString();
//    }

//    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
//        public Ingredient createFromParcel(Parcel source) {
//            return new Ingredient(source);
//        }
//
//        public Ingredient[] newArray(int size) {
//            return new Ingredient[size];
//        }
//    };
}
