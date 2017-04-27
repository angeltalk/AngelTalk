package act.angelman.presentation.activity;

import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import act.angelman.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateNewCard {

    // 앱 실행 → 엄마모드 카테고리 보기 화면이 표시됨
    @Rule
    public ActivityTestRule<CategoryMenuActivity> mActivityTestRule = new ActivityTestRule<>(CategoryMenuActivity.class);

    @Before
    public void setUp() throws Exception {
        TestUtil.InitializeDatabase(mActivityTestRule.getActivity().getApplicationContext(), mActivityTestRule.getActivity().categoryRepository, mActivityTestRule.getActivity().cardRepository);

        // 엄마모드 카테고리 보기 화면에서 두번째 카테고리를 탭하여 선택
        onView(secondCategoryItemView())
                .check(matches(isDisplayed()))
                .perform(click());

        // → ‘새 카드 만들기’ 화면이 표시됨: 상단에 해당 카테고리의 제목과 카테고리 내 [보이기] 상태로 설정되어 있는 카드 수가 표시되고, 중앙에 ‘새 카드 만들기’ 카드가 표시됨
        onView(withId(R.id.category_item_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("놀이")));
        onView(withId(R.id.category_item_count))
                .check(matches(isDisplayed()))
                .check(matches(withText("총 4장")));
        onView(withId(R.id.add_card_view_layout))
                .check(matches(isDisplayed()));
        onView(withId(R.id.add_card_text))
                .check(matches(isDisplayed()))
                .check(matches(withText("새 카드 만들기")));

        // ‘새 카드 만들기’ 카드를 탭하여 선택
        onView(withId(R.id.add_card_view_layout)).perform(click());

        // → ‘사진 찍기’, ‘사진 선택’, ‘동영상 촬영’을 선택할 수 있는 카드가 표시된 화면으로 이동
        onView(withId(R.id.camera_start_text))
                .check(matches(isDisplayed()))
                .check(matches(withText("카드에 사진이나 영상을\n 추가해주세요")));
        onView(withId(R.id.layout_camera))
                .check(matches(isDisplayed()));
        onView(withId(R.id.text_take_picture))
                .check(matches(isDisplayed()))
                .check(matches(withText("사진 찍기")));
        onView(withId(R.id.layout_gallery))
                .check(matches(isDisplayed()));
        onView(withId(R.id.text_select_gallery))
                .check(matches(isDisplayed()))
                .check(matches(withText("사진 선택")));
        onView(withId(R.id.layout_video))
                .check(matches(isDisplayed()));
        onView(withId(R.id.text_take_video))
                .check(matches(isDisplayed()))
                .check(matches(withText("동영상 촬영")));
    }

    @Test
    public void byTakingPicture() throws Exception {

        // ‘사진 찍기’를 탭하여 선택
        onView(withId(R.id.layout_camera))
                .perform(click());

        Thread.sleep(3000); // wait for loading camera

        // → 미리보기 Frame과 촬영 버튼이 있는 화면으로 이동
        onView(withId(R.id.camera_frame))
                .check(matches(isDisplayed()));
        onView(withId(R.id.camera_shutter))
                .check(matches(isDisplayed()));

        // ‘촬영’ 아이콘을 탭하여 사진 촬영
        onView(withId(R.id.camera_shutter))
                .perform(click());

        Thread.sleep(3000); // wait for take a picture

        // → 미리보기 Frame에 보이는 내용 그대로 사진이 촬영되고 ‘다시 촬영’과 ‘확인’ 버튼이 있는 화면으로 이동
        onView(withId(R.id.card_preview_layout))
                .check(matches(isDisplayed()));
        onView(withId(R.id.camera_recode_guide))
                .check(matches(isDisplayed()))
                .check(matches(withText("찍은 사진을 확인해 보세요")));
        onView(withId(R.id.rerecord_button))
                .check(matches(isDisplayed()));
        onView(withId(R.id.confirm_button))
                .check(matches(isDisplayed()));

        // ‘재촬영’ 버튼을 탭하여 사진 다시 촬영
        onView(withId(R.id.rerecord_button))
                .perform(click());

        Thread.sleep(3000); // wait for loading camera

        // “2.a” 화면으로 이동: 미리보기 Frame과 촬영 버튼이 있는 화면
        onView(withId(R.id.camera_frame))
                .check(matches(isDisplayed()));
        onView(withId(R.id.camera_shutter))
                .check(matches(isDisplayed()));

        // “2.b” 실행 후 ‘확인’ 버튼을 탭하여 사진 촬영 완료
        onView(withId(R.id.camera_shutter))
                .perform(click());

        Thread.sleep(3000); // wait for take a picture

        onView(withId(R.id.confirm_button))
                .perform(click());

        // → 카드 제목을 입력할 수 있는 화면으로 이동, 키보드가 올라옴
        onView(withId(R.id.card_image_title_edit))
                .check(matches(isDisplayed()));


    }

    @Test
    public void byBringingPictureFromGallery() throws Exception {

    }

    @Test
    public void byShootingVideo() throws Exception {

    }

    @NonNull
    private Matcher<View> secondCategoryItemView() {
        return Matchers.allOf(withId(R.id.category_item_card),
                TestUtil.childAtPosition(
                        withId(R.id.category_list),
                        1));
    }
}