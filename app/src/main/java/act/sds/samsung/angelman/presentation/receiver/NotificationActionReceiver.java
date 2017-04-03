package act.sds.samsung.angelman.presentation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import act.sds.samsung.angelman.presentation.manager.NotificationActionManager;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationActionManager notificationActionManager = new NotificationActionManager(context);
        notificationActionManager.updateNotification(intent);
    }
}
