package act.sds.samsung.angelman.presentation.custom;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;

public class AngelmanWidgetProvider extends AppWidgetProvider {

    private static String ACTION_BUTTON = "android.action.BUTTON_CLICK";
    public static String TARGET_WIDGET_ID = "appWidgetId";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_angelman);

        for(int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(ACTION_BUTTON);
            intent.putExtra(TARGET_WIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            widgetView.setOnClickPendingIntent(R.id.angelman_button, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, widgetView);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(ACTION_BUTTON)){
            AngelmanApplication applicationContext = (AngelmanApplication) context.getApplicationContext();
            if(applicationContext.isChildMode()){
                applicationContext.setNotChildMode();
            }else{
                applicationContext.setChildMode();
            }
        }
        super.onReceive(context, intent);
    }
}
