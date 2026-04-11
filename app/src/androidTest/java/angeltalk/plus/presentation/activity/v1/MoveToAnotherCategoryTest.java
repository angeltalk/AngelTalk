package angeltalk.plus.presentation.activity.v1;


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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MoveToAnotherCategoryTest {

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule =
            new ActivityTestRule<>(CategoryMenuActivity.class, true, false);

    @Before
    public void setUp() {
        TestUtil.resetDatabaseToDefaults(
                InstrumentationRegistry.getInstrumentation().getTargetContext());
        mActivityTestRule.launchActivity(null);
    }

    @Test
    public void moveToAnotherCategoryTest() {
        // Open the "놀이" category by its title text (content-based, layout-independent).
        onView(allOf(withId(R.id.category_title), withText("놀이"), isDisplayed()))
                .perform(click());

        onView(withId(R.id.category_item_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("놀이")));
        onView(withId(R.id.back_button))
                .check(matches(isDisplayed()))
                .perform(click());

        // Back on CategoryMenuActivity — verify the menu is displayed by its logo.
        onView(withId(R.id.logo_angeltalk))
                .check(matches(isDisplayed()));

        // Open the "가고 싶은 곳" category.
        onView(allOf(withId(R.id.category_title), withText("가고 싶은 곳"), isDisplayed()))
                .perform(click());

        onView(withId(R.id.category_item_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("가고 싶은 곳")));
    }
}
