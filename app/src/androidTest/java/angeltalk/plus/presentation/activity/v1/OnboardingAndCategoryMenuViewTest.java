package angeltalk.plus.presentation.activity.v1;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import angeltalk.plus.R;
import angeltalk.plus.presentation.activity.OnboardingActivity;
import angeltalk.plus.presentation.activity.TestUtil;
import angeltalk.plus.presentation.manager.ApplicationConstants;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OnboardingAndCategoryMenuViewTest {

    @Rule
    public ActivityTestRule<OnboardingActivity> mActivityTestRule = new ActivityTestRule<>(OnboardingActivity.class);

    @Before
    public void setUp() throws Exception {
        // Grant SYSTEM_ALERT_WINDOW so checkDrawOverlayPermission() doesn't bounce the
        // test out to system Settings after onboarding finishes.
        TestUtil.grantOverlayPermission();
        SharedPreferences preferences = mActivityTestRule.getActivity().getApplicationContext().getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(ApplicationConstants.FIRST_LAUNCH, true);
        edit.commit();
    }

    @Test
    public void onboardingAndCategoryMenuViewTest() throws InterruptedException {

        Resources resources = mActivityTestRule.getActivity().getResources();

        onView(withId(R.id.onboarding_start))
                .check(matches(isDisplayed()));

        Thread.sleep(4000); // wait for flying angel

        onView(withId(R.id.onboarding_view_pager))
                .perform(swipeLeft())
                .perform(swipeLeft())
                .perform(swipeLeft())
                .perform(swipeLeft());

        // R.id.onboarding_finish exists in every ViewPager page; UiAutomator-click
        // picks the visible one via accessibility. Using UiAutomator for both the
        // click and the subsequent wait avoids any dependence on main-looper-idle
        // state that's been unreliable on this transition.
        TestUtil.uiAutomatorClick("onboarding_finish");

        // Wait for CategoryMenuActivity to come up. The transition chains through
        // requestPermissions → callback → checkDrawOverlayPermission → moveToCategoryMenu
        // and can be slow on cold start, so allow a generous timeout.
        TestUtil.uiAutomatorWaitForId("drawer_meun", 15_000);

        onView(withId(R.id.drawer_meun))
                .check(matches(isDisplayed()));
        onView(withId(R.id.logo_angeltalk))
                .check(matches(isDisplayed()));
        // category_delete_button is an ImageView (drawable-only), no text to assert.
        onView(withId(R.id.category_delete_button))
                .check(matches(isDisplayed()));
        // The "new category" placeholder cell is uniquely identified by its category_title
        // text — content-based matcher is robust to layout/position changes.
        onView(allOf(withId(R.id.category_title), withText(resources.getString(R.string.new_category))))
                .check(matches(isDisplayed()));
    }
}
