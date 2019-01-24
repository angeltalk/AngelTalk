package angeltalk.plus.presentation.activity;

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

import angeltalk.plus.R;

import static angeltalk.plus.presentation.activity.TestUtil.checkIsDisplayed;
import static angeltalk.plus.presentation.activity.TestUtil.checkWithText;
import static angeltalk.plus.presentation.activity.TestUtil.performClick;
import static angeltalk.plus.presentation.activity.TestUtil.withDrawable;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

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
        performClick(secondCategoryItemView());

        // → ‘새 카드 만들기’ 화면이 표시됨: 상단에 해당 카테고리의 제목과 카테고리 내 [보이기] 상태로 설정되어 있는 카드 수가 표시되고, 중앙에 ‘새 카드 만들기’ 카드가 표시됨
        checkWithText(R.id.category_item_title, "놀이");
        checkWithText(R.id.category_item_count, "총 5장");
        checkIsDisplayed(R.id.add_card_view_layout);
        checkWithText(R.id.add_card_text, "새 카드 만들기");

        // ‘새 카드 만들기’ 카드를 탭하여 선택
        performClick(R.id.add_card_view_layout);

        // → ‘사진 찍기’, ‘사진 선택’, ‘동영상 촬영’을 선택할 수 있는 카드가 표시된 화면으로 이동
        checkWithText(R.id.camera_start_text, "카드에 사진이나 영상을\n 추가해주세요");
        checkIsDisplayed(R.id.layout_camera);
        checkWithText(R.id.text_take_picture, "사진 찍기");
        checkIsDisplayed(R.id.layout_gallery);
        checkWithText(R.id.text_select_gallery, "사진 선택");
        checkIsDisplayed(R.id.layout_video);
        checkWithText(R.id.text_take_video, "동영상 촬영");
    }

    @Test
    public void byTakingPicture() throws Exception {

        // ‘사진 찍기’를 탭하여 선택
        performClick(R.id.layout_camera);
        Thread.sleep(3000); // wait for loading camera

        // → 미리보기 Frame과 촬영 버튼이 있는 화면으로 이동
        checkIsDisplayed(R.id.camera_frame);
        checkIsDisplayed(R.id.camera_shutter);

        // ‘촬영’ 아이콘을 탭하여 사진 촬영
        performClick(R.id.camera_shutter);
        Thread.sleep(3000); // wait for take a picture

        // → 미리보기 Frame에 보이는 내용 그대로 사진이 촬영되고 ‘다시 촬영’과 ‘확인’ 버튼이 있는 화면으로 이동
        checkIsDisplayed(R.id.card_preview_layout);
        checkWithText(R.id.camera_recode_guide, "찍은 사진을 확인해 보세요");
        checkIsDisplayed(R.id.rerecord_button);
        checkIsDisplayed(R.id.confirm_button);

        // ‘재촬영’ 버튼을 탭하여 사진 다시 촬영
        performClick(R.id.rerecord_button);
        Thread.sleep(3000); // wait for loading camera

        // “2.a” 화면으로 이동: 미리보기 Frame과 촬영 버튼이 있는 화면
        checkIsDisplayed(R.id.camera_frame);
        checkIsDisplayed(R.id.camera_shutter);

        // “2.b” 실행 후 ‘확인’ 버튼을 탭하여 사진 촬영 완료
        performClick(R.id.camera_shutter);
        Thread.sleep(3000); // wait for take a picture

        performClick(R.id.confirm_button);
        Thread.sleep(1000);

        // → 카드 제목을 입력할 수 있는 화면으로 이동, 키보드가 올라옴
        checkIsDisplayed(R.id.card_image_title_edit);

        // 키보드를 사용하여 카드 제목란에 “새로운 사진” 입력 후 키보드의 ‘완료’ 버튼을 탭하여 제목 입력 완료
        onView(withId(R.id.card_image_title_edit))
                .perform(replaceText("새로운 사진"))
                .perform(pressImeActionButton());
        pressBack();
        closeSoftKeyboard();

        // → 음성 녹음 화면으로 이동: 카드가 중앙에 표시되고 ‘녹음’ 버튼이 카드 하단에 위치된 화면
        checkWithText(R.id.recoding_guide, "카드 이름을 녹음해 주세요");
        checkIsDisplayed(R.id.mic_btn);

        // 녹음 버튼을 탭하여 녹음 시작
        performClick(R.id.mic_btn);

        // → 카운트다운 3->2->1 후 음성 녹음 화면으로 전환됨
        Thread.sleep(4500); // wait for record counting

        // 음성 녹음이 시작되면 “테스트”라고 녹음하고 바로 ‘중지’ 버튼을 탭하여 녹음 완료
        checkWithText(R.id.waiting_count, "지금 말해주세요!");
        performClick(R.id.record_stop_button);

        // → 녹음된 음성 확인 화면으로 전환됨: ‘다시 녹음’, ‘확인’, ‘다시 듣기’ 버튼이 표시되고, 녹음된 음성이 한 번 재생됨
        checkWithText(R.id.waiting_count, "음성을 확인하세요");
        checkIsDisplayed(R.id.rerecord_button);
        checkIsDisplayed(R.id.record_stop_button)
                .check(matches(withDrawable(R.drawable.ic_check_button)));
        checkIsDisplayed(R.id.replay_button);

        // ‘다시 듣기’ 버튼을 탭하여 녹음된 음성 다시 확인
        performClick(R.id.replay_button);

        // → 녹음된 음성이 다시 한 번 재생됨 (재생되는 동안 ‘다시 듣기’ 버튼 비활성화)
//        onView(withId(R.id.replay_button))
//                .check(matches(not(isEnabled())));

        // ‘다시 녹음’ 버튼을 탭하여 음성 다시 녹음할 수 있는 화면으로 이동
        performClick(R.id.rerecord_button);

        // → “2.e” 화면으로 이동
        checkWithText(R.id.recoding_guide, "카드 이름을 녹음해 주세요");

        // “2.f” 실행 후, 음성 녹음이 시작되면 “녹음 테스트”라고 녹음하고 자동으로 녹음 완료될 때까지 기다림
        performClick(R.id.mic_btn);
        Thread.sleep(4500); // wait for record counting
        checkWithText(R.id.waiting_count, "지금 말해주세요!");
        Thread.sleep(3000); // wait for recording

        // → “2.g”의 녹음된 음성 확인 화면으로 자동으로 전환됨
        checkWithText(R.id.waiting_count, "음성을 확인하세요");

        // ‘확인’ 버튼을 탭하여 음성 녹음과 카드 생성 완료
        performClick(R.id.record_stop_button);

        // → 방금 생성한 카드가 해당 카테고리의 제일 처음 순서로 추가됨: 카테고리 제목 밑에 카드 수가 1개 증가하여 표시되고, ‘새로운 카드가 추가되었습니다.’ 메시지가 표시되었다가 사라짐
        checkWithText(R.id.category_item_count, "1 / 6");
        checkWithText(android.support.design.R.id.snackbar_text, "새로운 카드가 추가되었습니다");
    }

    @Test
    public void byBringingPictureFromGallery() throws Exception {

        // ‘사진 선택’을 탭하여 갤러리에서 사진 불러오기 선택
        // → 스마트폰에 설치되어 있는 사진을 관리하는 앱들 중 하나를 선택할 수 있는 창이 표시됨
        // 사진 관리 앱들 중 ‘갤러리’ 아이콘을 탭하여 선택
        // → 갤러리 앱이 실행됨
        // 갤러리 앱에서 두번째로 표시되는 사진을 탭하여 선택
        // → 미리보기 화면으로 전환되고 선택된 사진이 Frame내에 표시됨
        // ‘90도 돌리기’ 버튼을 탭하여 사진 90도 돌리기
        // → 해당 사진이 시계 반대 방향으로 90도 돌아감
        // 해당 사진을 pinch zoom out을 통해 사이즈를 줄임
        // → 사진이 줄어듬
        // ‘확인’ 버튼을 탭하여 사진 선택 완료
        // → “#011”의 “2.e”로 이동하여 이후 과정 동일하게 수행
        // “#011”의 “3. 아이모드에서 새로운 카드 확인” 실행
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