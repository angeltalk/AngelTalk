package act.angelman.presentation.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import act.angelman.R;
import act.angelman.presentation.manager.ApplicationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22)
public class NotificationActionReceiverTest {
    private NotificationActionReceiver subject;
    private NotificationManager notificationManager;
    private Notification notification;
    private ApplicationManager applicationManager;

    @Before
    public void setUp() throws Exception {
        subject = new NotificationActionReceiver();
        notificationManager = (NotificationManager) RuntimeEnvironment.application.getSystemService(Context.NOTIFICATION_SERVICE);

        notification = new Notification(R.drawable.angelee, null, System.currentTimeMillis());

        notificationManager.notify(1, notification);
        applicationManager = mock(ApplicationManager.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Test
    public void givenChildModeAndLaunched_whenOnReceive_thenChangeNotificationViewOn() throws Exception {
        when(applicationManager.isChildMode()).thenReturn(false);
        assertThat(applicationManager.isChildMode()).isFalse();
    }
}