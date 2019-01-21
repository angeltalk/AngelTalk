package act.angelman.presentation.activity;

import android.content.Intent;
import android.content.pm.PackageManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import javax.inject.Inject;

import act.angelman.TestAngelmanApplication;
import act.angelman.UITest;
import act.angelman.presentation.manager.ApplicationManager;

import static act.angelman.presentation.manager.ApplicationConstants.ONBOARDING_PERMISSION_REQUEST_CODE;
import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22)
public class OnboardingActivityTest extends UITest{

    @Inject
    ApplicationManager applicationManager;

    private OnboardingActivity subject;

    @Test
    public void whenFirstLaunched_thenShowOnBoardingPage() throws Exception {
        setUpWhenFirstLaunched();
        assertThat(subject.onboardingFirstPageLayout).isShown();
    }

    @Test
    public void givenFirstLaunched_whenAfter4SecondsOnOnboardingFirstPage_thenShowNextOnBoaringPage() throws Exception {
        setUpWhenFirstLaunched();
        advance4Seconds();

        assertThat(subject.onboardingFirstPageLayout).isGone();
    }

    @Test
    public void givenWhenNotFirstLaunchedWithoutStoragePermission_thenShowLastPage() throws Exception {
        // given when
        setUpWhenNotFirstLaunched();

        //then
        assertThat(subject.onboardingViewPager.getCurrentItem()).isEqualTo(4);
    }

    @Test
    public void givenNotFirstLaunchedWithoutPermission_whenGrantPermission_thenMoveToCategoryMenuActivity() throws Exception {
        //given
        setUpWhenNotFirstLaunched();

        //when
        int[] grantResults = {PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED};
        subject.onRequestPermissionsResult(ONBOARDING_PERMISSION_REQUEST_CODE, null, grantResults);

        //then
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).contains(CategoryMenuActivity.class.getCanonicalName());
    }

    @Test
    public void givenNotFirstLaunchedWithoutPermission_whenDenyPermission_thenDoNotMoveToCategoryMenuActivity() throws Exception {
        //given
        setUpWhenNotFirstLaunched();

        //when
        int[] grantResults = {PackageManager.PERMISSION_DENIED, PackageManager.PERMISSION_DENIED, PackageManager.PERMISSION_DENIED};
        subject.onRequestPermissionsResult(ONBOARDING_PERMISSION_REQUEST_CODE, null, grantResults);

        //then
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity).isNull();
    }

    private void setUpWhenFirstLaunched() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(applicationManager.isFirstLaunched()).thenReturn(true);
        subject = setupActivity(OnboardingActivity.class);
    }

    private void setUpWhenNotFirstLaunched() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(applicationManager.isFirstLaunched()).thenReturn(false);
        subject = setupActivity(OnboardingActivity.class);
    }

    private void advance4Seconds() {
        Robolectric.getForegroundThreadScheduler().advanceBy(4000);
    }
}