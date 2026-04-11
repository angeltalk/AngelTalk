package angeltalk.plus.presentation.activity.v1;


import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import angeltalk.plus.R;
import angeltalk.plus.presentation.activity.CategoryMenuActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MakeNewCategoryTest {

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule = new ActivityTestRule<>(CategoryMenuActivity.class);

    @Before
    public void setUp() throws Exception {
        // Activity is launched by ActivityTestRule before @Before runs, so we can't
        // initialize the DB here without recreating the activity. The test relies on
        // whatever default data exists. The new "테스트" category created by this test
        // is left behind in the DB; duplicate creation is allowed by production code,
        // so back-to-back runs still work.
    }

    @Test
    public void makeNewCategoryTest() {
        // The "new category" placeholder cell on CategoryMenuActivity is uniquely
        // identified by its category_title text.
        onView(allOf(withId(R.id.category_title), withText(R.string.new_category)))
                .check(matches(isDisplayed()))
                .perform(click());

        // We are now on MakeCategoryActivity. The header TextView shows "새 카테고리".
        onView(allOf(withId(R.id.new_category_header), isDisplayed()))
                .check(matches(isDisplayed()));
        // category_title is the cell preview text inside the new-category screen
        // (only one match here since it's not in a list).
        onView(withId(R.id.category_title))
                .check(matches(withText(R.string.new_category_name)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.edit_category_title))
                .check(matches(isDisplayed()))
                .perform(replaceText("취소테스트"), closeSoftKeyboard())
                .perform(pressImeActionButton());

        onView(withId(R.id.category_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("취소테스트")));
        onView(withId(R.id.category_title_cancel))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.category_title))
                .check(matches(withText(R.string.new_category_name)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.edit_category_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("")));
        // new_category_save_button is an ImageView; "enabled" reflects the empty/non-empty
        // EditText state (set in MakeCategoryActivity via setEnabled + setImageAlpha).
        onView(withId(R.id.new_category_save_button))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.edit_category_title))
                .check(matches(isDisplayed()))
                .perform(replaceText("테스트"), closeSoftKeyboard())
                .perform(pressImeActionButton());

        onView(withId(R.id.new_category_save_button))
                .check(matches(isEnabled()))
                .check(matches(isDisplayed()))
                .perform(click());

        // Back on CategoryMenuActivity → CardListActivity for the new category.
        onView(withId(R.id.category_item_title))
                .check(matches(withText("테스트")))
                .check(matches(isDisplayed()));
        onView(withId(R.id.category_item_count))
                .check(matches(withText("총 0장")))
                .check(matches(isDisplayed()));
        onView(withId(R.id.back_button))
                .perform(click());

        // The new "테스트" category should now be visible in the menu grid alongside
        // the (still-displayed) "새 카테고리" placeholder. Match by content, not position.
        onView(allOf(withId(R.id.category_title), withText("테스트")))
                .check(matches(isDisplayed()));
    }
}
