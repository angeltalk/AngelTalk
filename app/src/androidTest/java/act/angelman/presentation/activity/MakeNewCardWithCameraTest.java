package act.angelman.presentation.activity;


import android.support.annotation.NonNull;
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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MakeNewCardWithCameraTest {

    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule = new ActivityTestRule<>(CategoryMenuActivity.class);

    @Before
    public void setUp() throws Exception {
        TestUtil.InitializeDatabase(mActivityTestRule.getActivity().getApplicationContext(), mActivityTestRule.getActivity().categoryRepository, mActivityTestRule.getActivity().cardRepository);
    }

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
                .perform(replaceText("New Card"));
        Thread.sleep(1000);
        pressBack();
        onView(withId(R.id.card_image_title_edit))
                .perform(pressImeActionButton());

        Thread.sleep(1000);

        onView(withId(R.id.recoding_guide))
                .check(matches(isDisplayed()))
                .check(matches(withText("카드 이름을 녹음해주세요")));
        onView(withId(R.id.mic_btn))
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(4500); // wait for record

        onView(withId(R.id.waiting_count))
                .check(matches(isDisplayed()))
                .check(matches(withText("지금 말해주세요!")));
        onView(withId(R.id.record_stop_btn))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.waiting_count))
                .check(matches(isDisplayed()))
                .check(matches(withText("녹음된 음성을 확인하세요")));
        onView(withId(R.id.record_stop_btn))
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.snackbar_text))
                .check(matches(isDisplayed()))
                .check(matches(withText("새로운 카드가 추가되었습니다.")));

        Thread.sleep(2500); // wait for dismiss snackbar

        onView(withId(R.id.snackbar_text))
                .check(doesNotExist());
        onView(withId(R.id.category_item_count))
                .check(matches(isDisplayed()))
                .check(matches(withText("1 / 5")));
        onView(cardViewPagerFirstItem())
                .check(matches(isDisplayed()))
                .check(matches(withText("New Card")));
    }

    @NonNull
    private Matcher<View> cardViewPagerFirstItem() {
        return allOf(withId(R.id.card_image_title),
                    childAtPosition(childAtPosition(childAtPosition(childAtPosition(
                            withId(R.id.view_pager)
                            ,0),0),0),1)
        );
    }

    @NonNull
    private Matcher<View> secondCategoryItemView() {
        return allOf(withId(R.id.category_item_card),
                childAtPosition(
                        withId(R.id.category_list),
                        1));
    }
}
