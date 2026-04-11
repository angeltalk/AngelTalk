package angeltalk.plus.presentation.activity.v1;


import androidx.test.espresso.ViewInteraction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import angeltalk.plus.R;
import angeltalk.plus.presentation.activity.CategoryMenuActivity;
import angeltalk.plus.presentation.activity.TestUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Camera2PerformanceTest {

    final static int TEST_TIMES = 2;

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
    public void camera2PerformanceTest() throws Exception {
        // Open the "음식" category (the first default category) by content.
        onView(allOf(withId(R.id.category_title), withText("음식"), isDisplayed()))
                .perform(click());
        // On the card list screen, tap the "새 카드 만들기" cell to enter the camera flow.
        onView(allOf(withId(R.id.add_card_text), withText("새 카드 만들기"), isDisplayed()))
                .perform(click());

        for (int i = 0; i < TEST_TIMES; i++) {
            Log.d("camera2PerformanceTest", "camera2PerformanceTest (" + i + "/" + TEST_TIMES + ")");
            onView(allOf(withId(R.id.layout_camera), isDisplayed())).perform(click());
            // Camera2Activity needs time to initialise the fake emulator camera
            // pipeline (Camera2 + HandlerThread + surface texture). Wait for the
            // shutter button to be laid out and hittable.
            TestUtil.uiAutomatorWaitForId("camera_shutter", 10_000);
            onView(allOf(withId(R.id.camera_shutter), isDisplayed())).perform(click());
            // Capture + ImageSaver + startActivity(MakeCardPreviewActivity) is async.
            // Wait for the preview screen's confirm button to appear before clicking.
            TestUtil.uiAutomatorWaitForId("confirm_button", 15_000);
            onView(allOf(withId(R.id.confirm_button), isDisplayed())).perform(click());
            // Next screen is the title entry flow.
            TestUtil.uiAutomatorWaitForId("card_image_title_edit", 10_000);

            ViewInteraction cardImageTitleEditView =
                    onView(allOf(withId(R.id.card_image_title_edit), isDisplayed()));
            cardImageTitleEditView.perform(replaceText("pen"));
            Thread.sleep(1000);
            pressBack(); // hide soft keyboard
            pressBack(); // back to CameraGallerySelectionActivity
            Thread.sleep(1000);
        }
    }
}
