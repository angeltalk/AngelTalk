package act.angelman.presentation.activity;

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

import act.angelman.R;
import act.angelman.TestAngelmanApplication;
import act.angelman.UITest;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.model.CardTransferModel;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.domain.repository.CategoryRepository;
import act.angelman.network.transfer.CardTransfer;
import act.angelman.presentation.custom.CategorySelectDialog;
import act.angelman.presentation.listener.OnDownloadCompleteListener;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.util.ResourcesUtil;

import static act.angelman.presentation.util.ResourceMapper.IconType.BUS;
import static act.angelman.presentation.util.ResourceMapper.IconType.FOOD;
import static act.angelman.presentation.util.ResourceMapper.IconType.FRIEND;
import static act.angelman.presentation.util.ResourceMapper.IconType.PUZZLE;
import static act.angelman.presentation.util.ResourceMapper.IconType.SCHOOL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class ShareCardActivityTest extends UITest {

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;

    @Inject
    CardTransfer cardTransfer;

    private ShareCardActivity kakaotalk_subject;
    private ShareCardActivity message_subject;
    private ShareCardActivity multiDownload_subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(cardTransfer.isConnectedToNetwork()).thenReturn(true);
    }

    @Test
    public void whenLaunched_thenHideCardCountTitleAndListCardButton() throws Exception {
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));
        assertThat(kakaotalk_subject.titleLayout.cardCount.getVisibility()).isEqualTo(View.GONE);
        assertThat(kakaotalk_subject.titleLayout.listCardButton.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void givenWhenLaunchedAndKaKaoIntentReceived_thenShowLoadingAnimation() throws Exception {
        // then
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));
        LinearLayout loadingViewLayout = (LinearLayout) kakaotalk_subject.findViewById(R.id.on_loading_view);
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void givenWhenLaunchedAndSMSIntentReceived_thenShowLoadingAnimation() throws Exception {
        // then
        message_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("app://angeltalk?key=a1234")));
        LinearLayout loadingViewLayout = (LinearLayout) message_subject.findViewById(R.id.on_loading_view);
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(message_subject.getReceiveKeys().get(0)).isEqualTo("a1234");
    }

    @Test
    public void givenLaunchedAndKaKaoIntentReceived_whenNetworkDisconnected_thenShowCardDownloadFailDialog() throws Exception {
        // when
        when(cardTransfer.isConnectedToNetwork()).thenReturn(false);
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

        //then
        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(((TextView) latestAlertDialog.findViewById(R.id.alert_message)).getText()).isEqualTo("카드 불러오기에 실패했습니다.\n대화창으로 다시 돌아갈까요?");
     }

    @Test
    public void givenLaunchedAndKaKaoIntentReceived_whenNetworkDisconnectedAndClickCancelbutton_thenMoveToCategoryMenu() throws Exception {
        // when
        when(cardTransfer.isConnectedToNetwork()).thenReturn(false);
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

        //then
        AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
        latestAlertDialog.findViewById(R.id.cancel_button).performClick();
        ShadowActivity shadowActivity = shadowOf(kakaotalk_subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(CategoryMenuActivity.class.getCanonicalName());
    }

    @Test
    public void whenDownloadCardComplete_thenHideLoadingAnimationAndShowShareCard() throws Exception {
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));
        LinearLayout loadingViewLayout = (LinearLayout) kakaotalk_subject.findViewById(R.id.on_loading_view);
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.VISIBLE);

        // when
        kakaotalk_subject.downloadCard();

        // then
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.GONE);
        TextView shareCardTextView = (TextView) kakaotalk_subject.mViewPager.getChildAt(kakaotalk_subject.mViewPager.getCurrentItem()).findViewById(R.id.card_image_title);
        assertThat(shareCardTextView.getText()).isEqualTo("TESTCARD");
    }

    @Test
    public void whenClickDownloadButton_thenShowCategorySelectDialogAndContentsOfDialogCorrectly() throws Exception {
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

        // when
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();

        // then
        assertThat(dialog.isShowing()).isTrue();

        TextView infoText = ((TextView) dialog.findViewById(R.id.text_select_category_guide));
        assertThat(infoText.getText().toString()).isEqualTo("저장하실 카테고리를 선택해 주세요");

        RecyclerView listView = ((RecyclerView) dialog.findViewById(R.id.category_list_recycler_view));
        assertThat(listView.getAdapter().getItemCount()).isEqualTo(6);
    }

    @Test
    @Config(qualifiers = "en")
    public void whenClickDownloadButton_thenShowCategorySelectDialogAndContentsOfDialogCorrectly_en() throws Exception {
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

        // when
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();

        // then
        assertThat(dialog.isShowing()).isTrue();

        TextView infoText = ((TextView) dialog.findViewById(R.id.text_select_category_guide));
        assertThat(infoText.getText().toString()).isEqualTo("Choose a category for this card");

        RecyclerView listView = ((RecyclerView) dialog.findViewById(R.id.category_list_recycler_view));
        assertThat(listView.getAdapter().getItemCount()).isEqualTo(6);
    }

    @Test
    public void givenCategorySelectDialogShowing_whenClickCancelButton_thenDismissDialog() throws Exception {
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

        // given
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();
        TextView cancelView = (TextView) dialog.findViewById(R.id.cancel_button);
        assertThat(dialog.isShowing()).isTrue();
        // when
        cancelView.performClick();
        // then
        assertThat(dialog.isShowing()).isFalse();
    }

    @Test
    public void givenCategorySelectDialogShowing_whenClickSaveButton_thenSaveCard() throws Exception {
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

        // given
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();
        // when
        dialog.findViewById(R.id.confirm_button).performClick();
        // then
        assertThat(dialog.isShowing()).isTrue();
    }

    @Test
    public void givenCategorySelectDialogShowing_whenClickFirstCategory_thenSetConfirmButtonEnabledAndChangeTextColor() throws Exception {
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

        // given
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();
        TextView confirmButton = (TextView) dialog.findViewById(R.id.confirm_button);
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
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

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
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

        // given
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();
        selectCategoryRadioButtonAt(dialog, 0);
        // when
        dialog.findViewById(R.id.confirm_button).performClick();
        // then
        verify(cardRepository).createSingleCardModel(any(CardModel.class));
    }

    @Test
    public void givenCategorySelectDialogShowing_whenClickFirstCategoryAndClickSaveButton_thenSetCategoryModelAndCurrentCardIndexAndMoveToCardListActivity() throws Exception {
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

        // given
        AlertDialog dialog = clickSaveButtonAndShowSelectCategoryDialog();
        // when
        selectCategoryRadioButtonAt(dialog, 0);
        dialog.findViewById(R.id.confirm_button).performClick();

        // then
        verify(kakaotalk_subject.applicationManager).setCategoryModel(any(CategoryModel.class));
        verify(kakaotalk_subject.applicationManager).setCurrentCardIndex(anyInt());
        ShadowActivity shadowActivity = shadowOf(kakaotalk_subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CardListActivity.class.getCanonicalName());
    }


    @Test
    public void givenLaunchedAndDownloadCardCompleted_whenClickBackButton_thenMoveToCategoryMenuActivity() throws Exception {
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

        // given
        kakaotalk_subject.downloadCard();
        // when
        kakaotalk_subject.findViewById(R.id.back_button).performClick();
        // then
        ShadowActivity shadowActivity = shadowOf(kakaotalk_subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CategoryMenuActivity.class.getCanonicalName());
    }

    @Test
    public void whenOnBackPressed_thenMoveToCategoryMenuActivity() throws Exception {
        kakaotalk_subject = setupActivityWithIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kakao_scheme) + "://" + getString(R.string.kakaolink_host))));

        // when
        kakaotalk_subject.onBackPressed();
        // then
        ShadowActivity shadowActivity = shadowOf(kakaotalk_subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CategoryMenuActivity.class.getCanonicalName());
    }

    @Test
    public void whenLaunchedWithMultiReceiveKey_thenLoadingMessageShowCorrectly() throws Exception {
        multiDownload_subject = setupActivityWithMultiDownload();
        assertThat(multiDownload_subject.loadingViewText.getText()).contains("/3)");
        verify(cardTransfer, times(3)).downloadCard(any(String.class), any(OnDownloadCompleteListener.class));
    }

    @Test
    public void givenMultiReceiveKey_whenClickSaveButton3times_thenMoveToCardListActivity() throws Exception {
        // given
        multiDownload_subject = setupActivityWithMultiDownload();
        multiDownload_subject.downloadCard();

        // when
        AlertDialog dialog;
        ImageView saveButton;

        saveButton = (ImageView) multiDownload_subject.findViewById(R.id.card_save_button);
        saveButton.performClick();
        dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        selectCategoryRadioButtonAt(dialog, 0);
        dialog.findViewById(R.id.confirm_button).performClick();
        assertThat(multiDownload_subject.getShareCardModelList().size()).isEqualTo(2);

        saveButton = (ImageView) multiDownload_subject.findViewById(R.id.card_save_button);
        saveButton.performClick();
        dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        selectCategoryRadioButtonAt(dialog, 0);
        dialog.findViewById(R.id.confirm_button).performClick();
        assertThat(multiDownload_subject.getShareCardModelList().size()).isEqualTo(1);

        saveButton = (ImageView) multiDownload_subject.findViewById(R.id.card_save_button);
        saveButton.performClick();
        dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        selectCategoryRadioButtonAt(dialog, 0);
        dialog.findViewById(R.id.confirm_button).performClick();

        // then
        ShadowActivity shadowActivity = shadowOf(multiDownload_subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CardListActivity.class.getCanonicalName());
    }

    private AlertDialog clickSaveButtonAndShowSelectCategoryDialog() {
        kakaotalk_subject.downloadCard();
        ImageView saveButton = (ImageView) kakaotalk_subject.findViewById(R.id.card_save_button);
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

    private ShareCardActivity setupActivityWithIntent(Intent intent) {
        ShareCardActivity subject = setupActivityWithIntentAndPostCreate(ShareCardActivity.class, intent);
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

        return subject;
    }

    private ShareCardActivity setupActivityWithMultiDownload() {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.INTENT_KEY_MULTI_DOWNLOAD, true);
        intent.putStringArrayListExtra(ApplicationConstants.INTENT_KEY_MULTI_DOWNLOAD_DATA, Lists.newArrayList("a", "b", "c"));

        ShareCardActivity subject = setupActivityWithIntent(ShareCardActivity.class, intent);
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

        return subject;
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