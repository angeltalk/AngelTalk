package act.angelman.presentation.activity;


import android.support.annotation.NonNull;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import act.angelman.R;

import static act.angelman.presentation.activity.TestUtil.childAtPosition;
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
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule = new ActivityTestRule<>(CategoryMenuActivity.class);

    @Before
    public void setUp() throws Exception {
        TestUtil.InitializeDatabase(mActivityTestRule.getActivity().getApplicationContext(), mActivityTestRule.getActivity().categoryRepository, mActivityTestRule.getActivity().cardRepository);
    }

    @Test
    public void deleteCategoryTest() {

        ViewInteraction categoryDeleteButton = onView(withId(R.id.category_delete_button));

        // TODO: 삭제버튼 -> 오들오들에니메이션 코드 진행시 perform(click())과 충돌... espresso에서 looping animation 지원안함
        categoryDeleteButton.check(matches(withText("삭제")))
                .check(matches(isDisplayed()))
                .perform(click());

        categoryDeleteButton.check(matches(withText("완료")))
                .check(matches(isDisplayed()));

        onView(secondCategoryItemDeleteButton()).check(matches(isDisplayed()));

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
                allOf(withId(R.id.confirm_button), withText("확인"),
                        childAtPosition(childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                1), 0),
                        isDisplayed()));
        textView2.check(matches(withText("확인")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.cancel_button), withText("취소"),
                        childAtPosition(childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                1), 1),
                        isDisplayed()));
        textView3.check(matches(withText("취소")));

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.confirm_button), withText("확인"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction cardView4 = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                3),
                        isDisplayed()));
        cardView4.perform(click());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.confirm_button), withText("확인"), isDisplayed()));
        appCompatTextView3.perform(click());

        ViewInteraction cardView5 = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                2),
                        isDisplayed()));
        cardView5.perform(click());

        ViewInteraction appCompatTextView4 = onView(
                allOf(withId(R.id.confirm_button), withText("확인"), isDisplayed()));
        appCompatTextView4.perform(click());

        ViewInteraction cardView6 = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                1),
                        isDisplayed()));
        cardView6.perform(click());

        ViewInteraction appCompatTextView5 = onView(
                allOf(withId(R.id.confirm_button), withText("확인"), isDisplayed()));
        appCompatTextView5.perform(click());

        ViewInteraction cardView8 = onView(
                allOf(withId(R.id.category_item_card),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                                0),
                        isDisplayed()));
        cardView8.perform(click());

        ViewInteraction appCompatTextView7 = onView(
                allOf(withId(R.id.confirm_button), withText("확인"), isDisplayed()));
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
                allOf(withId(R.id.confirm_button), withText("확인"), isDisplayed()));
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
                                0), 0), 1), 1),
                        isDisplayed()));
        textView7.check(matches(withText("새 카테고리")));

    }

    @NonNull
    private Matcher<View> secondCategoryItemDeleteButton() {
        return allOf(withId(R.id.delete_button),
                childAtPosition(childAtPosition(childAtPosition(childAtPosition(
                        IsInstanceOf.<View>instanceOf(android.widget.GridView.class),
                        1),0),0),0));
    }
}
