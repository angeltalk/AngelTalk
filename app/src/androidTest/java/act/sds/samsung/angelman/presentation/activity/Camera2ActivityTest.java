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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import act.sds.samsung.angelman.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Camera2ActivityTest {

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule = new ActivityTestRule<>(CategoryMenuActivity.class);

    @Test
    public void camera2ActivityTest() throws Exception {

        ViewInteraction cardView = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                withId(R.id.category_list),
                                0),
                        isDisplayed()));
        cardView.perform(click());

        ViewInteraction addCardView = onView(
                allOf(withClassName(is("act.sds.samsung.angelman.presentation.custom.AddCardView")),
                        withParent(allOf(withId(R.id.view_pager),
                                withParent(withId(R.id.category_item_container)))),

                        isDisplayed()));
        addCardView.perform(click());

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.camera_start_card), isDisplayed()));
        relativeLayout.perform(click());

        Thread.sleep(2000);
        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.camera_shutter), isDisplayed()));
        Thread.sleep(1000);
        frameLayout.perform(click());
        Thread.sleep(1000);
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.card_image_title_edit),
                        withParent(allOf(withId(R.id.card_container),
                                withParent(withId(R.id.card_view_layout)))),
                        isDisplayed()));

        Thread.sleep(1000);
        appCompatEditText.perform(replaceText("pen"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.card_image_title_edit), withText("pen"),
                        withParent(allOf(withId(R.id.card_container),
                                withParent(withId(R.id.card_view_layout)))),
                        isDisplayed()));
        appCompatEditText2.perform(pressImeActionButton());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.mic_btn), isDisplayed()));
        appCompatButton.perform(click());

        Thread.sleep(4000);

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.record_stop_btn),
                        withParent(withId(R.id.counting_scene)),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.record_stop_btn),
                        withParent(withId(R.id.counting_scene)),
                        isDisplayed()));
        appCompatButton3.perform(click());

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
