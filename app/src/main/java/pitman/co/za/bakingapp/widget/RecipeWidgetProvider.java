package pitman.co.za.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

import pitman.co.za.bakingapp.MainActivity;
import pitman.co.za.bakingapp.R;
import pitman.co.za.bakingapp.domainObjects.Ingredient;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = RecipeWidgetProvider.class.getSimpleName();
    public static ArrayList<Ingredient> widgetIngredients = new ArrayList<>();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);

        Intent intent = new Intent(context, RecipeWidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));  // ? what's this for?

        views.setRemoteAdapter(R.id.recipeWidgetListView, intent);
        views.setEmptyView(R.layout.recipe_widget_provider, R.id.empty_view);

        // Open the app when user clicks on the widget
        Intent widgetClickedIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, widgetClickedIntent, 0);
        views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

//        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        // https://stackoverflow.com/questions/42129390/onupdate-not-calling-the-widget-service
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.recipeWidgetListView);
    }

    /* Called when widget manager send broadcast intent with ACTION_APPWIDGET_UPDATE action */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {      // There may be multiple widgets active, so update all of them
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(LOG_TAG, "intent received in RecipeWidgetProvider for updating");

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final ComponentName thisAppWidget = new ComponentName(context.getPackageName(), RecipeWidgetProvider.class.getName());
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }
}

