package pitman.co.za.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import pitman.co.za.bakingapp.R;

/* Sourced in part(s) from
 * https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview/
 * https://stackoverflow.com/questions/21379949/how-to-load-items-in-android-homescreen-listview-widget
 * https://www.sitepoint.com/killer-way-to-show-a-list-of-items-in-android-collection-widget/
 */

public class RecipeRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = RecipeRemoteViewsFactory.class.getSimpleName();
    private ArrayList<String> ingredientsList;
    private Context context;
    private int appWidgetId;

    public RecipeRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
//        this.ingredientsList = intent.getParcelableArrayListExtra("ingredients");
        this.ingredientsList = intent.getStringArrayListExtra("ingredients");

        Log.d(LOG_TAG, "constructor called, number of ingredients: " + ingredientsList.size());
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
        ingredientsList.clear();
    }

    @Override
    public int getCount() {
        return ingredientsList == null ? 0 : ingredientsList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(),
                R.layout.recipe_widget_list_item);
//        Ingredient ingredient = ingredientsList.get(position);
        String ingredient = ingredientsList.get(position);
        remoteView.setTextViewText(R.id.ingredient_name, ingredient);
        Log.d(LOG_TAG, "new remote view created with text " + ingredient);
//        remoteView.setTextViewText(R.id.ingredient_quantity, ingredient.getQuantity());
//        remoteView.setTextViewText(R.id.ingredient_measure, ingredient.getMeasure());

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
