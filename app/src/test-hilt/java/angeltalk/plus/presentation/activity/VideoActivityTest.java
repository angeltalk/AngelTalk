package angeltalk.plus.presentation.activity;


import android.app.Fragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import angeltalk.plus.UITest;
import angeltalk.plus.presentation.manager.ApplicationConstants;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static org.assertj.core.api.Assertions.assertThat;

@HiltAndroidTest
@RunWith(RobolectricTestRunner.class)
public class VideoActivityTest extends UITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    public VideoActivity subject;

    @Before
    public void setUp() {
        hiltRule.inject();
        subject = Robolectric.setupActivity(VideoActivity.class);
    }

    @Test
    public void testMainActivity() {
        assertThat(subject).isNotNull();
    }

    @Test
    public void whenLaunched_thenMoveToVideoFragment() throws Exception {
        Fragment fragment = subject.getFragmentManager().findFragmentByTag(ApplicationConstants.VIDEO_FRAGMENT_TAG);
        assertThat(fragment).isNotNull();
    }
}