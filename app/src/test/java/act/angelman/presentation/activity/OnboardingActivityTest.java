package act.angelman.presentation.activity;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import act.angelman.BuildConfig;
import act.angelman.TestAngelmanApplication;
import act.angelman.UITest;
import act.angelman.presentation.manager.ApplicationManager;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OnboardingActivityTest extends UITest{

    @Inject
    ApplicationManager applicationManager;

    private OnboardingActivity subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(applicationManager.isFirstLaunched()).thenReturn(true);
        subject = setupActivity(OnboardingActivity.class);
    }

    @Test
    public void whenFirstLaunched_thenShowOnBoardingPage() throws Exception {

        assertThat(subject.onboardingFirstPageLayout).isShown();
    }

    @Test
    public void givenFirstLaunched_whenAfter4SecondsOnOnboardingFirstPage_thenShowNextOnBoaringPage() throws Exception {
        advance4Seconds();

        assertThat(subject.onboardingFirstPageLayout).isGone();
    }

    @Test
    public void givenFirstLaunched_whenFinishButtonClicked_thenMoveToCategoryMenuActivity() throws Exception {
        advance4Seconds();

        subject.onboardingFinishButton.performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();

        assertThat(nextStartedActivity.getComponent().getClassName()).contains(CategoryMenuActivity.class.getCanonicalName());
    }

    @Test
    public void givenWhenNotFirstLaunched_thenDirectlyMoveToCategoryMenuActivity() throws Exception {
        advance4Seconds();

        subject.onboardingFinishButton.performClick();

        OnboardingActivity newSubject = setupActivity(OnboardingActivity.class);

        ShadowActivity shadowActivity = shadowOf(newSubject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();

        assertThat(nextStartedActivity.getComponent().getClassName()).contains(CategoryMenuActivity.class.getCanonicalName());
    }

    private void advance4Seconds() {
        Robolectric.getForegroundThreadScheduler().advanceBy(4000, TimeUnit.MILLISECONDS);
    }
}