package act.angelman.presentation.activity;


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

import act.angelman.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MoveToAnotherCategoryTest {

    @Rule
    public ActivityTestRule<OnboardingActivity> mActivityTestRule = new ActivityTestRule<>(OnboardingActivity.class);

    @Test
    public void moveToAnotherCategoryTest() {

        ViewInteraction cardView = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                withId(R.id.category_list),
                                1),
                        isDisplayed()));
        cardView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.category_item_title),
                        childAtPosition(
                                allOf(withId(R.id.title_container),
                                        childAtPosition(
                                                withId(R.id.title_container),
                                                0)),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("놀이")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.back_button),
                        childAtPosition(
                                allOf(withId(R.id.title_container),
                                        childAtPosition(
                                                withId(R.id.title_container),
                                                0)),
                                0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.back_button),
                        withParent(allOf(withId(R.id.title_container),
                                withParent(withId(R.id.title_container)))),
                        isDisplayed()));
        appCompatImageView2.perform(click());

        ViewInteraction imageView2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.clock_layout),
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        0)),
                        1),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction cardView2 = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                withId(R.id.category_list),
                                3),
                        isDisplayed()));
        cardView2.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.category_item_title),
                        childAtPosition(
                                allOf(withId(R.id.title_container),
                                        childAtPosition(
                                                withId(R.id.title_container),
                                                0)),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("가고 싶은 곳")));

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
