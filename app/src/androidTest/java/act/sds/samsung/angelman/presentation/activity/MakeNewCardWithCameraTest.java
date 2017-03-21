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
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MakeNewCardWithCameraTest {

    @Rule
    public ActivityTestRule<OnboardingActivity> mActivityTestRule = new ActivityTestRule<>(OnboardingActivity.class);

    @Test
    public void makeNewCardWithCameraTest() throws InterruptedException {

        ViewInteraction cardView = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                withId(R.id.category_list),
                                1),
                        isDisplayed()));
        cardView.perform(click());

        ViewInteraction relativeLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.add_card_view_layout),
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        0)),
                        0),
                        isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.add_card_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.add_card_view_layout),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("새 카드 만들기")));

        ViewInteraction addCardView = onView(
                allOf(withClassName(is("act.sds.samsung.angelman.presentation.custom.AddCardView")),
                        withParent(allOf(withId(R.id.view_pager),
                                withParent(withId(R.id.category_item_container)))),
                        isDisplayed()));
        addCardView.perform(click());

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.layout_camera),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        relativeLayout2.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.text_take_picture),
                        childAtPosition(
                                allOf(withId(R.id.layout_camera),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("카메라")));

        ViewInteraction relativeLayout3 = onView(
                allOf(withId(R.id.layout_camera), isDisplayed()));
        relativeLayout3.perform(click());

        Thread.sleep(2000);
        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.camera_shutter), isDisplayed()));
        Thread.sleep(1000);
        frameLayout.perform(click());
        Thread.sleep(1000);

        ViewInteraction fontEditText = onView(
                allOf(withId(R.id.card_image_title_edit),
                        withParent(allOf(withId(R.id.card_container),
                                withParent(withId(R.id.card_view_layout)))),
                        isDisplayed()));
        Thread.sleep(1000);

        fontEditText.perform(replaceText("test"), closeSoftKeyboard());

        ViewInteraction fontEditText2 = onView(
                allOf(withId(R.id.card_image_title_edit),
                        withParent(allOf(withId(R.id.card_container),
                                withParent(withId(R.id.card_view_layout)))),
                        isDisplayed()));
        fontEditText2.perform(pressImeActionButton());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.card_image_title),
                        childAtPosition(
                                allOf(withId(R.id.card_container),
                                        childAtPosition(
                                                withId(R.id.card_view_layout),
                                                0)),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText("test")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.recoding_guide),
                        childAtPosition(
                                allOf(withId(R.id.show_card_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                                0)),
                                2),
                        isDisplayed()));
        textView4.check(matches(withText("카드 이름을 녹음해주세요")));

        ViewInteraction button = onView(
                allOf(withId(R.id.mic_btn),
                        childAtPosition(
                                allOf(withId(R.id.show_card_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                                0)),
                                3),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.mic_btn),
                        withParent(withId(R.id.show_card_layout)),
                        isDisplayed()));
        appCompatButton.perform(click());

        Thread.sleep(4000);

        ViewInteraction button3 = onView(
                allOf(withId(R.id.record_stop_btn),
                        withParent(withId(R.id.counting_scene)),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.waiting_count),
                        withParent(withId(R.id.counting_scene)),
                        isDisplayed()));
        textView5.check(matches(withText("지금 말해주세요!")));

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


        ViewInteraction relativeLayout4 = onView(
                allOf(withId(R.id.footer_container),
                        childAtPosition(
                                allOf(withId(R.id.category_item_container),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        relativeLayout4.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.category_item_count),
                        childAtPosition(
                                allOf(withId(R.id.title_container),
                                        childAtPosition(
                                                withId(R.id.title_container),
                                                0)),
                                2),
                        isDisplayed()));
        textView6.check(matches(withText("1 / 3")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.card_image_title),
                        childAtPosition(
                                allOf(withId(R.id.card_container),
                                        childAtPosition(
                                                withId(R.id.card_view_layout),
                                                0)),
                                1),
                        isDisplayed()));
        textView7.check(matches(withText("test")));

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
