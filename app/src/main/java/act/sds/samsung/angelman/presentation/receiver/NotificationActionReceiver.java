package act.sds.samsung.angelman.presentation.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.util.ApplicationManager;

import static act.sds.samsung.angelman.presentation.util.ApplicationManager.PRIVATE_PREFERENCE_NAME;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences(PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean isChildMode = preferences.getBoolean("childMode", false);

        ApplicationManager applicationManager = new ApplicationManager(context);
        NotificationManager notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));

        applicationManager.changeChildMode(!isChildMode);

        RemoteViews notificationView = new RemoteViews(context.getPackageName(), isChildMode ? R.layout.layout_notification_off : R.layout.layout_notification_on);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        notificationView.setOnClickPendingIntent(isChildMode ? R.id.btn_on : R.id.btn_off, pendingIntent);

        Notification notification = new Notification(R.drawable.angelee, null, System.currentTimeMillis());
        notification.contentView = notificationView;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(1, notification);
    }
}
