package act.sds.samsung.angelman.presentation.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLooper;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.UITest;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OnboardingActivityTest extends UITest{

    private OnboardingActivity subject;
    private ImageView finishButton;

    @Before
    public void setUp() throws Exception {
        subject = setupActivity(OnboardingActivity.class);

        finishButton = (ImageView) subject.findViewById(R.id.onboarding_finish);
    }

    @Test
    public void whenFirstLaunched_thenShowOnBoardingPage() throws Exception {
        assertThat(((RelativeLayout) subject.findViewById(R.id.onboarding_first_page))).isNotNull();
    }

    @Test
    public void whenAfter4SecondsOnOnboardingFirstPage_thenShowNextOnBoaringPage() throws Exception {
        advanceInSchedule();

        RelativeLayout onboardingFirstPage = (RelativeLayout) subject.findViewById(R.id.onboarding_first_page);

        assertThat(onboardingFirstPage).isGone();
    }

    @Test
    public void whenFinishButtonClicked_thenShowCategoryMenu() throws Exception {
        advanceInSchedule();

        finishButton.performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();

        assertThat(nextStartedActivity.getComponent().getClassName()).contains("CategoryMenuActivity");

    }

    @Test
    public void givenOnBoardingHadFinished_whenAppIsLaunchedAgain_thenDirectlyShowCategoryMenu() throws Exception {
        advanceInSchedule();

        finishButton.performClick();

        subject = setupActivity(OnboardingActivity.class);

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();

        assertThat(nextStartedActivity.getComponent().getClassName()).contains("CategoryMenuActivity");
    }

    private void advanceInSchedule() {
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
    }
}