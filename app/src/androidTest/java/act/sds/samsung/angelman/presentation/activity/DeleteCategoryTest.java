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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DeleteCategoryTest {

    @Rule
    public ActivityTestRule<OnboardingActivity> mActivityTestRule = new ActivityTestRule<>(OnboardingActivity.class);

    @Test
    public void deleteCategoryTest() {

        ViewInteraction fontTextView5 = onView(
                allOf(withId(R.id.category_delete_button), withText("삭제"),
                        withParent(withId(R.id.clock_layout)),
                        isDisplayed()));
        fontTextView5.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.category_delete_button),
                        childAtPosition(
                                allOf(withId(R.id.clock_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                2),
                        isDisplayed()));
        textView.check(matches(withText("완료")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.delete_button),
                        childAtPosition(
                                childAtPosition(childAtPosition(childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                        1),
                                0),0),0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction cardView3 = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                4),
                        isDisplayed()));
        cardView3.perform(click());

        ViewInteraction linearLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.custom),
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0)),
                        0),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.confirm), withText("확인"),
                        childAtPosition(childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                1), 0),
                        isDisplayed()));
        textView2.check(matches(withText("확인")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.cancel), withText("취소"),
                        childAtPosition(childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                1), 1),
                        isDisplayed()));
        textView3.check(matches(withText("취소")));

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.confirm), withText("확인"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction cardView4 = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                3),
                        isDisplayed()));
        cardView4.perform(click());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.confirm), withText("확인"), isDisplayed()));
        appCompatTextView3.perform(click());

        ViewInteraction cardView5 = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                2),
                        isDisplayed()));
        cardView5.perform(click());

        ViewInteraction appCompatTextView4 = onView(
                allOf(withId(R.id.confirm), withText("확인"), isDisplayed()));
        appCompatTextView4.perform(click());

        ViewInteraction cardView6 = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                1),
                        isDisplayed()));
        cardView6.perform(click());

        ViewInteraction appCompatTextView5 = onView(
                allOf(withId(R.id.confirm), withText("확인"), isDisplayed()));
        appCompatTextView5.perform(click());

        ViewInteraction cardView8 = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                0),
                        isDisplayed()));
        cardView8.perform(click());

        ViewInteraction appCompatTextView7 = onView(
                allOf(withId(R.id.confirm), withText("확인"), isDisplayed()));
        appCompatTextView7.perform(click());


        ViewInteraction cardView7 = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                0),
                        isDisplayed()));
        cardView7.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.alert_message),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView4.check(matches(withText("카테고리는 최소 1개 이상이어야 합니다. 삭제 후 새 카테고리를 만드시겠습니까?")));

        ViewInteraction appCompatTextView8 = onView(
                allOf(withId(R.id.confirm), withText("확인"), isDisplayed()));
        appCompatTextView8.perform(click());

        ViewInteraction textView6 = onView(
                allOf(withText("새 카테고리"),
                        childAtPosition(
                                allOf(withId(R.id.new_category_header),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView6.check(matches(withText("새 카테고리")));

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.left_arrow_button),
                        withParent(withId(R.id.new_category_header)),
                        isDisplayed()));
        appCompatImageView3.perform(click());

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.category_title),
                        childAtPosition(childAtPosition(childAtPosition(childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                        0),
                                0),0),1),1),
                        isDisplayed()));
        textView7.check(matches(withText("새 카테고리")));

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
