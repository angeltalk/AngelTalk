package act.angelman.presentation.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Ignore;
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
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;
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
import act.angelman.presentation.adapter.CardImageAdapter;
import act.angelman.presentation.custom.AddCardView;
import act.angelman.presentation.custom.CardView;
import act.angelman.presentation.custom.CardViewPager;
import act.angelman.presentation.custom.VideoCardTextureView;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.manager.ApplicationManager;
import act.angelman.presentation.util.AngelManGlideTransform;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.PlayUtil;
import act.angelman.presentation.util.ResourcesUtil;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CardViewPagerActivityTest extends UITest {

    @Inject
    CardRepository repository;

    @Inject
    ApplicationManager applicationManager;

    private CardViewPagerActivity subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
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
        assertThat(subject.buttonContainer).isVisible();
    }

    @Test
    public void whenLaunchedCardViewPagerActivity_thenShowsButtonContainer() throws Exception {
        assertThat(subject.buttonContainer).isVisible();
    }

    @Test
    public void whenClickedDeleteButton_thenShowsAlertDialog() throws Exception {
        ShadowAlertDialog shadowDialog = getShadowAlertDialog();
        assertThat(shadowDialog).isNotNull();
    }

    @Test
    public void whenClickedDeleteButton_thenShowsAlertDialogMessage() throws Exception {
        ShadowAlertDialog shadowDialog = getShadowAlertDialog();
        shadowDialog.getView().findViewById(R.id.confirm_button).performClick();

        assertThat(((TextView) shadowDialog.getView().findViewById(R.id.alert_message)).getText()).contains("카드를 삭제하시겠습니까?");
    }

    @Test
    public void whenClickedDeleteButton_thenShowsCardTitleCorrectly() throws Exception {
        subject.mViewPager.setCurrentItem(1);
        assertThat(((CardView) ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1)).cardTitle.getText()).isEqualTo("물");
        ShadowAlertDialog shadowDialog = getShadowAlertDialog();
        assertThat(((TextView) shadowDialog.getView().findViewById(R.id.alert_message)).getText()).contains( "물" );
    }

    @Test
    @Ignore
    public void whenClickedEditButton_thenShowsEditSelectDialog() throws Exception {
        subject.findViewById(R.id.card_edit_button).performClick();

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(alertDialog).isNotNull();
        assertThat(((TextView) alertDialog.findViewById(R.id.card_edit_select_guide)).getText()).contains( "카드를 수정해보세요" );
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

        ShadowAlertDialog shadowDialog = getShadowAlertDialog();
        shadowDialog.getView().findViewById(R.id.confirm_button).performClick();

        assertThat(viewPager.getAdapter().getCount()).isEqualTo(4);
        assertThat(((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(2)).cardTitle.getText()).isEqualTo(cardListWithCategoryId.get(2).name);
    }

    @Test
    public void whenCardViewPagerActivityLaunched_thenShowCardsRelatedInCategory() throws Exception {
        CardViewPager viewPager = subject.mViewPager;

        assertThat(viewPager.getAdapter()).isNotNull();
        assertThat(viewPager.getAdapter().getCount()).isNotEqualTo(0);

        viewPager.invalidate();
        viewPager.requestLayout();

        assertThat(((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(1)).cardTitle.getText()).isEqualTo("물");

        ImageView cardImageView = ((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(1)).cardImage;

        if (cardImageView.getDrawable() != null) {

            Bitmap actualImage = ((GlideBitmapDrawable) cardImageView.getDrawable()).getBitmap();

            try {
                ImageView expectedImageView = new ImageView(RuntimeEnvironment.application);
                expectedImageView.setLayoutParams(cardImageView.getLayoutParams());

                Glide.with(RuntimeEnvironment.application)
                        .load("file:///android_asset/bus.png")
                        .bitmapTransform(new AngelManGlideTransform(RuntimeEnvironment.application, 10, 0, AngelManGlideTransform.CornerType.TOP))
                        .override(280, 280)
                        .into(expectedImageView);

                Bitmap expectedImage = ((GlideBitmapDrawable) expectedImageView.getDrawable()).getBitmap();
                assertThat(equals(actualImage, expectedImage)).isTrue();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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

        assertThat(((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(1)).cardTitle.getText()).isEqualTo("물");

        ImageView cardImageView = ((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(1)).cardImage;

        Bitmap actualImage = null;

        if (cardImageView != null && cardImageView.getDrawable() != null) {
            actualImage = ((GlideBitmapDrawable) cardImageView.getDrawable()).getBitmap();
        }
        try {
            ImageView expectedImageView = new ImageView(subject.getApplicationContext());
            expectedImageView.setLayoutParams(cardImageView.getLayoutParams());

            rm.load("file:///android_asset/bus.png")
                    .bitmapTransform(new AngelManGlideTransform(subject.getApplicationContext(), 10, 0, AngelManGlideTransform.CornerType.TOP))
                    .override(280, 280)
                    .into(expectedImageView);
            Bitmap expectedImage = ((GlideBitmapDrawable)expectedImageView.getDrawable()).getBitmap();
            assertThat(equals(actualImage, expectedImage)).isTrue();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
    public void whenFinishedCardListActivityAndCurrentCardIsHide_thenShowFirstCardInViewPager() throws Exception {
        Intent intent = new Intent();

        intent.putExtra(ApplicationConstants.INTENT_KEY_LIST_BACK, true);

        when(repository.getSingleCardListWithCategoryId(anyInt(), anyBoolean())).thenReturn(getCardListWithCategoryId());
        when(applicationManager.getCurrentCardIndex()).thenReturn(8);

        subject = setupActivityWithIntent(CardViewPagerActivity.class, intent);

        assertThat(subject.mViewPager.getCurrentItem()).isEqualTo(1);
    }

    @Test
    public void whenShareButtonClick_thenShowAvailableMessengerList() throws Exception{
        subject.cardShareButton.performClick();
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);
        assertThat(shadowDialog).isNotNull();
    }

    @Test
    public void givenKakaotalkNotInstall_whenShareButtonClick_thenShowAvailableMessengerListWithOutKakaotalk() throws Exception{

        subject.cardShareButton.performClick();
        subject.pm = mock(PackageManager.class);
        when(subject.pm.getPackageInfo("com.kakao.talk",PackageManager.GET_ACTIVITIES)).thenThrow(new PackageManager.NameNotFoundException());

        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowDialog = shadowOf(alert);
        assertThat(shadowDialog).isNotNull();
        assertThat(shadowDialog.getView().findViewById(R.id.item_kakaotalk).getVisibility()).isEqualTo(View.GONE);
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

        innerView.findViewById(R.id.item_message).performClick();
        assertThat(((AppCompatRadioButton) innerView.findViewById(R.id.radio_kakaotalk)).isChecked()).isFalse();
        assertThat(((AppCompatRadioButton) innerView.findViewById(R.id.radio_message)).isChecked()).isTrue();
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
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("카드 공유가 실패하였습니다.");
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
        assertThat(startedIntent.getStringExtra("CARD_ID")).isNotEmpty();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(MakeCardActivity.class.getCanonicalName());
    }

    public boolean equals(Bitmap bitmap1, Bitmap bitmap2) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }

    private List<CardModel> getCardListWithCategoryId() {

        String contentFolder = ContentsUtil.getContentFolder() + File.separator;

        List<CardModel> ret = Lists.newArrayList(
                makeSingleCardModel( "물", contentFolder+"water.png", "20010928_120020", 0, 0, CardModel.CardType.PHOTO_CARD, null, false),
                makeSingleCardModel( "우유", contentFolder+"milk.png", "20010928_120019", 0, 1, CardModel.CardType.PHOTO_CARD, null, false),
                makeSingleCardModel( "쥬스", contentFolder+"juice.png", "20010928_120015", 0, 2, CardModel.CardType.PHOTO_CARD, null, false),
                makeSingleCardModel( "젤리", contentFolder+"haribo.mp4", "20010928_120015", 0, 3, CardModel.CardType.VIDEO_CARD, contentFolder+"haribo.jpg", false)
        );

        return ret;
    }

    int i = 0;
    public CardModel makeSingleCardModel(String name, String path, String time, int categoryId, int cardIndex, CardModel.CardType cardType, String thumbnailPath , boolean hide) {
        return CardModel.builder()._id(String.valueOf(++i)).name(name).contentPath(path).firstTime(time).categoryId(categoryId).cardIndex(cardIndex).cardType(cardType).thumbnailPath(thumbnailPath).hide(hide).build();
    }

    private ShadowAlertDialog getShadowAlertDialog() {
        subject.cardDeleteButton.performClick();

        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        return shadowOf(alert);
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