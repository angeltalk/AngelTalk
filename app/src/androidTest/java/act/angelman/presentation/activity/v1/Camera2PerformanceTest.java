package act.angelman.presentation.activity.v1;


import android.support.annotation.NonNull;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import act.angelman.R;
import act.angelman.presentation.activity.CategoryMenuActivity;
import act.angelman.presentation.activity.TestUtil;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Camera2PerformanceTest {

    final static int TEST_TIMES = 2;

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule = new ActivityTestRule<>(CategoryMenuActivity.class);

    @Test
    public void camera2PerformanceTest() throws Exception {
        onView(firstCategoryItemViewMatcher()).perform(click());
        onView(addCareViewMatcher()).perform(click());

        for (int i = 0; i < TEST_TIMES; i++) {
            Log.d("camera2PerformanceTest", "camera2PerformanceTest (" + i + "/" + TEST_TIMES + ")");
            onView(addPhotoCardButtonMatcher()).perform(click());
            onView(cameraShutterMatcher()).perform(click());
            Thread.sleep(2000);

            ViewInteraction cardImageTitleEditView = onView(cardImageTitleEditMatcher());
            cardImageTitleEditView.perform(replaceText("pen"));
            Thread.sleep(1000);
            pressBack(); // To hide softKey
            pressBack(); // Go Back To CameraGallerySelection Activity
            Thread.sleep(1000);
        }
    }

    @NonNull
    private Matcher<View> cardImageTitleEditMatcher() {
        return allOf(withId(R.id.card_image_title_edit),
                withParent(allOf(withId(R.id.card_container),
                        withParent(withId(R.id.card_view_layout)))),
                isDisplayed());
    }

    @NonNull
    private Matcher<View> cameraShutterMatcher() {
        return allOf(withId(R.id.camera_shutter), isDisplayed());
    }

    @NonNull
    private Matcher<View> addPhotoCardButtonMatcher() {
        return allOf(withId(R.id.layout_camera), isDisplayed());
    }

    @NonNull
    private Matcher<View> addCareViewMatcher() {
        return allOf(withClassName(is("act.angelman.presentation.custom.AddCardView")),
                withParent(allOf(withId(R.id.view_pager),
                        withParent(withId(R.id.category_item_container)))),
                isDisplayed());
    }

    @NonNull
    private Matcher<View> firstCategoryItemViewMatcher() {
        return Matchers.allOf(withId(R.id.category_item_card),
                TestUtil.childAtPosition(
                        withId(R.id.category_list),
                        0),
                isDisplayed());
    }
}
