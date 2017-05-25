package act.angelman.presentation.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboVibrator;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowBitmap;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import act.angelman.BuildConfig;
import act.angelman.R;
import act.angelman.TestAngelmanApplication;
import act.angelman.UITest;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.network.transfer.CardTransfer;
import act.angelman.network.transfer.MessageTransfer;
import act.angelman.presentation.adapter.CardImageAdapter;
import act.angelman.presentation.custom.AddCardView;
import act.angelman.presentation.custom.CardView;
import act.angelman.presentation.custom.CardViewPager;
import act.angelman.presentation.custom.VideoCardTextureView;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.manager.ApplicationManager;
import act.angelman.presentation.shadow.ShadowSnackbar;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.PlayUtil;
import act.angelman.presentation.util.ResourcesUtil;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowSnackbar.class})
public class CardViewPagerActivityTest extends UITest {

    @Inject
    CardRepository repository;

    @Inject
    CardTransfer cardTransfer;

    @Inject
    MessageTransfer messageTransfer;

    @Inject
    ApplicationManager applicationManager;

    private CardViewPagerActivity subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(cardTransfer.isConnectedToNetwork()).thenReturn(true);
        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        when(applicationManager.getCategoryModel()).thenReturn(getCategoryModel());
        when(applicationManager.getCategoryModelColor()).thenReturn(getCategoryModelColor());
        subject = setupActivity(CardViewPagerActivity.class);
        subject.mViewPager.setCurrentItem(1);
    }

    @Test
    public void whenLaunchedApp_thenSetBackgroundColorChangedToRelatedInCategory() throws Exception {
        assertThat(applicationManager.getCategoryModelColor()).isEqualTo(R.drawable.background_gradient_red);
    }

    @Test
    public void whenLaunchedCardViewPagerView_thenShowsCategoryNameOnTitleCorrectly() throws Exception {
        TextView title = (TextView) subject.findViewById(R.id.category_item_title);
        assertThat(title.getText()).isEqualTo("먹을 것");
    }

    @Test
    public void whenLaunchedCardViewPager_thenShowsAddNewCardPageOnViewPager() throws Exception {
        subject.mViewPager.setCurrentItem(0);

        assertThat(subject.buttonContainer).isGone();

        assertThat(subject.cardTitleLayout.listCardButton).isVisible();
        assertThat(((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(0)).isInstanceOf(AddCardView.class);
    }

    @Test
    public void givenLaunched_whenMoveToPhotoCard_thenShowTitleCorrectly() throws Exception {
        subject.mViewPager.setCurrentItem(1);

        View view = ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1);
        assertThat(view.findViewById(R.id.card_video)).isGone();
        assertThat(view.findViewById(R.id.card_image)).isVisible();
        assertThat(subject.buttonContainer).isVisible();
    }

    @Test
    public void givenLaunched_whenMoveToVideoCard_thenShowTitleCorrectly() throws Exception {
        subject.mViewPager.setCurrentItem(4);

        View view = ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(4);
        assertThat(view.findViewById(R.id.card_video)).isVisible();
        assertThat(view.findViewById(R.id.card_image)).isVisible();
        assertThat(view.findViewById(R.id.play_button)).isNotVisible();
        assertThat(subject.buttonContainer).isVisible();
    }

    @Test
    public void whenLaunchedCardViewPagerActivity_thenShowsButtonContainer() throws Exception {
        assertThat(subject.buttonContainer).isVisible();
    }

    @Test
    public void whenClickedDeleteButton_thenShowConfirmDialogCorrectly() throws Exception {
        // when
        subject.mViewPager.setCurrentItem(1);
        assertThat(((CardView) ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1)).cardTitle.getText()).isEqualTo("물");
        subject.cardDeleteButton.performClick();
        // then
        ShadowAlertDialog shadowDialog = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        assertThat(shadowDialog).isNotNull();
        assertThat(((TextView) shadowDialog.getView().findViewById(R.id.alert_message)).getText()).contains( "물" );
        assertThat(((TextView) shadowDialog.getView().findViewById(R.id.alert_message)).getText()).contains("카드를 삭제하시겠습니까?");
    }

    @Test
    public void whenClickedEditButton_thenShowsEditSelectDialog() throws Exception {
        subject.cardEditButton.performClick();
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);
        assertThat(((TextView) shadowDialog.getView().findViewById(R.id.card_edit_select_guide)).getText()).contains( "카드를 수정해 보세요" );
    }

    @Test
    public void givenCardEditSelectDialogShow_whenClickEditContent_thenMoveToCameraGallerySelectionActivity() throws Exception {
        // given
        subject.mViewPager.setCurrentItem(1);
        subject.cardEditButton.performClick();
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);

        // when
        shadowDialog.getView().findViewById(R.id.card_edit_content_text).performClick();

        // then
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getStringExtra(ApplicationConstants.EDIT_CARD_ID)).isEqualTo("1");
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CameraGallerySelectionActivity.class.getCanonicalName());
    }

    @Test
    public void givenClickedDeleteButton_whenClickedConfirmButton_thenDeleteSelectedCardViewInViewPager() throws Exception {
        CardViewPager viewPager = subject.mViewPager;
        assertThat(viewPager.getAdapter().getCount()).isEqualTo(5);

        subject.mViewPager.setCurrentItem(2);
        assertThat(((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(2)).cardTitle.getText()).isEqualTo("우유");
        assertThat(subject.cardDeleteButton.getVisibility()).isEqualTo(View.VISIBLE);

        when(repository.deleteSingleCardWithCardIndex(anyInt(), anyInt())).thenReturn(true);
        final List<CardModel> cardListWithCategoryId = getCardListWithCategoryId();
        cardListWithCategoryId.remove(2);
        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(cardListWithCategoryId);

        subject.cardDeleteButton.performClick();
        ShadowAlertDialog shadowDialog = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        shadowDialog.getView().findViewById(R.id.confirm_button).performClick();

        assertThat(viewPager.getAdapter().getCount()).isEqualTo(4);
        assertThat(((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(2)).cardTitle.getText()).isEqualTo(cardListWithCategoryId.get(2).name);
    }

    @Test
    public void givenClickedDeleteButton_whenClickedCancelButton_thenDismissDialog() throws Exception {
        subject.mViewPager.setCurrentItem(2);
        subject.cardDeleteButton.performClick();

        ShadowAlertDialog shadowDialog = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        shadowDialog.getView().findViewById(R.id.cancel_button).performClick();

        assertThat(shadowDialog.hasBeenDismissed()).isTrue();
    }

    @Test
    public void whenCardViewPagerActivityLaunched_thenShowCardsRelatedInCategory() throws Exception {
        CardViewPager viewPager = subject.mViewPager;

        assertThat(viewPager.getAdapter()).isNotNull();
        assertThat(viewPager.getAdapter().getCount()).isNotEqualTo(0);

        viewPager.invalidate();
        viewPager.requestLayout();

        CardView cardView = (CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(1);
        ShadowBitmap loadedImage = shadowOf(((BitmapDrawable) cardView.cardImage.getDrawable()).getBitmap());
        assertThat(cardView.cardTitle.getText()).isEqualTo("물");
        assertThat(loadedImage.getDescription()).contains("contents"+File.separator+"water.jpg");
    }


    @Test
    public void givenSetTextToTitleView_whenClickCardView_thenPlayBackTitle() throws Exception {
        CardViewPager viewPager = subject.mViewPager;

        assertThat(viewPager.getAdapter()).isNotNull();
        assertThat(viewPager.getAdapter().getCount()).isNotEqualTo(0);

        viewPager.invalidate();
        viewPager.requestLayout();

        PlayUtil ttsMock = mock(PlayUtil.class);

        Field declaredField = CardImageAdapter.class.getDeclaredField("playUtil");
        declaredField.setAccessible(true);
        CardImageAdapter cardImageAdapter = (CardImageAdapter) subject.mViewPager.getAdapter();
        declaredField.set(cardImageAdapter, ttsMock);

        doNothing().when(ttsMock).ttsSpeak("물");
        (((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1)).findViewById(R.id.card_container).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(ttsMock).ttsSpeak("물");

        declaredField.setAccessible(false);
    }

    @Test
    public void givenSetTextToTitleView_whenLongClickCardView_thenPlayBackTitle() throws Exception {
        CardViewPager viewPager = subject.mViewPager;

        viewPager.invalidate();
        viewPager.requestLayout();

        PlayUtil ttsMock = mock(PlayUtil.class);

        Field declaredField = CardImageAdapter.class.getDeclaredField("playUtil");
        declaredField.setAccessible(true);
        CardImageAdapter cardImageAdapter = (CardImageAdapter) subject.mViewPager.getAdapter();
        declaredField.set(cardImageAdapter, ttsMock);

        doNothing().when(ttsMock).ttsSpeak("물");
        (((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1)).findViewById(R.id.card_container).performLongClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        verify(ttsMock).ttsSpeak("물");

        declaredField.setAccessible(false);
    }

    @Test
    public void givenShownSingleCardOnScreen_whenClickCardView_thenVibrate500ms() throws Exception {
        CardViewPager viewPager = subject.mViewPager;

        assertThat(viewPager.getAdapter()).isNotNull();
        assertThat(viewPager.getAdapter().getCount()).isNotEqualTo(0);

        viewPager.invalidate();
        viewPager.requestLayout();
        @SuppressLint("ServiceCast")
        RoboVibrator vibrator = (RoboVibrator) RuntimeEnvironment.application.getSystemService(Context.VIBRATOR_SERVICE);
        (((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1)).findViewById(R.id.card_container).performClick();

        assertThat(vibrator.isVibrating()).isTrue();
        assertThat(vibrator.getMilliseconds()).isEqualTo(500);

        (((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1)).findViewById(R.id.card_container).performLongClick();

        assertThat(vibrator.isVibrating()).isTrue();
        assertThat(vibrator.getMilliseconds()).isEqualTo(500);
    }

    @Test
    public void givenShownVideoCardOnScreen_whenClickCardView_thenPlayVideo() throws Exception {
        subject.mViewPager.setCurrentItem(4);

        View cardView = ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(4);
        cardView.findViewById(R.id.card_container).performClick();

        Robolectric.getForegroundThreadScheduler().advanceBy(1000, TimeUnit.MILLISECONDS);

        assertThat(((VideoCardTextureView) cardView.findViewById(R.id.card_video))).isVisible();
    }

    @Test
    public void whenClickedListButton_thenMovesToCardListActivity() throws Exception {
        subject.cardTitleLayout.listCardButton.performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CardListActivity.class.getCanonicalName());
    }


    @Test
    public void whenSetCardDataCompleted_firstCardIsAddCardView() throws Exception {
        assertThat(subject.mViewPager.getChildAt(0)).isInstanceOf(AddCardView.class);
        assertThat(subject.mViewPager.getChildAt(0)).isNotInstanceOf(CardView.class);
        assertThat(subject.mViewPager.getChildAt(1)).isInstanceOf(CardView.class);
    }

    @Test
    public void giveClickedCategoryInMainMenuActivity_whenLoadActivity_ShowCategoryTitleAndCardCount() throws Exception {
        assertThat(subject.allCardListInSelectedCategory.size()).isEqualTo(5);
    }

    @Test
    public void givenWithIntentData_whenLaunchedActivity_thenShowsSelectedCategoryName(){
        String itemTitle = ((TextView) subject.findViewById(R.id.category_item_title)).getText().toString();
        assertThat(itemTitle).isEqualTo("먹을 것");
    }

    @Test
    public void whenClickBackButton_thenMoveToCategoryMenuActivity() throws Exception {
        // when
        CardImageAdapter mockImageAdapter = mock(CardImageAdapter.class);
        subject.mViewPager.setAdapter(mockImageAdapter);
        subject.cardImageAdapter = mockImageAdapter;
        subject.findViewById(R.id.back_button).performClick();

        // then
        verify(((CardImageAdapter) subject.mViewPager.getAdapter())).releaseSpeakHandler();
        verify(((CardImageAdapter) subject.mViewPager.getAdapter())).stopVideoView();
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CategoryMenuActivity.class.getCanonicalName());
    }

    @Test
    public void whenOnBackPressed_thenMoveToCategoryMenuActivity() throws Exception {
        // when
        CardImageAdapter mockImageAdapter = mock(CardImageAdapter.class);
        subject.mViewPager.setAdapter(mockImageAdapter);
        subject.cardImageAdapter = mockImageAdapter;

        subject.onBackPressed();

        // then
        verify(((CardImageAdapter) subject.mViewPager.getAdapter())).releaseSpeakHandler();
        verify(((CardImageAdapter) subject.mViewPager.getAdapter())).stopVideoView();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CategoryMenuActivity.class.getCanonicalName());
    }

    @Test
    public void givenPlayingCard_whenOnBackPressed_thenStopPlayCard() throws Exception {
        PlayUtil playUtilMock = mock(PlayUtil.class);
        Field declaredField = CardImageAdapter.class.getDeclaredField("playUtil");
        declaredField.setAccessible(true);
        declaredField.set(subject.cardImageAdapter, playUtilMock);

        subject.mViewPager.setCurrentItem(1);

        View cardView = ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1);
        cardView.findViewById(R.id.card_container).performClick();

        subject.onBackPressed();

        verify(playUtilMock).playStop();
        verify(playUtilMock).ttsStop();
    }

    @Test
    public void givenPlayingCard_whenClickListCardButton_thenStopPlayCard() throws Exception {
        PlayUtil playUtilMock = mock(PlayUtil.class);
        Field declaredField = CardImageAdapter.class.getDeclaredField("playUtil");
        declaredField.setAccessible(true);
        declaredField.set(subject.cardImageAdapter, playUtilMock);

        subject.mViewPager.setCurrentItem(1);

        View cardView = ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1);
        cardView.findViewById(R.id.card_container).performClick();

        subject.listCardButton.performClick();

        verify(playUtilMock).playStop();
        verify(playUtilMock).ttsStop();
    }

    @Test
    public void givenPlayingCard_whenClickShareCardButton_thenStopPlayCard() throws Exception {
        PlayUtil playUtilMock = mock(PlayUtil.class);
        Field declaredField = CardImageAdapter.class.getDeclaredField("playUtil");
        declaredField.setAccessible(true);
        declaredField.set(subject.cardImageAdapter, playUtilMock);

        subject.mViewPager.setCurrentItem(1);

        View cardView = ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1);
        cardView.findViewById(R.id.card_container).performClick();

        subject.cardShareButton.performClick();

        verify(playUtilMock).playStop();
        verify(playUtilMock).ttsStop();
    }

    @Test
    public void givenPlayingCard_whenClickDeleteCardButton_thenStopPlayCard() throws Exception {
        PlayUtil playUtilMock = mock(PlayUtil.class);
        Field declaredField = CardImageAdapter.class.getDeclaredField("playUtil");
        declaredField.setAccessible(true);
        declaredField.set(subject.cardImageAdapter, playUtilMock);

        subject.mViewPager.setCurrentItem(1);

        View cardView = ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1);
        cardView.findViewById(R.id.card_container).performClick();

        subject.cardDeleteButton.performClick();

        verify(playUtilMock).playStop();
        verify(playUtilMock).ttsStop();
    }

    @Test
    public void whenLaunchActivity_thenShowWaterCardWithImage() throws Exception {

        RequestManager rm = Glide.with(subject.getApplicationContext());
        CardViewPager viewPager = (CardViewPager) subject.findViewById(R.id.view_pager);

        assertThat(viewPager.getAdapter()).isNotNull();
        assertThat(viewPager.getAdapter().getCount()).isNotEqualTo(0);


        CardView cardView = (CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(2);
        ShadowBitmap loadedImage = shadowOf(((BitmapDrawable) cardView.cardImage.getDrawable()).getBitmap());
        assertThat(cardView.cardTitle.getText()).isEqualTo("우유");
        assertThat(loadedImage.getDescription()).contains("contents"+File.separator+"milk.png");

        assertThat(((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(1)).cardTitle.getText()).isEqualTo("물");
    }

    @Test
    public void whenFinishedToMakeNewCard_thenShowsNewAddedCardAtFirstCardInViewPager() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.INTENT_KEY_NEW_CARD, true);

        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        subject = setupActivityWithIntent(CardViewPagerActivity.class, intent);

        assertThat(subject.mViewPager.getCurrentItem()).isNotEqualTo(0);
        assertThat(subject.mViewPager.getCurrentItem()).isEqualTo(1);
    }

    @Test
    public void whenStartWithRefreshCardIntent_thenShowsAddCardItemInCardViewPager() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.INTENT_KEY_REFRESH_CARD, true);

        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        subject = setupActivityWithIntent(CardViewPagerActivity.class, intent);

        assertThat(subject.mViewPager.getCurrentItem()).isNotEqualTo(1);
        assertThat(subject.mViewPager.getCurrentItem()).isEqualTo(0);
    }

    @Test
    public void whenFinishedCardListActivityAndCurrentCardIsShow_thenShowTheCard() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.INTENT_KEY_LIST_BACK, true);

        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        when(applicationManager.getCurrentCardIndex()).thenReturn(2);

        subject = setupActivityWithIntent(CardViewPagerActivity.class, intent);

        assertThat(subject.mViewPager.getCurrentItem()).isNotEqualTo(1);
        assertThat(subject.mViewPager.getCurrentItem()).isEqualTo(3);
    }

    @Test
    public void whenFinishedCardListActivityAndLastCardIsShow_thenShowLastCard() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.INTENT_KEY_LIST_BACK, true);

        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        when(applicationManager.getCurrentCardIndex()).thenReturn(0);

        subject = setupActivityWithIntent(CardViewPagerActivity.class, intent);

        assertThat(subject.mViewPager.getCurrentItem()).isEqualTo(1);
    }

    @Test
    public void whenFinishedCardListActivityAndCurrentCardIsHide_thenShowFirstCardInViewPager() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.INTENT_KEY_LIST_BACK, true);

        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        when(applicationManager.getCurrentCardIndex()).thenReturn(8);
        subject = setupActivityWithIntent(CardViewPagerActivity.class, intent);

        assertThat(subject.mViewPager.getCurrentItem()).isEqualTo(1);
    }

    @Test
    public void whenCardEditSuccess_thenShowTheCard() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.INTENT_KEY_CARD_EDITED, true);

        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        when(applicationManager.getCurrentCardIndex()).thenReturn(1);
        subject = setupActivityWithIntent(CardViewPagerActivity.class, intent);

        assertThat(subject.mViewPager.getCurrentItem()).isEqualTo(2);
    }

    @Test
    public void whenCardEditSuccess_thenShowSnackBarMessage() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.INTENT_KEY_CARD_EDITED, true);

        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        subject = setupActivityWithIntent(CardViewPagerActivity.class, intent);

        assertThat(ShadowSnackbar.getLatestSnackbar()).isNotNull();
        assertThat(ShadowSnackbar.getTextOfLatestSnackbar()).isEqualTo("카드가 수정되었습니다");
    }

    @Test
    public void whenShareButtonClick_thenShowAvailableMessengerList() throws Exception{
        subject.cardShareButton.performClick();
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);
        assertThat(shadowDialog).isNotNull();
    }

    @Test
    public void givenKakaotalkNotInstall_whenShareButtonClick_thenShowAvailableMessengerListWithoutKakaotalk() throws Exception{

        subject.cardShareButton.performClick();
        subject.pm = mock(PackageManager.class);
        when(subject.pm.getPackageInfo("com.kakao.talk",PackageManager.GET_ACTIVITIES)).thenThrow(new PackageManager.NameNotFoundException());

        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);
        assertThat(shadowDialog).isNotNull();
        assertThat(shadowDialog.getView().findViewById(R.id.item_kakaotalk).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void givenWhatsappNotInstall_whenShareButtonClick_thenShowAvailableMessengerListWithoutKakaotalk() throws Exception{

        subject.cardShareButton.performClick();
        subject.pm = mock(PackageManager.class);
        when(subject.pm.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES)).thenThrow(new PackageManager.NameNotFoundException());

        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);
        assertThat(shadowDialog).isNotNull();
        assertThat(shadowDialog.getView().findViewById(R.id.item_whatsapp).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void whenMessengerItemClicked_thenConfirmButtonEnableAndTheOtherItemUnchecked() throws Exception{
        subject.cardShareButton.performClick();
        subject.pm = mock(PackageManager.class);
        when(subject.pm.getPackageInfo("com.kakao.talk",PackageManager.GET_ACTIVITIES)).thenReturn(new PackageInfo());

        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);

        View innerView = shadowDialog.getView();
        innerView.findViewById(R.id.item_kakaotalk).performClick();
        assertThat(innerView.findViewById(R.id.confirm_button).isEnabled()).isTrue();
        assertThat(((AppCompatRadioButton) innerView.findViewById(R.id.radio_kakaotalk)).isChecked()).isTrue();
        assertThat(((AppCompatRadioButton) innerView.findViewById(R.id.radio_message)).isChecked()).isFalse();
        assertThat(((AppCompatRadioButton) innerView.findViewById(R.id.radio_whatsapp)).isChecked()).isFalse();


        innerView.findViewById(R.id.item_message).performClick();
        assertThat(((AppCompatRadioButton) innerView.findViewById(R.id.radio_whatsapp)).isChecked()).isFalse();
        assertThat(((AppCompatRadioButton) innerView.findViewById(R.id.radio_kakaotalk)).isChecked()).isFalse();
        assertThat(((AppCompatRadioButton) innerView.findViewById(R.id.radio_message)).isChecked()).isTrue();

        innerView.findViewById(R.id.item_whatsapp).performClick();
        assertThat(((AppCompatRadioButton) innerView.findViewById(R.id.radio_whatsapp)).isChecked()).isTrue();
        assertThat(((AppCompatRadioButton) innerView.findViewById(R.id.radio_kakaotalk)).isChecked()).isFalse();
        assertThat(((AppCompatRadioButton) innerView.findViewById(R.id.radio_message)).isChecked()).isFalse();
    }

    @Test
    public void whenClickShareButtonAndSelectKakaotalkAndUploadSuccess_thenSendKakaoLinkMessage() throws Exception {
        subject.mViewPager.setCurrentItem(1);
        CardModel cardModel = subject.getCardModel(1);

        final Map<String, String> resultMap = new HashMap<>();
        resultMap.put("url", "url string");
        resultMap.put("key", "key string");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                OnSuccessListener<Map<String,String>> onSuccessListener = ((OnSuccessListener<Map<String, String>>) invocation.getArguments()[1]);
                onSuccessListener.onSuccess(resultMap);
                return null;
            }
        }).when(subject.cardTransfer).uploadCard(any(CardModel.class), any(OnSuccessListener.class), any(OnFailureListener.class));

        // when
        subject.cardShareButton.performClick();
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);
        View innerView = shadowDialog.getView();
        innerView.findViewById(R.id.item_kakaotalk).performClick();
        innerView.findViewById(R.id.confirm_button).performClick();

        // then
        verify(subject.kaKaoTransfer).sendKakaoLinkMessage(subject.context, "key string", "url string", cardModel);
    }

    @Test
    public void whenClickShareButtonAndSelectWhatsappAndUploadSuccess_thenSendWhatsappMessage() throws Exception {
        subject.mViewPager.setCurrentItem(1);

        final Map<String, String> resultMap = new HashMap<>();
        resultMap.put("url", "url string");
        resultMap.put("key", "key string");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                OnSuccessListener<Map<String,String>> onSuccessListener = ((OnSuccessListener<Map<String, String>>) invocation.getArguments()[1]);
                onSuccessListener.onSuccess(resultMap);
                return null;
            }
        }).when(subject.cardTransfer).uploadCard(any(CardModel.class), any(OnSuccessListener.class), any(OnFailureListener.class));

        // when
        subject.cardShareButton.performClick();
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);
        View innerView = shadowDialog.getView();
        innerView.findViewById(R.id.item_whatsapp).performClick();
        innerView.findViewById(R.id.confirm_button).performClick();

        // then
        verify(subject.messageTransfer).sendMessage(eq(ApplicationConstants.SHARE_MESSENGER_TYPE.WHATSAPP), eq(resultMap.get("key")), any(CardModel.class), any(MessageTransfer.OnCompleteListener.class));
    }

    @Test
    public void whenClickShareButtonAndSelectMessengerAndUploadSuccess_thenSendMessengerMessage() throws Exception {
        subject.mViewPager.setCurrentItem(1);

        final Map<String, String> resultMap = new HashMap<>();
        resultMap.put("url", "url string");
        resultMap.put("key", "key string");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                OnSuccessListener<Map<String,String>> onSuccessListener = ((OnSuccessListener<Map<String, String>>) invocation.getArguments()[1]);
                onSuccessListener.onSuccess(resultMap);
                return null;
            }
        }).when(subject.cardTransfer).uploadCard(any(CardModel.class), any(OnSuccessListener.class), any(OnFailureListener.class));

        // when
        subject.cardShareButton.performClick();
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);
        View innerView = shadowDialog.getView();
        innerView.findViewById(R.id.item_message).performClick();
        innerView.findViewById(R.id.confirm_button).performClick();

        // then
        verify(subject.messageTransfer).sendMessage(eq(ApplicationConstants.SHARE_MESSENGER_TYPE.MESSAGE), eq(resultMap.get("key")), any(CardModel.class), any(MessageTransfer.OnCompleteListener.class));
    }

    @Test
    public void whenClickShareButtonAndNetworkDisconnected_thenShowFailMessage() throws Exception {
        // when
        when(cardTransfer.isConnectedToNetwork()).thenReturn(false);
        subject.cardShareButton.performClick();

        // then
        assertThat(ShadowToast.getLatestToast()).isNotNull();
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("‘설정’ 앱에서 모바일 데이터나\nWi-Fi를 켜주세요");
    }

    @Test
    public void whenClickShareButtonAndSelectKakaotalkAndUploadFail_thenShowFailMessage() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnFailureListener onFailureListener = ((OnFailureListener) invocation.getArguments()[2]);
                onFailureListener.onFailure(new Exception());
                return null;
            }
        }).when(subject.cardTransfer).uploadCard(any(CardModel.class), any(OnSuccessListener.class), any(OnFailureListener.class));

        // when
        subject.cardShareButton.performClick();
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);
        View innerView = shadowDialog.getView();
        innerView.findViewById(R.id.item_kakaotalk).performClick();
        innerView.findViewById(R.id.confirm_button).performClick();

        // then
        assertThat(ShadowToast.getLatestToast()).isNotNull();
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("카드 보내기에 실패했습니다.\n다시 시도해 보세요.");
    }

    @Test
    public void whenViewPageChanged_thenSetApplicationMangerCurrentIndex() throws Exception {
        int juiceViewPageIndex = 3;
        subject.mViewPager.setCurrentItem(juiceViewPageIndex);
        verify(applicationManager).setCurrentCardIndex(2);
    }

    @Test
    public void givenShowingCardEditSelectPopup_whenClickCardNameEditButton_thenMoveToMakeCardActivityWithCardId() throws Exception {
        // given
        subject.findViewById(R.id.card_edit_button).performClick();
        // when
        AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        dialog.findViewById(R.id.card_edit_name_text).performClick();
        // then
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getStringExtra(ApplicationConstants.EDIT_CARD_ID)).isNotEmpty();
        assertThat(startedIntent.getStringExtra(ApplicationConstants.EDIT_TYPE)).isEqualTo(ApplicationConstants.CardEditType.NAME.value());
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(MakeCardActivity.class.getCanonicalName());
    }

    @Test
    public void givenShowingCardEditSelectPopup_whenClickVoiceEditButton_thenMoveToMakeCardActivityWithCardId() throws Exception {
        // given
        subject.findViewById(R.id.card_edit_button).performClick();
        // when
        AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        dialog.findViewById(R.id.card_edit_voice_text).performClick();
        // then
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getStringExtra(ApplicationConstants.EDIT_CARD_ID)).isNotEmpty();
        assertThat(startedIntent.getStringExtra(ApplicationConstants.EDIT_TYPE)).isEqualTo(ApplicationConstants.CardEditType.VOICE.value());
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(MakeCardActivity.class.getCanonicalName());
    }

    @Test
    public void givenShowingCardEditSelectPopup_whenClickCancelButton_thenDismissDialog() throws Exception {
        // given
        subject.findViewById(R.id.card_edit_button).performClick();
        // when
        AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
        dialog.findViewById(R.id.cancel_button).performClick();
        // then
        assertThat(shadowOf(dialog).hasBeenDismissed()).isTrue();
    }

    @Test
    public void whenActivityOnPause_thenIsNotForegroundRunning() throws Exception {
        subject.onPause();
        assertThat(subject.isForegroundRunning).isFalse();
    }

    private List<CardModel> getCardListWithCategoryId() {

        String contentFolder = ContentsUtil.getContentFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator;

        List<CardModel> ret = Lists.newArrayList(
                makeSingleCardModel("1", "물", contentFolder+"water.jpg", "20010928_120020", 0, 0, CardModel.CardType.PHOTO_CARD, null, false),
                makeSingleCardModel("2",  "우유", contentFolder+"milk.png", "20010928_120019", 0, 1, CardModel.CardType.PHOTO_CARD, null, false),
                makeSingleCardModel("3",  "쥬스", contentFolder+"juice.png", "20010928_120015", 0, 2, CardModel.CardType.PHOTO_CARD, null, false),
                makeSingleCardModel("4",  "젤리", contentFolder+"haribo.mp4", "20010928_120015", 0, 3, CardModel.CardType.VIDEO_CARD, contentFolder+"haribo.jpg", false)
        );

        return ret;
    }

    public CardModel makeSingleCardModel(String id, String name, String path, String time, int categoryId, int cardIndex, CardModel.CardType cardType, String thumbnailPath , boolean hide) {
        return CardModel.builder()._id(id).name(name).contentPath(path).firstTime(time).categoryId(categoryId).cardIndex(cardIndex).cardType(cardType).thumbnailPath(thumbnailPath).hide(hide).build();
    }


    private CategoryModel getCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.index = 0;
        categoryModel.title = "먹을 것";
        categoryModel.color = ResourcesUtil.RED;
        return categoryModel;
    }

    private int getCategoryModelColor() {
        return ResourcesUtil.getCardViewLayoutBackgroundBy(getCategoryModel().color);
    }

}