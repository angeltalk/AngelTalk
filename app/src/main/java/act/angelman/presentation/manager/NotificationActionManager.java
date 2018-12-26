package act.angelman.presentation.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.RemoteViews;

import javax.inject.Inject;

import act.angelman.AngelmanApplication;
import act.angelman.R;

import static act.angelman.presentation.manager.ApplicationConstants.PRIVATE_PREFERENCE_NAME;

public class NotificationActionManager {

    private final RemoteViews notificationViewOfChildMode;
    private final RemoteViews notificationViewOfParentMode;
    @Inject
    ApplicationManager applicationManager;

    private Context context;
    private boolean isChildMode;
    private static int NOTIFICATION_ID = 1;
    private static String CHANNEL_ID = "ANGELTALK";
    private static String CHANNEL_NAME = "Angel Talk";

    public NotificationActionManager(Context context) {
        ((AngelmanApplication) context.getApplicationContext()).getAngelmanComponent().inject(this);
        this.context = context;
        isChildMode = getChildMode();
        notificationViewOfChildMode = new RemoteViews(context.getPackageName(), R.layout.layout_notification_on);
        notificationViewOfParentMode = new RemoteViews(context.getPackageName(), R.layout.layout_notification_off);
    }

    public Notification generateNotification(Intent intent) {
        RemoteViews remoteViews = this.getNotificationView();
        this.setOnClickListener(remoteViews, intent);
        return this.notify(remoteViews, intent);
    }

    public void updateNotification(Intent intent) {
        this.changeChildMode();
        generateNotification(intent);
    }

    public void initNotificationAfterCompletingBoot(Intent intent) {
        applicationManager.changeChildMode(false);
        isChildMode = false;
        generateNotification(intent);
    }

    public RemoteViews getNotificationView() {
        return isChildMode ? notificationViewOfChildMode : notificationViewOfParentMode;
    }

    public Notification.Builder createNotificationBuilder(Intent intent) {
        Notification.Builder builder = new Notification.Builder(context);
        return builder.setSmallIcon(R.drawable.angelee)
                .setOngoing(true)
                .setContentTitle("Angel talk")
                .setContentText("Angel talk")
                .setContentIntent(PendingIntent.getBroadcast(context, 0, intent, 0));
    }

    private void changeChildMode() {
        applicationManager.changeChildMode(!isChildMode);
        isChildMode = !isChildMode;
    }

    public void setOnClickListener(RemoteViews notificationView, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        notificationView.setOnClickPendingIntent(isChildMode ? R.id.btn_off : R.id.btn_on, pendingIntent);
    }

    public Notification notify(RemoteViews notificationView, Intent intent) {
        Notification.Builder notificationBuilder = createNotificationBuilder(intent);
        NotificationManager notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setCustomContentView(notificationView);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationBuilder.setChannelId(CHANNEL_ID);
            notificationManager.createNotificationChannel(channel);
        } else {
            notificationBuilder.setContent(notificationView);
        }
        Notification notification = notificationBuilder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);

        return notification;
    }

    private boolean getChildMode() {
        SharedPreferences preferences = context.getSharedPreferences(PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean("childMode", false);
    }
}
