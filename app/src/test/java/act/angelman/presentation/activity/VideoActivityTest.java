package act.angelman.presentation.activity;


import android.app.Fragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import act.angelman.UITest;
import act.angelman.presentation.manager.ApplicationConstants;

import static junit.framework.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22)
public class VideoActivityTest extends UITest{

    public VideoActivity subject;

    @Before
    public void setUp() {
        subject = Robolectric.setupActivity(VideoActivity.class);
    }

    @Test
    public void testMainActivity() {
        assertNotNull(subject);
    }

    @Test
    public void whenLaunched_thenMoveToVideoFragment() throws Exception {
        Fragment fragment = subject.getFragmentManager().findFragmentByTag(ApplicationConstants.VIDEO_FRAGMENT_TAG);
        assertThat(fragment).isNotNull();
    }
}