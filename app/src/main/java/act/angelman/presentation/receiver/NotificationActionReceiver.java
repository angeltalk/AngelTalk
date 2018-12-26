package act.angelman.presentation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import act.angelman.AngelmanApplication;
import act.angelman.presentation.manager.NotificationActionManager;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Inject
    NotificationActionManager notificationActionManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((AngelmanApplication) context.getApplicationContext()).getAngelmanComponent().inject(this);
        notificationActionManager.updateNotification(intent);
    }
}
