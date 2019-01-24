package angeltalk.plus.presentation.custom;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import angeltalk.plus.presentation.activity.VideoActivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22)
public class AutoFitTextureViewTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private VideoActivity activity;
    private AutoFitTextureView subject;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(VideoActivity.class);
        subject = new AutoFitTextureView(activity);
    }

    @Test
    public void setAspectRatioTest() throws Exception {
        assertThat(shadowOf(subject).didRequestLayout()).isFalse();
        subject.setAspectRatio(1, 1);
        assertThat(shadowOf(subject).didRequestLayout()).isTrue();
    }

    @Test
    public void setAspectRatioExceptionTest() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Size cannot be negative.");
        subject.setAspectRatio(-1, 1);
    }

    @Test
    public void onMeasureTest() throws Exception {
        subject.measure(100, 200);
        assertThat(subject.getMeasuredWidth()).isEqualTo(100);
        assertThat(subject.getMeasuredHeight()).isEqualTo(200);

        subject.setAspectRatio(16, 9);

        subject.measure(1920 + 40, 1080);
        assertThat(subject.getMeasuredWidth()).isEqualTo(1920);
        assertThat(subject.getMeasuredHeight()).isEqualTo(1080);

        subject.measure(1920, 1080 + 40);
        assertThat(subject.getMeasuredWidth()).isEqualTo(1920);
        assertThat(subject.getMeasuredHeight()).isEqualTo(1080);
    }
}