package angeltalk.plus.presentation.activity.v1;


import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import angeltalk.plus.presentation.activity.CategoryMenuActivity;
import angeltalk.plus.presentation.activity.TestUtil;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DeleteCategoryTest {

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule =
            new ActivityTestRule<>(CategoryMenuActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        // Reset to the 5 default categories so the deletion sequence is deterministic.
        TestUtil.resetDatabaseToDefaults(
                InstrumentationRegistry.getInstrumentation().getTargetContext());
        mActivityTestRule.launchActivity(null);
    }

    @Test
    public void deleteCategoryTest() {
        // Clicks in this test use UiAutomator because the shake_anim on category
        // cells (started when delete mode is toggled on) is infinite — Espresso's
        // main-looper-idle wait would time out with AppNotIdleException.
        // UiAutomator drives the UI via the accessibility service and doesn't
        // care about the looper's idle state. Assertions still use Espresso
        // matchers where the looper is momentarily idle (e.g. inside the dialog).
        TestUtil.uiAutomatorClick("category_delete_button");

        // Delete categories one at a time. Each deletion shifts the remaining cells
        // down so position 0 always points at the next real category. With 5 default
        // categories, the first 4 deletions just confirm; the 5th hits the
        // "최소 1개" alert because count drops to 1 in CategoryMenuActivity.
        for (int i = 0; i < 4; i++) {
            TestUtil.uiAutomatorClick("category_item_card");
            TestUtil.uiAutomatorClick("confirm_button");
        }

        // 5th click — only one category remains, so the alert fires instead of deleting.
        TestUtil.uiAutomatorClick("category_item_card");
        TestUtil.uiAutomatorWaitForId("alert_message");
        TestUtil.uiAutomatorClick("confirm_button");

        // Confirm on the alert kicks off moveToNewCategoryActivity().
        TestUtil.uiAutomatorWaitForId("new_category_header");
    }
}
