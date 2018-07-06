package pitman.co.za.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class RecipeWidgetRemoteViewsService extends RemoteViewsService {

    private static final String LOG_TAG = RecipeWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
