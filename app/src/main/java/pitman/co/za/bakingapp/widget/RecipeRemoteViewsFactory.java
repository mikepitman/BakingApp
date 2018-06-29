package pitman.co.za.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import pitman.co.za.bakingapp.R;
import pitman.co.za.bakingapp.data.RecipeDao;
import pitman.co.za.bakingapp.data.RecipeRoomDatabase;
import pitman.co.za.bakingapp.domainObjects.Ingredient;

/* Sourced in part(s) from
 * https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview/
 * https://stackoverflow.com/questions/21379949/how-to-load-items-in-android-homescreen-listview-widget
 * https://www.sitepoint.com/killer-way-to-show-a-list-of-items-in-android-collection-widget/
 */

public class RecipeRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = RecipeRemoteViewsFactory.class.getSimpleName();
    private List<Ingredient> mList;
    private Context context;
    private int appWidgetId;

    public RecipeRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
    }

    /* Get the current selected recipe name from SharedPreferences, and get the ingredients for that recipe from the room database.
     * onDataSetChanged() seems the only way to get new data into the factory to display in the widget */
    @Override
    public void onDataSetChanged() {
        RecipeRoomDatabase db = RecipeRoomDatabase.getDatabase(context);
        RecipeDao dao = db.recipeDao();

        SharedPreferences sharedPreferences = context.getSharedPreferences("pitman.co.za.bakingapp", Context.MODE_PRIVATE);

        String selectedRecipeName = sharedPreferences.getString("selectedRecipe", "");
        mList = dao.getRecipeIngredients(selectedRecipeName);
    }

    @Override
    public void onDestroy() {
        mList.clear();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(),
                R.layout.recipe_widget_list_item);
        Ingredient ingredient = mList.get(position);
        remoteView.setTextViewText(R.id.ingredient_name, ingredient.getIngredient());
        remoteView.setTextViewText(R.id.ingredient_quantity, ingredient.getQuantity());
        remoteView.setTextViewText(R.id.ingredient_measure, ingredient.getMeasure());

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
