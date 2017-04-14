package act.angelman.presentation.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivityManager;
import org.robolectric.shadows.ShadowApplication;

import java.util.Arrays;
import java.util.List;

import act.angelman.BuildConfig;
import act.angelman.UITest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ScreenServiceTest extends UITest {

    private ActivityManager activityManager;

    @Before
    public void setUp() throws Exception {
         Intent screenService = new Intent(RuntimeEnvironment.application, ScreenService.class);
         RuntimeEnvironment.application.startService(screenService);

         activityManager = (ActivityManager) RuntimeEnvironment.application.getSystemService(Context.ACTIVITY_SERVICE);

    }

    @Test
    public void  whenServiceCreated_thenAddRegisteredReceiver() throws Exception {

        List<ShadowApplication.Wrapper> registeredReceivers = ShadowApplication.getInstance().getRegisteredReceivers();
        boolean isFindRegisteredScreenReceiver = false;
        for(ShadowApplication.Wrapper wrapper: registeredReceivers){
            if(wrapper.getBroadcastReceiver().getClass().getCanonicalName().contains("ScreenReceiver")){
                isFindRegisteredScreenReceiver = true;
                break;
            }
        }
        assertThat(isFindRegisteredScreenReceiver).isTrue();
    }

    @Test
    public void whenStartService_ThenThatServiceRunForeground() throws Exception {
        createRunningAppProcessInfo(0);
        assertThat(isContextForeground()).isTrue();
    }

    private boolean isContextForeground(){
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }

    private void createRunningAppProcessInfo(int pid) {
        ShadowActivityManager shadowAm = shadowOf(activityManager);

        ActivityManager.RunningAppProcessInfo proc = new ActivityManager.RunningAppProcessInfo();
        proc.importance = ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        proc.pid = pid;
        shadowAm.setProcesses(Arrays.asList(proc));
    }
}