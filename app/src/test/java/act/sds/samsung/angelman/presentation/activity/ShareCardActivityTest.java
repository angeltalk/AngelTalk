package act.sds.samsung.angelman.presentation.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import org.robolectric.shadows.ShadowIntent;

import java.util.ArrayList;
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

    ShareCardActivity subject;


    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;


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
    }

    @Test
    public void whenKaKaoIntentReceived_thenShowLoadingAnimation() throws Exception {
        LinearLayout loadingViewLayout = (LinearLayout) subject.findViewById(R.id.on_loading_view);
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void whenDownloadCardComplete_thenHideLoadingAnimationAndShowShareCard() throws Exception {
        LinearLayout loadingViewLayout = (LinearLayout) subject.findViewById(R.id.on_loading_view);
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.VISIBLE);
        subject.downloadCard();
        assertThat(loadingViewLayout.getVisibility()).isEqualTo(View.GONE);
        TextView shareCardTextView = (TextView) subject.mViewPager.getChildAt(subject.mViewPager.getCurrentItem()).findViewById(R.id.card_image_title);
        assertThat(shareCardTextView.getText()).isEqualTo("TESTCARD");
    }

    @Test
    public void whenClickDownloadButton_thenShowShareConfirmDialog() throws Exception {
        subject.downloadCard();
        ImageView saveButton = (ImageView) subject.findViewById(R.id.card_save_button);
        saveButton.performClick();
        AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        assertThat(dialog.isShowing()).isTrue();
    }

    @Test
    public void givenShareConfirmDialogShowing_whenClickCancelButton_thenDismissDialog() throws Exception {
        subject.downloadCard();
        ImageView saveButton = (ImageView) subject.findViewById(R.id.card_save_button);
        saveButton.performClick();
        AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        FontTextView cancelView = (FontTextView) dialog.findViewById(R.id.cancel);
        assertThat(dialog.isShowing()).isTrue();
        cancelView.performClick();
        assertThat(dialog.isShowing()).isFalse();
    }

    @Test
    public void givenShareConfirmDialogShowing_whenClickSaveButton_thenSaveCard() throws Exception {
        when(categoryRepository.getCategoryAllList()).thenReturn(getCategoryList(6));
        subject.downloadCard();
        ImageView saveButton = (ImageView) subject.findViewById(R.id.card_save_button);
        saveButton.performClick();
        AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        FontTextView confirmView= (FontTextView) dialog.findViewById(R.id.confirm);
        confirmView.performClick();
        verify(cardRepository).createSingleCardModel(any(CardModel.class));

    }

    @Test
    public void givenShareConfirmDialogShowing_whenClickSaveButton_thenMoveToViewCardPage() throws Exception {
        when(categoryRepository.getCategoryAllList()).thenReturn(getCategoryList(6));

        subject.downloadCard();
        ImageView saveButton = (ImageView) subject.findViewById(R.id.card_save_button);
        saveButton.performClick();
        AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        FontTextView confirmView= (FontTextView) dialog.findViewById(R.id.confirm);
        confirmView.performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertThat(shadowIntent.getIntentClass()).isEqualTo(CardViewPagerActivity.class);
    }

    private List<CategoryModel> getCategoryList(int listSize) {
        List<CategoryModel> categoryList = new ArrayList<>();
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