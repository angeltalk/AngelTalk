package act.sds.samsung.angelman.presentation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import act.sds.samsung.angelman.presentation.manager.NotificationActionManager;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            NotificationActionManager notificationActionManager = new NotificationActionManager(context);
            notificationActionManager.initNotificationAfterCompletingBoot(new Intent(context, NotificationActionReceiver.class));
        }
    }
}