package angeltalk.plus.presentation.manager;

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
import javax.inject.Singleton;

import angeltalk.plus.R;
import dagger.hilt.android.qualifiers.ApplicationContext;

import static angeltalk.plus.presentation.manager.ApplicationConstants.PRIVATE_PREFERENCE_NAME;

@Singleton
public class NotificationActionManager {

    private final RemoteViews notificationViewOfChildMode;
    private final RemoteViews notificationViewOfParentMode;
    private final NotificationManager notificationManager;
    private final ApplicationManager applicationManager;

    private Context context;
    private boolean isChildMode;
    private static int NOTIFICATION_ID = 1;
    private static String CHANNEL_ID = "ANGELTALK";
    private static String CHANNEL_NAME = "Angel Talk";

    @Inject
    public NotificationActionManager(@ApplicationContext Context context, ApplicationManager applicationManager) {
        this.context = context;
        this.applicationManager = applicationManager;
        notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
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
        int pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }
        return builder.setSmallIcon(R.drawable.angelee)
                .setOngoing(true)
                .setContentTitle("Angel talk")
                .setContentText("Angel talk")
                .setContentIntent(PendingIntent.getBroadcast(context, 0, intent, pendingFlags));
    }

    private void changeChildMode() {
        applicationManager.changeChildMode(!isChildMode);
        isChildMode = !isChildMode;
    }

    public void setOnClickListener(RemoteViews notificationView, Intent intent) {
        int pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, pendingFlags);
        notificationView.setOnClickPendingIntent(isChildMode ? R.id.btn_off : R.id.btn_on, pendingIntent);
    }

    public Notification notify(RemoteViews notificationView, Intent intent) {
        Notification.Builder notificationBuilder = createNotificationBuilder(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setCustomContentView(notificationView);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.setVibrationPattern(new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
            channel.setSound(null, null);
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
