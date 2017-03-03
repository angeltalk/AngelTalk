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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MakeNewCategoryTest {

    @Rule
    public ActivityTestRule<OnboardingActivity> mActivityTestRule = new ActivityTestRule<>(OnboardingActivity.class);


    @Test
    public void makeNewCategoryTest() {
        ViewInteraction textView1 = onView(
                allOf(withId(R.id.category_title),
                        childAtPosition(childAtPosition(childAtPosition(childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                        5),
                                0),0),1),1),
                        isDisplayed()));
        textView1.check(matches(withText("새 카테고리")));

        ViewInteraction cardView = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                withId(R.id.category_list),
                                5),
                        isDisplayed()));
        cardView.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withText("새 카테고리"),
                        childAtPosition(
                                allOf(withId(R.id.new_category_header),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("새 카테고리")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.category_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.new_category_color),
                                        0),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText("카테고리 이름")));

        ViewInteraction fontEditText = onView(
                allOf(withId(R.id.edit_category_title), isDisplayed()));
        fontEditText.perform(replaceText(" 테스트"), closeSoftKeyboard());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.category_title), withText(" 테스트"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.new_category_color),
                                        0),
                                1),
                        isDisplayed()));
        textView4.check(matches(withText(" 테스트")));

        ViewInteraction fontEditText2 = onView(
                allOf(withId(R.id.edit_category_title), withText(" 테스트"), isDisplayed()));
        fontEditText2.perform(pressImeActionButton());

        ViewInteraction imageView = onView(
                allOf(withId(R.id.category_title_cancel),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                1),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.category_title_cancel), isDisplayed()));
        appCompatImageView2.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.category_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.new_category_color),
                                        0),
                                1),
                        isDisplayed()));
        textView5.check(matches(withText("카테고리 이름")));

        ViewInteraction editText = onView(
                allOf(withId(R.id.edit_category_title),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        editText.check(matches(withText("")));

        ViewInteraction fontEditText3 = onView(
                allOf(withId(R.id.edit_category_title), isDisplayed()));
        fontEditText3.perform(replaceText("테스트"), closeSoftKeyboard());

        ViewInteraction fontEditText4 = onView(
                allOf(withId(R.id.edit_category_title), withText("테스트"), isDisplayed()));
        fontEditText4.perform(pressImeActionButton());

        ViewInteraction button = onView(
                allOf(withId(R.id.new_category_save_button),
                        childAtPosition(
                                allOf(withId(R.id.new_category_header),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                2),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.new_category_save_button), withText("등록"),
                        withParent(withId(R.id.new_category_header)),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.category_item_title),
                        childAtPosition(
                                allOf(withId(R.id.title_container),
                                        childAtPosition(
                                                withId(R.id.title_container),
                                                0)),
                                1),
                        isDisplayed()));
        textView6.check(matches(withText("테스트")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.category_item_count),
                        childAtPosition(
                                allOf(withId(R.id.title_container),
                                        childAtPosition(
                                                withId(R.id.title_container),
                                                0)),
                                2),
                        isDisplayed()));
        textView7.check(matches(withText("총 0장")));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.add_card_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.add_card_view_layout),
                                        0),
                                0),
                        isDisplayed()));
        textView8.check(matches(withText("새 카드 만들기")));

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.back_button),
                        withParent(allOf(withId(R.id.title_container),
                                withParent(withId(R.id.title_container)))),
                        isDisplayed()));
        appCompatImageView3.perform(click());

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.category_title),
                        childAtPosition(childAtPosition(childAtPosition(childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                        5),
                                0),0),1),1),
                        isDisplayed()));
        textView9.check(matches(withText("테스트")));

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
