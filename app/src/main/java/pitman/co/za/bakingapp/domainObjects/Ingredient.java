package pitman.co.za.bakingapp.domainObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Michael on 2018/01/24.
 */
@Entity(tableName = "recipe_ingredient")
public class Ingredient implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private Recipe recipe;

    private String quantity;
    private String measure;
    private String ingredient;

    public Ingredient(@NonNull Recipe recipe) {
        this.recipe = recipe;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public Recipe getRecipe() {
        return recipe;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.quantity);
        parcel.writeString(this.measure);
        parcel.writeString(this.ingredient);
    }

    public Ingredient() {}

    protected Ingredient(Parcel in) {
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
