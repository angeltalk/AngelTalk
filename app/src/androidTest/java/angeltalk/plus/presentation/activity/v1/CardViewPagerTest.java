package angeltalk.plus.presentation.activity.v1;


import androidx.test.espresso.ViewInteraction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import angeltalk.plus.R;
import angeltalk.plus.presentation.activity.CategoryMenuActivity;
import angeltalk.plus.presentation.activity.TestUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CardViewPagerTest {

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule =
            new ActivityTestRule<>(CategoryMenuActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        // Reset DB BEFORE the activity launches so the menu sees the default 5 categories
        // with their default cards (the "놀이" category has 4 default cards, which the
        // assertions below depend on).
        TestUtil.resetDatabaseToDefaults(
                InstrumentationRegistry.getInstrumentation().getTargetContext());
        mActivityTestRule.launchActivity(null);
    }

    @Test
    public void cardViewPagerTest() {
        // The "놀이" category cell on CategoryMenuActivity — content-based.
        onView(allOf(withId(R.id.category_title), withText("놀이"), isDisplayed()))
                .perform(click());

        // CardListActivity for the "놀이" category. Default data has 5 cards.
        onView(withId(R.id.category_item_title)).check(matches(withText("놀이")));
        onView(withId(R.id.category_item_count)).check(matches(withText("총 5장")));
        onView(withId(R.id.add_card_text)).check(matches(withText("새 카드 만들기")));

        // First swipe leaves the "add card" cell and lands on card 1/5.
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.list_card_button)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.card_share_button), isDisplayed())).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.card_delete_button), isDisplayed())).check(matches(isDisplayed()));
        ViewInteraction categoryItemCount = onView(withId(R.id.category_item_count));
        categoryItemCount.check(matches(withText("1 / 5")));

        onView(withId(R.id.view_pager)).perform(swipeLeft());
        categoryItemCount.check(matches(withText("2 / 5")));

        onView(withId(R.id.view_pager)).perform(swipeLeft());
        categoryItemCount.check(matches(withText("3 / 5")));

        onView(withId(R.id.view_pager)).perform(swipeLeft());
        categoryItemCount.check(matches(withText("4 / 5")));

        onView(withId(R.id.view_pager)).perform(swipeLeft());
        categoryItemCount.check(matches(withText("5 / 5")));

        // Delete the current card. Cancel first to verify the cancel button works.
        onView(allOf(withId(R.id.card_delete_button), isDisplayed())).perform(click());
        // The dialog text varies with the active card name; only verify the dialog appears.
        onView(allOf(withId(R.id.alert_message), isDisplayed())).check(matches(isDisplayed()));
        ViewInteraction cancelButton = onView(withId(R.id.cancel_button));
        cancelButton.check(matches(withText("취소")));
        cancelButton.perform(click());

        onView(allOf(withId(R.id.card_delete_button), isDisplayed())).perform(click());
        ViewInteraction confirmButton = onView(withId(R.id.confirm_button));
        confirmButton.check(matches(withText("확인")));
        confirmButton.perform(click());

        // After deleting one of 5 cards, the pager auto-shifts and the new total is 4.
        onView(withId(R.id.category_item_count)).check(matches(withText("4 / 4")));
    }
}
