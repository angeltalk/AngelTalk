package act.sds.samsung.angelman.presentation.custom;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;

public class AngelmanWidgetProvider extends AppWidgetProvider {

    private static String ACTION_BTN = "android.action.BUTTON_CLICK";
    public static String TARGET_WIDGET_ID = "appWidgetId";

    private Context context;
    private ApplicationManager applicationManager;

    @Override
    public void onEnabled(Context context) {
        this.context = context;
        super.onEnabled(context);
        applicationManager  = new ApplicationManager(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_angelman);

        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(ACTION_BTN);
            intent.putExtra(TARGET_WIDGET_ID, appWidgetId);
            PendingIntent btnClick = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            widgetView.setOnClickPendingIntent(R.id.angelman_button, btnClick);
            if(applicationManager == null) {
                applicationManager = new ApplicationManager(context);
            }
            if (applicationManager.isChildMode()) {
                applicationManager.setChildMode();
            } else {
                applicationManager.setNotChildMode();
            }
            appWidgetManager.updateAppWidget(appWidgetId, widgetView);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_BTN)) {
            if(applicationManager == null) {
                applicationManager = new ApplicationManager(context);
            }
            if (applicationManager.isChildMode()) {
                applicationManager.setNotChildMode();
            } else {
                applicationManager.setChildMode();
            }
        }
        super.onReceive(context, intent);
    }
}
