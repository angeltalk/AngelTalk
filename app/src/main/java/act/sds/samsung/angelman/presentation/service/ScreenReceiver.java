package act.sds.samsung.angelman.presentation.service;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import act.sds.samsung.angelman.AngelmanApplication;


public class ScreenReceiver extends BroadcastReceiver {

    private KeyguardManager km;
    protected KeyguardManager.KeyguardLock keyLock;

    public void onReceive(final Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            if (km == null)
                km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyLock == null) keyLock = km.newKeyguardLock(Context.KEYGUARD_SERVICE);

            disableKeyguard();

            ((AngelmanApplication) context.getApplicationContext()).makeChildView();
        }
    }
    public void disableKeyguard() {
        keyLock.disableKeyguard();
    }
}
