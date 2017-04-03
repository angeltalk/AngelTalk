package act.sds.samsung.angelman.presentation.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;

import static act.sds.samsung.angelman.presentation.manager.ApplicationConstants.PRIVATE_PREFERENCE_NAME;

public class NotificationActionManager {

    @Inject
    ApplicationManager applicationManager;

    private Context context;
    private boolean isChildMode;

    public NotificationActionManager(Context context) {
        ((AngelmanApplication) context.getApplicationContext()).getAngelmanComponent().inject(this);
        this.context = context;
        isChildMode = getChildMode();
    }

    public void generateNotification(Intent intent) {
        RemoteViews remoteViews = this.setNotificationView();
        this.setOnClickListener(remoteViews, intent);
        this.notify(remoteViews);
    }

    public void updateNotification(Intent intent) {
        this.changeChildMode();
        generateNotification(intent);
    }

    public void initNotificationAfterCompletingBoot(Intent intent) {
        applicationManager.changeChildMode(false);
        isChildMode = false;
        RemoteViews remoteViews = this.setNotificationView();
        this.setOnClickListener(remoteViews, intent);
        this.notify(remoteViews);
    }

    private void changeChildMode() {
        applicationManager.changeChildMode(!isChildMode);
        isChildMode = !isChildMode;
    }

    private RemoteViews setNotificationView() {
        return new RemoteViews(context.getPackageName(), isChildMode ? R.layout.layout_notification_on : R.layout.layout_notification_off);
    }

    private void setOnClickListener(RemoteViews notificationView, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        notificationView.setOnClickPendingIntent(isChildMode ? R.id.btn_off : R.id.btn_on, pendingIntent);
    }

    private void notify(RemoteViews notificationView) {
        NotificationManager notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        Notification notification = new Notification(R.drawable.angelee, null, System.currentTimeMillis());
        notification.contentView = notificationView;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(1, notification);
    }

    private boolean getChildMode() {
        SharedPreferences preferences = context.getSharedPreferences(PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean("childMode", false);
    }
}
