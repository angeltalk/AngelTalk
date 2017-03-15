package act.sds.samsung.angelman.presentation.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class ScreenService extends Service{

    private ScreenReceiver mReceiver;
    private static final String TOGGLE_ACTION = "TOGGLE";

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

        if(intent != null){
            if(intent.getAction()==null){
                if(mReceiver==null){
                    mReceiver = new ScreenReceiver();
                    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                    registerReceiver(mReceiver, filter);
                }
            }
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());

        Notification notification = notificationBuilder.build();
        startForeground(1, notification);

//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
//                .setContentTitle("Angel")
//                .setContentText("Widget On/Off")
//                .setSmallIcon(R.drawable.ic_angelman_logo);
//
//        Intent toggleReceive = new Intent();
//        toggleReceive.setAction(TOGGLE_ACTION);
//        final PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, toggleReceive, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.addAction(R.drawable.ic_cancel, "On", pendingIntentYes);
//
//        Notification notification = builder.build();
//
//        startForeground(2000, notification);
//
//        // FIXME : 예제코드, 5초 후 알림바 버튼 변경됨 (childmode on 일때)
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                builder.mActions.clear();
//                builder.addAction(R.drawable.ic_transport_white, "Off", pendingIntentYes);
//                Notification notification = builder.build();
//                startForeground(2000, notification);
//
//            }
//        }, 5000);

        return START_REDELIVER_INTENT;
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
