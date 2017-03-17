package act.sds.samsung.angelman.presentation.service;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.UITest;

import static android.content.Context.KEYGUARD_SERVICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ScreenReceiverTest extends UITest{

    private Context context;
    private KeyguardManager manager;
    private ScreenReceiver subject;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application;
        manager = (KeyguardManager) RuntimeEnvironment.application.getSystemService(KEYGUARD_SERVICE);
        subject = new ScreenReceiver();
    }


    @Test
    public void whenActionScreenOffThenDisableKeyguard() throws Exception {
        KeyguardManager.KeyguardLock lock = manager.newKeyguardLock(KEYGUARD_SERVICE);

        assertThat(shadowOf(lock).isEnabled()).isTrue();

        receiveTheActionScreenOff();

        assertThat(shadowOf(lock).isEnabled()).isFalse();
    }

    @Test
    public void whenActionScreenOffThenMakeChildView() throws Exception {
        receiveTheActionScreenOff();
        assertThat(subject.getApplicationManager().getChildModeManager().getCategoryMenuLayout()).isNotNull();
    }

    private void receiveTheActionScreenOff() {
        Intent it = new Intent();
        it.setAction(Intent.ACTION_SCREEN_OFF);
        subject.onReceive(context, it);
    }
}