package pitman.co.za.bakingapp.domainObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Michael on 2018/01/24.
 */
@Entity(tableName = "recipe_ingredient",
        primaryKeys = {"parentRecipe", "ingredient"},
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "name",
                childColumns = "parentRecipe"))
public class Ingredient implements Parcelable {

    @NonNull
    private String parentRecipe;
    @NonNull
    private String ingredient;
    private String quantity;
    private String measure;

    public Ingredient(String quantity, String measure, @NonNull String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    @Ignore
    public Ingredient() {}

    public void setParentRecipe(@NonNull String parentRecipe) {
        this.parentRecipe = parentRecipe;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.parentRecipe);
        parcel.writeString(this.quantity);
        parcel.writeString(this.measure);
        parcel.writeString(this.ingredient);
    }

    public Ingredient(Parcel in) {
        this.parentRecipe = in.readString();
        this.quantity = in.readString();
        this.measure= in.readString();
        this.ingredient= in.readString();
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
