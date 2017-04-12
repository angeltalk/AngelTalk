package act.sds.samsung.angelman.presentation.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CardTransferModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.presentation.custom.CategorySelectDialog;
import act.sds.samsung.angelman.presentation.custom.FontTextView;
import act.sds.samsung.angelman.presentation.listener.OnDownloadCompleteListener;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.BUS;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.FOOD;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.FRIEND;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.PUZZLE;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.SCHOOL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ShareCardActivityTest extends UITest {

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;

    private ShareCardActivity subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host)));

        subject = setupActivityWithIntent(ShareCardActivity.class, intent);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnDownloadCompleteListener onSuccessListener = ((OnDownloadCompleteListener) invocation.getArguments()[1]);
                CardTransferModel cardTransferModel = new CardTransferModel();
                cardTransferModel.name = "TESTCARD";
                cardTransferModel.cardType = CardModel.CardType.PHOTO_CARD.getValue();
                cardTransferModel.contentPath = "/content";
                String filePath = "/";
                onSuccessListener.onSuccess(cardTransferModel, filePath);
                return null;

            }
        }).when(subject.cardTransfer).downloadCard(any(String.class), any(OnDownloadCompleteListener.class));
        when(categoryRepository.getCategoryAllList()).thenReturn(getCategoryList(6));
    }

    @Test
    public void givenWhenLaunchedAndKaKaoIntentReceived_thenShowLoadingAnimation() throws Exception {
        // then
        LinearLayout loadingViewLayout = (LinearLayout) subject.findViewById(R.id.on_loading_view);
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void whenDownloadCardComplete_thenHideLoadingAnimationAndShowShareCard() throws Exception {
        LinearLayout loadingViewLayout = (LinearLayout) subject.findViewById(R.id.on_loading_view);
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.VISIBLE);

        // when
        subject.downloadCard();

        // then
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.GONE);
        TextView shareCardTextView = (TextView) subject.mViewPager.getChildAt(subject.mViewPager.getCurrentItem()).findViewById(R.id.card_image_title);
        assertThat(shareCardTextView.getText()).isEqualTo("TESTCARD");
    }

    @Test
    public void whenClickDownloadButton_thenShowCategorySelectDialogAndContentsOfDialogCorrectly() throws Exception {
        // when
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();

        // then
        assertThat(dialog.isShowing()).isTrue();

        FontTextView infoText = ((FontTextView) dialog.findViewById(R.id.text_select_category_guide));
        assertThat(infoText.getText().toString()).isEqualTo("저장하실 카테고리를 선택해주세요");

        RecyclerView listView = ((RecyclerView) dialog.findViewById(R.id.category_list_recycler_view));
        assertThat(listView.getChildCount()).isEqualTo(6);
    }

    @Test
    public void givenCategorySelectDialogShowing_whenClickCancelButton_thenDismissDialog() throws Exception {
        // given
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();
        FontTextView cancelView = (FontTextView) dialog.findViewById(R.id.cancel_button);
        assertThat(dialog.isShowing()).isTrue();
        // when
        cancelView.performClick();
        // then
        assertThat(dialog.isShowing()).isFalse();
    }

    @Test
    public void givenCategorySelectDialogShowing_whenClickSaveButton_thenSaveCard() throws Exception {
        // given
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();
        // when
        dialog.findViewById(R.id.confirm_button).performClick();
        // then
        assertThat(dialog.isShowing()).isTrue();
    }

    @Test
    public void givenCategorySelectDialogShowing_whenClickFirstCategory_thenSetConfirmButtonEnabledAndChangeTextColor() throws Exception {
        // given
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();
        FontTextView confirmButton = (FontTextView) dialog.findViewById(R.id.confirm_button);
        assertThat(confirmButton.isEnabled()).isFalse();
        // when
        selectCategoryRadioButtonAt(dialog, 0);
        // then
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.category_list_recycler_view);
        assertThat(confirmButton.isEnabled()).isTrue();
        assertThat(confirmButton.getCurrentTextColor()).isEqualTo(getColor(R.color.simple_background_red));
        assertThat(((CategorySelectDialog.CategoryListAdapter) recyclerView.getAdapter()).getSelectItem()).isNotNull();
    }

    @Test
    public void givenCategorySelectDialogShowingAndClickFirstCategory_whenClickSecondCategory_thenChangeRadioButtonState() throws Exception {
        // given
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();
        AppCompatRadioButton radioButton1 = selectCategoryRadioButtonAt(dialog, 0);
        assertThat(radioButton1.isChecked()).isTrue();
        // when
        AppCompatRadioButton radioButton2 = selectCategoryRadioButtonAt(dialog, 1);
        // then
        assertThat(radioButton1.isChecked()).isFalse();
        assertThat(radioButton2.isChecked()).isTrue();
    }

    @Test
    public void givenCategorySelectDialogShowing_whenClickFirstCategoryAndClickConfirmButton_thenSaveCard() throws Exception {
        // given
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();
        selectCategoryRadioButtonAt(dialog, 0);
        // when
        dialog.findViewById(R.id.confirm_button).performClick();
        // then
        verify(cardRepository).createSingleCardModel(any(CardModel.class));
    }

    @Test
    public void givenCategorySelectDialogShowing_whenClickFirstCategoryAndClickSaveButton_thenSetCategoryModelAndMoveToCardListActivity() throws Exception {
        // given
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();
        // when
        selectCategoryRadioButtonAt(dialog, 0);
        dialog.findViewById(R.id.confirm_button).performClick();

        // then
        verify(subject.applicationManager).setCategoryModel(any(CategoryModel.class));
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CardListActivity.class.getCanonicalName());
    }


    @Test
    public void givenLaunchedAndDownloadCardCompleted_whenClickBackButton_thenMoveToCategoryMenuActivity() throws Exception {
        // given
        subject.downloadCard();
        // when
        subject.findViewById(R.id.back_button).performClick();
        // then
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CategoryMenuActivity.class.getCanonicalName());
    }

    @Test
    public void whenOnBackPressed_thenMoveToCategoryMenuActivity() throws Exception {
        // when
        subject.onBackPressed();
        // then
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CategoryMenuActivity.class.getCanonicalName());
    }

    private AlertDialog clickSaveButtonAndShowSelectCategoryDialog() {
        subject.downloadCard();
        ImageView saveButton = (ImageView) subject.findViewById(R.id.card_save_button);
        saveButton.performClick();
        return (AlertDialog) ShadowAlertDialog.getLatestDialog();
    }

    @NonNull
    private AppCompatRadioButton selectCategoryRadioButtonAt(AlertDialog dialog, int position) {
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.category_list_recycler_view);
        AppCompatRadioButton radioButton = (AppCompatRadioButton) recyclerView.getChildAt(position).findViewById(R.id.category_item_radio);
        radioButton.performClick();
        return radioButton;
    }

    private List<CategoryModel> getCategoryList(int listSize) {
        List<CategoryModel> categoryList = Lists.newArrayList();
        CategoryModel [] categoryModel = new CategoryModel[listSize];

        switch(listSize) {
            case 6: //Full
                categoryModel[5] = new CategoryModel();
                categoryModel[5].title = "엄마";
                categoryModel[5].color = ResourcesUtil.BLUE;
                categoryModel[5].icon = FRIEND.ordinal();
                categoryModel[5].index = 5;
            case 5: //Default
                categoryModel[4] = new CategoryModel();
                categoryModel[4].title = "사람";
                categoryModel[4].color = ResourcesUtil.BLUE;
                categoryModel[4].icon = FRIEND.ordinal();
                categoryModel[4].index = 4;

                categoryModel[3] = new CategoryModel();
                categoryModel[3].title = "가고 싶은 곳";
                categoryModel[3].color = ResourcesUtil.GREEN;
                categoryModel[3].icon = SCHOOL.ordinal();
                categoryModel[3].index = 3;

                categoryModel[2] = new CategoryModel();
                categoryModel[2].title = "탈 것";
                categoryModel[2].color = ResourcesUtil.YELLOW;
                categoryModel[2].icon = BUS.ordinal();
                categoryModel[2].index = 2;

                categoryModel[1] = new CategoryModel();
                categoryModel[1].title = "놀이";
                categoryModel[1].color = ResourcesUtil.ORANGE;
                categoryModel[1].icon = PUZZLE.ordinal();
                categoryModel[1].index = 1;
            case 1: //One
                categoryModel[0] = new CategoryModel();
                categoryModel[0].title = "먹을 것";
                categoryModel[0].color = ResourcesUtil.RED;
                categoryModel[0].icon = FOOD.ordinal();
                categoryModel[0].index = 0;
            default: //None
        }

        for (CategoryModel model : categoryModel) {
            categoryList.add(model);
        }

        return categoryList;
    }
}