package act.angelman.presentation.activity;

import android.content.Intent;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import act.angelman.BuildConfig;
import act.angelman.R;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Robolectric.setupActivity;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class Camera2ActivityTest {

    private Camera2Activity subject;

    @Before
    public void setUp() throws Exception {
        subject = setupActivity(Camera2Activity.class);
    }

    @Test
    @Ignore("Camera2 Activity 테스트는 UI 테스트로 추후 Cover")
    public void whenClickCameraShutterButton_thenMoveToMakeCardPreviewActivity() throws Exception {
        subject.findViewById(R.id.camera_shutter).performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent intent = shadowActivity.getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).isEqualTo(MakeCardPreviewActivity.class.getCanonicalName());
    }
}