package act.angelman.presentation.activity;


import android.support.annotation.NonNull;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import act.angelman.R;

import static act.angelman.presentation.activity.TestUtil.childAtPosition;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CardViewPagerTest {

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule = new ActivityTestRule<>(CategoryMenuActivity.class);

    @Before
    public void setUp() throws Exception {
        TestUtil.InitializeDatabase(mActivityTestRule.getActivity().getApplicationContext(), mActivityTestRule.getActivity().categoryRepository, mActivityTestRule.getActivity().cardRepository);
    }

    @Test
    public void cardViewPagerTest() {
        onView(secondCategoryItemTextViewMatcher()).check(matches(withText("놀이")));
        onView(secondCategoryItemViewMatcher()).perform(click());

        onView(categoryItemTitleTextViewMatcher()).check(matches(withText("놀이")));
        onView(categoryItemCountTextViewMatcher()).check(matches(withText("총 4장")));
        onView(addCardTextViewMatcher()).check(matches(withText("새 카드 만들기")));
        onView(withId(R.id.view_pager)).perform(swipeLeft());

        onView(withId(R.id.list_card_button)).check(matches(isDisplayed()));
        onView(cardShareButtonMatcher()).check(matches(isDisplayed()));
        onView(cardDeleteButtonMatcher()).check(matches(isDisplayed()));
        ViewInteraction categoryItemCountTextView = onView(categoryItemCountTextViewMatcher());
        categoryItemCountTextView.check(matches(withText("1 / 4")));
        onView(withId(R.id.view_pager)).perform(swipeLeft());

        categoryItemCountTextView.check(matches(withText("2 / 4")));
        onView(withId(R.id.view_pager)).perform(swipeLeft());

        categoryItemCountTextView.check(matches(withText("3 / 4")));
        onView(withId(R.id.view_pager)).perform(swipeLeft());

        categoryItemCountTextView.check(matches(withText("4 / 4")));
        onView(cardDeleteButtonMatcher()).perform(click());

        onView(deleteConfirmDialogMatcher()).check(matches(withText("'색칠 놀이 해요' 카드를 삭제하시겠습니까?")));
        ViewInteraction cancelButton = onView(withId(R.id.cancel_button));
        cancelButton.check(matches(withText("취소")));
        cancelButton.perform(click());

        onView(cardDeleteButtonMatcher()).perform(click());
        ViewInteraction confirmButton = onView(withId(R.id.confirm_button));
        confirmButton.check(matches(withText("확인")));
        confirmButton.perform(click());

        onView(categoryItemCountTextViewMatcher()).check(matches(withText("3 / 3")));
    }

    @NonNull
    private Matcher<View> deleteConfirmDialogMatcher() {
        return allOf(withId(R.id.alert_message),
                isDisplayed());
    }

    @NonNull
    private Matcher<View> cardDeleteButtonMatcher() {
        return allOf(withId(R.id.card_delete_button),
                childAtPosition(
                        allOf(withId(R.id.button_container),
                                childAtPosition(
                                        withId(R.id.category_item_container),
                                        2)),
                        0));
    }

    @NonNull
    private Matcher<View> cardShareButtonMatcher() {
        return allOf(withId(R.id.card_share_button),
                childAtPosition(
                        allOf(withId(R.id.button_container),
                                childAtPosition(
                                        withId(R.id.category_item_container),
                                        2)),
                        1));
    }

    @NonNull
    private Matcher<View> addCardTextViewMatcher() {
        return allOf(withId(R.id.add_card_text),
                childAtPosition(
                        childAtPosition(
                                withId(R.id.add_card_view_layout),
                                0),
                        0),
                isDisplayed());
    }

    @NonNull
    private Matcher<View> categoryItemCountTextViewMatcher() {
        return allOf(withId(R.id.category_item_count),
                childAtPosition(
                        allOf(withId(R.id.title_container),
                                childAtPosition(
                                        withId(R.id.title_container),
                                        0)),
                        2),
                isDisplayed());
    }

    @NonNull
    private Matcher<View> categoryItemTitleTextViewMatcher() {
        return allOf(withId(R.id.category_item_title),
                childAtPosition(
                        allOf(withId(R.id.title_container),
                                childAtPosition(
                                        withId(R.id.title_container),
                                        0)),
                        1),
                isDisplayed());
    }

    @NonNull
    private Matcher<View> secondCategoryItemViewMatcher() {
        return allOf(withId(R.id.category_item_card),
                childAtPosition(
                        withId(R.id.category_list),
                        1),
                isDisplayed());
    }

    @NonNull
    private Matcher<View> secondCategoryItemTextViewMatcher() {
        return allOf(withId(R.id.category_title),
                childAtPosition(childAtPosition(childAtPosition(childAtPosition(
                        childAtPosition(
                                withId(R.id.category_list),
                                1),
                        0), 0), 1), 1),
                isDisplayed());
    }
}
