package angeltalk.plus.presentation.activity.v1;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import angeltalk.plus.R;
import angeltalk.plus.presentation.activity.OnboardingActivity;
import angeltalk.plus.presentation.activity.TestUtil;
import angeltalk.plus.presentation.manager.ApplicationConstants;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OnboardingAndCategoryMenuViewTest {

    @Rule
    public ActivityTestRule<OnboardingActivity> mActivityTestRule = new ActivityTestRule<>(OnboardingActivity.class);

    @Before
    public void setUp() throws Exception {
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

        onView(withId(R.id.onboarding_finish))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.drawer_meun))
                .check(matches(isDisplayed()));
        onView(withId(R.id.logo_angeltalk))
                .check(matches(isDisplayed()));
        onView(withId(R.id.category_delete_button))
                .check(matches(isDisplayed()))
                .check(matches(withText("삭제")));
        onView(newCategoryItemTitleMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText(resources.getString(R.string.new_category))));
    }

    @NonNull
    private Matcher<View> newCategoryItemTitleMatcher() {
        return Matchers.allOf(
                withId(R.id.category_title),
                TestUtil.childAtPosition(TestUtil.childAtPosition(TestUtil.childAtPosition(TestUtil.childAtPosition(
                        TestUtil.childAtPosition(
                                withId(R.id.category_list),
                                5),
                        0), 0), 1), 1
                )
        );
    }
}
