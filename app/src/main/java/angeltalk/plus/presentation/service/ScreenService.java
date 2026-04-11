package angeltalk.plus.presentation.service;

import dagger.hilt.android.AndroidEntryPoint;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import javax.inject.Inject;

import angeltalk.plus.AngelmanApplication;
import angeltalk.plus.presentation.manager.NotificationActionManager;
import angeltalk.plus.presentation.receiver.NotificationActionReceiver;

@AndroidEntryPoint
public class ScreenService extends Service{

    @Inject
    NotificationActionManager notificationActionManager;

    private static int NOTIFICATION_ID = 1;
    private ScreenReceiver mReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerScreenOffReceiver();
    }

    private void registerScreenOffReceiver() {
        if (mReceiver != null) return;
        mReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        // ACTION_SCREEN_OFF is sent by the system process (different UID), so on
        // targetSdk 33+ the receiver must be RECEIVER_EXPORTED — RECEIVER_NOT_EXPORTED
        // silently drops system broadcasts and onReceive() never fires.
        ContextCompat.registerReceiver(this, mReceiver, filter, ContextCompat.RECEIVER_EXPORTED);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        registerScreenOffReceiver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = notificationActionManager.notify(
                    notificationActionManager.getNotificationView(),
                    new Intent(this, NotificationActionReceiver.class));
            // Android 14 (API 34+) requires an explicit foreground service type.
            // Child-mode lock-screen overlay doesn't fit any of the predefined types,
            // so "specialUse" is the intended fallback per Google's guidance.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(NOTIFICATION_ID, notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else {
                startForeground(NOTIFICATION_ID, notification);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
}
