package act.sds.samsung.angelman.presentation.service;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.VisibleForTesting;

import act.sds.samsung.angelman.presentation.util.ApplicationManager;


public class ScreenReceiver extends BroadcastReceiver {

    private KeyguardManager km;
    protected KeyguardManager.KeyguardLock keyLock;
    ApplicationManager applicationManager;

    public void onReceive(final Context context, Intent intent) {

        if(applicationManager == null) {
            applicationManager = new ApplicationManager(context);
        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            if (km == null)
                km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyLock == null) keyLock = km.newKeyguardLock(Context.KEYGUARD_SERVICE);

            disableKeyguard();
            applicationManager.makeChildView();
        }
    }

    public void disableKeyguard() {
        keyLock.disableKeyguard();
    }

    @VisibleForTesting
    public ApplicationManager getApplicationManager() {
        return applicationManager;
    }
}
