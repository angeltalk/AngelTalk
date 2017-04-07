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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
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
public class MakeNewCardWithCameraTest {

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule = new ActivityTestRule<>(CategoryMenuActivity.class);

    @Test
    public void makeNewCardWithCameraTest() throws InterruptedException {

        onView(secondCategoryItemView())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.add_card_view_layout))
                .check(matches(isDisplayed()));
        onView(withId(R.id.add_card_text))
                .check(matches(isDisplayed()))
                .check(matches(withText("새 카드 만들기")));
        onView(withId(R.id.add_card_view_layout)).perform(click());

        onView(withId(R.id.layout_camera))
                .check(matches(isDisplayed()));
        onView(withId(R.id.text_take_picture))
                .check(matches(isDisplayed()))
                .check(matches(withText("사진 찍기")));
        onView(withId(R.id.layout_camera)).perform(click());

        Thread.sleep(2000); // wait for loading camera

        onView(withId(R.id.camera_shutter))
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(2000); // wait for take a picture

        onView(withId(R.id.card_image_title_edit))
                .check(matches(isDisplayed()))
                .perform(replaceText("Make New Card"));
        Thread.sleep(1000);
        pressBack();
        onView(withId(R.id.card_image_title_edit))
                .perform(pressImeActionButton());

//        onView(withId(R.id.card_image_title_edit))
//                .check(matches(isDisplayed()))
//                .check(matches(withText("Make New Card")));
        onView(withId(R.id.recoding_guide))
                .check(matches(isDisplayed()))
                .check(matches(withText("카드 이름을 녹음해주세요")));
        onView(withId(R.id.mic_btn))
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(4000); // wait for record

        ViewInteraction button3 = onView(
                allOf(withId(R.id.record_stop_btn),
                        withParent(withId(R.id.counting_scene)),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.waiting_count),
                        withParent(withId(R.id.counting_scene)),
                        isDisplayed()));
        textView5.check(matches(withText("지금 말해주세요!")));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.record_stop_btn),
                        withParent(withId(R.id.counting_scene)),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.record_stop_btn),
                        withParent(withId(R.id.counting_scene)),
                        isDisplayed()));
        appCompatButton3.perform(click());


        ViewInteraction relativeLayout4 = onView(
                allOf(withId(R.id.button_container),
                        childAtPosition(
                                allOf(withId(R.id.category_item_container),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        relativeLayout4.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.category_item_count),
                        childAtPosition(
                                allOf(withId(R.id.title_container),
                                        childAtPosition(
                                                withId(R.id.title_container),
                                                0)),
                                2),
                        isDisplayed()));
        textView6.check(matches(withText("1 / 3")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.card_image_title),
                        childAtPosition(
                                allOf(withId(R.id.card_container),
                                        childAtPosition(
                                                withId(R.id.card_view_layout),
                                                0)),
                                1),
                        isDisplayed()));
        textView7.check(matches(withText("test")));

    }

    @NonNull
    private Matcher<View> secondCategoryItemView() {
        return allOf(withId(R.id.category_item_card),
                childAtPosition(
                        withId(R.id.category_list),
                        1));
    }
}
