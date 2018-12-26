package act.angelman.presentation.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import act.angelman.AngelmanApplication;
import act.angelman.presentation.manager.NotificationActionManager;
import act.angelman.presentation.receiver.NotificationActionReceiver;

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

        mReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        ((AngelmanApplication) getApplicationContext().getApplicationContext()).getAngelmanComponent().inject(this);
        if(intent != null){
            if(intent.getAction()==null){
                if(mReceiver==null){
                    mReceiver = new ScreenReceiver();
                    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                    registerReceiver(mReceiver, filter);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForeground(NOTIFICATION_ID, notificationActionManager.notify(notificationActionManager.getNotificationView(), new Intent(this, NotificationActionReceiver.class)));
                }

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
