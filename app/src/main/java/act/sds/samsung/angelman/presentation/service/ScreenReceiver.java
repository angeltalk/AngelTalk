package act.sds.samsung.angelman.presentation.service;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.presentation.util.ApplicationManager;


public class ScreenReceiver extends BroadcastReceiver {

    @Inject
    ApplicationManager applicationManager;

    private KeyguardManager km;
    protected KeyguardManager.KeyguardLock keyLock;

    public void onReceive(final Context context, Intent intent) {
        ((AngelmanApplication) context.getApplicationContext()).getAngelmanComponent().inject(this);

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

}
