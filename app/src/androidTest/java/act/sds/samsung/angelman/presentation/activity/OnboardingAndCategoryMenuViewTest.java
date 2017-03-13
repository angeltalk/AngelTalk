package act.sds.samsung.angelman.presentation.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import act.sds.samsung.angelman.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OnboardingAndCategoryMenuViewTest {

    @Rule
    public ActivityTestRule<OnboardingActivity> mActivityTestRule = new ActivityTestRule<>(OnboardingActivity.class);

    @Test
    public void onboardingAndCategoryMenuViewTest() throws InterruptedException {

        Thread.sleep(2000);

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.onboarding_start),
                        withParent(withId(R.id.onboarding_first_page)),
                        isDisplayed()));
        appCompatImageButton.check(matches(isDisplayed()));
        appCompatImageButton.perform(click());


        onView(allOf(withId(R.id.onboarding_view_pager)))
                .perform(swipeLeft())
                .perform(swipeLeft())
                .perform(swipeLeft())
                .perform(swipeLeft());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.onboarding_finish), isDisplayed()));
        appCompatImageView.check(matches(isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.category_delete_button),
                        withParent(withId(R.id.clock_layout)),
                        isDisplayed()));
        textView.check(matches(withText("삭제")));

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.send_voc),
                        withParent(withId(R.id.clock_layout)),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.category_title),
                        childAtPosition(childAtPosition(childAtPosition(childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                        5),
                                0),0),1),1),
                        isDisplayed()));
        textView2.check(matches(withText("새 카테고리")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
