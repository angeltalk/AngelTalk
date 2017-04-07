package act.sds.samsung.angelman.presentation.activity;


import android.support.annotation.NonNull;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import act.sds.samsung.angelman.R;

import static act.sds.samsung.angelman.presentation.activity.TestUtil.childAtPosition;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Camera2ActivityTest {

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule = new ActivityTestRule<>(CategoryMenuActivity.class);

    @Test
    public void camera2ActivityTest() throws Exception {
        onView(firstCategoryItemViewMatcher()).perform(click());
        onView(addCardViewMatcher()).perform(click());
        onView(addPhotoCardButtonMatcher()).perform(click());
        Thread.sleep(2000);
        onView(cameraShutterMatcher()).perform(click());
        Thread.sleep(2000);

        ViewInteraction cardImageTitleView = onView(cardImageTitleEditMatcher());
        cardImageTitleView.perform(replaceText("pen"));
        Thread.sleep(1000);
        cardImageTitleView.check(matches(withText("pen")));
        pressBack();
        cardImageTitleView.perform(pressImeActionButton());

        onView(recordStartButtonMatcher()).perform(click());
        Thread.sleep(4500);

        ViewInteraction recordStopButton = onView(recordStopButtonMatcher());
        recordStopButton.perform(click());  // stop recording
        recordStopButton.perform(click());  // confirm save
    }

    @NonNull
    private Matcher<View> recordStopButtonMatcher() {
        return allOf(withId(R.id.record_stop_btn),
                isDisplayed());
    }

    @NonNull
    private Matcher<View> recordStartButtonMatcher() {
        return allOf(withId(R.id.mic_btn), isDisplayed());
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
    private Matcher<View> addCardViewMatcher() {
        return allOf(
                withClassName(is("act.sds.samsung.angelman.presentation.custom.AddCardView")),
                withParent(allOf(
                        withId(R.id.view_pager),
                        withParent(withId(R.id.category_item_container))
                        )
                ),
                isDisplayed()
        );
    }

    @NonNull
    private Matcher<View> firstCategoryItemViewMatcher() {
        return allOf(
                withId(R.id.category_item_card),
                childAtPosition(
                        withId(R.id.category_list),
                        0
                ),
                isDisplayed()
        );
    }
}
