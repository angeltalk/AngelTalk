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
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CardViewPagerTest {

    @Rule
    public ActivityTestRule<OnboardingActivity> mActivityTestRule = new ActivityTestRule<>(OnboardingActivity.class);

    @Test
    public void cardViewPagerTest() {

        ViewInteraction textView = onView(
                allOf(withId(R.id.category_title),
                        childAtPosition(childAtPosition(childAtPosition(childAtPosition(
                                childAtPosition(
                                        withId(R.id.category_list),
                                        1),
                                0),0),1),1),
                        isDisplayed()));
        textView.check(matches(withText("놀이")));

        ViewInteraction cardView = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                withId(R.id.category_list),
                                1),
                        isDisplayed()));
        cardView.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.category_item_title),
                        childAtPosition(
                                allOf(withId(R.id.title_container),
                                        childAtPosition(
                                                withId(R.id.title_container),
                                                0)),
                                1),
                        isDisplayed()));
        textView4.check(matches(withText("놀이")));


        ViewInteraction textView3 = onView(
                allOf(withId(R.id.category_item_count),
                        childAtPosition(
                                allOf(withId(R.id.title_container),
                                        childAtPosition(
                                                withId(R.id.title_container),
                                                0)),
                                2),
                        isDisplayed()));
        textView3.check(matches(withText("총 3장")));


        ViewInteraction textView2 = onView(
                allOf(withId(R.id.add_card_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.add_card_view_layout),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("새 카드 만들기")));


        ViewInteraction viewPager = onView(allOf(withId(R.id.view_pager)));
        viewPager.perform(swipeLeft());


        ViewInteraction textView5 = onView(
                allOf(withId(R.id.add_card_button_text),
                        isDisplayed()));
        textView5.check(matches(withText("새 카드")));

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.card_delete_button),
                        childAtPosition(
                                allOf(withId(R.id.button_container),
                                        childAtPosition(
                                                withId(R.id.category_item_container),
                                                2)),
                                0),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

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


        viewPager.perform(swipeLeft());
        textView6.check(matches(withText("2 / 3")));

        viewPager.perform(swipeLeft());
        textView6.check(matches(withText("3 / 3")));


        ViewInteraction imageButton2 = onView(
                allOf(withId(R.id.card_delete_button),
                        childAtPosition(
                                allOf(withId(R.id.button_container),
                                        childAtPosition(
                                                withId(R.id.category_item_container),
                                                2)),
                                0),
                        isDisplayed()));
        imageButton2.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.card_delete_button),
                        withParent(allOf(withId(R.id.button_container),
                                withParent(withId(R.id.category_item_container)))),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.alert_message),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        textView7.check(matches(withText("'블럭' 카드를 삭제하시겠습니까?")));

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.cancel), withText("취소"), isDisplayed()));
        appCompatTextView.perform(click());

        appCompatImageButton2.perform(click());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.confirm), withText("확인"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.category_item_count), withText("2 / 2"),
                        childAtPosition(
                                allOf(withId(R.id.title_container),
                                        childAtPosition(
                                                withId(R.id.title_container),
                                                0)),
                                2),
                        isDisplayed()));
        textView8.check(matches(withText("2 / 2")));
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
