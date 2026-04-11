package angeltalk.plus.presentation.receiver;

import dagger.hilt.android.AndroidEntryPoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import angeltalk.plus.AngelmanApplication;
import angeltalk.plus.presentation.manager.NotificationActionManager;

@AndroidEntryPoint
public class NotificationActionReceiver extends BroadcastReceiver {

    @Inject
    NotificationActionManager notificationActionManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationActionManager.updateNotification(intent);
    }
}
