package act.angelman.presentation.service;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import act.angelman.AngelmanApplication;
import act.angelman.presentation.manager.ApplicationManager;


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
