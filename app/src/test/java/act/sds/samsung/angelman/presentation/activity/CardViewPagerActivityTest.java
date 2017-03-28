package act.sds.samsung.angelman.presentation.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboVibrator;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLooper;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.presentation.adapter.CardImageAdapter;
import act.sds.samsung.angelman.presentation.custom.AddCardView;
import act.sds.samsung.angelman.presentation.custom.CardView;
import act.sds.samsung.angelman.presentation.custom.CardViewPager;
import act.sds.samsung.angelman.presentation.custom.VideoCardTextureView;
import act.sds.samsung.angelman.presentation.util.AngelManGlideTransform;
import act.sds.samsung.angelman.presentation.util.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.PlayUtil;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static junit.framework.Assert.assertTrue;
import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
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
    private ImageButton deleteCardButton;
    private TextView addCardText;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(repository.getSingleCardListWithCategoryId(anyInt())).thenReturn(getCardListWithCategoryId());
        when(applicationManager.getCategoryModel()).thenReturn(getCategoryModel());
        when(applicationManager.getCategoryModelColor()).thenReturn(getCategoryModelColor());
        subject = setupActivity(CardViewPagerActivity.class);
        subject.mViewPager.setCurrentItem(1);

        deleteCardButton = (ImageButton) subject.findViewById(R.id.card_delete_button);
        addCardText = (TextView) subject.findViewById(R.id.add_card_button_text);
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

        assertThat(deleteCardButton).isGone();
        assertThat(addCardText).isGone();
        assertThat(((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(0)).isInstanceOf(AddCardView.class);
    }

    @Test
    public void givenLaunched_whenMoveToPhotoCard_thenShowTitleCorrectly() throws Exception {
        subject.mViewPager.setCurrentItem(1);

        View view = ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(1);
        assertThat(view.findViewById(R.id.card_video)).isGone();
        assertThat(view.findViewById(R.id.card_image)).isVisible();
    }

    @Test
    public void givenLaunched_whenMoveToVideoCard_thenShowTitleCorrectly() throws Exception {
        subject.mViewPager.setCurrentItem(4);

        View view = ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(4);
        assertThat(view.findViewById(R.id.card_video)).isVisible();
        assertThat(view.findViewById(R.id.card_image)).isGone();
    }

    @Test
    public void whenLaunchedCardViewPagerActivity_thenShowsDeleteButton() throws Exception {
        assertThat(deleteCardButton).isVisible();
    }

    @Test
    public void whenClickedDeleteButton_thenShowsAlertDialog() throws Exception {
        ShadowAlertDialog shadowDialog = getShadowAlertDialog();
        assertThat(shadowDialog).isNotNull();
    }

    @Test
    public void whenClickedDeleteButton_thenShowsAlertDialogMessage() throws Exception {
        ShadowAlertDialog shadowDialog = getShadowAlertDialog();
        shadowDialog.getView().findViewById(R.id.confirm).performClick();

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
    public void givenClickedDeleteButton_whenClickedConfirmButton_thenDeleteSelectedCardViewInViewPager() throws Exception {
        CardViewPager viewPager = subject.mViewPager;
        assertThat(viewPager.getAdapter().getCount()).isEqualTo(5);

        subject.mViewPager.setCurrentItem(2);
        assertThat(((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(2)).cardTitle.getText()).isEqualTo("우유");
        assertThat(deleteCardButton.getVisibility()).isEqualTo(View.VISIBLE);

        when(repository.deleteSingleCardWithCardIndex(anyInt(), anyInt())).thenReturn(true);
        final ArrayList<CardModel> cardListWithCategoryId = getCardListWithCategoryId();
        cardListWithCategoryId.remove(2);
        when(repository.getSingleCardListWithCategoryId(anyInt())).thenReturn(cardListWithCategoryId);

        ShadowAlertDialog shadowDialog = getShadowAlertDialog();
        shadowDialog.getView().findViewById(R.id.confirm).performClick();

        assertThat(viewPager.getAdapter().getCount()).isEqualTo(4);
        assertThat(((CardView) ((CardImageAdapter) viewPager.getAdapter()).getItemAt(2)).cardTitle.getText()).isEqualTo(cardListWithCategoryId.get(2).name);
    }

    @Test
    public void whenClickedBackButton_thenFinishesCardViewPagerActivity() throws Exception {
        ImageView backButton = (ImageView) subject.findViewById(R.id.back_button);

        assertThat(backButton).isVisible();
        backButton.performClick();

        ShadowActivity activityShadow = shadowOf(subject);
        assertTrue(activityShadow.isFinishing());
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
    @Ignore
    public void givenShownVideoCardOnScreen_whenClickCardView_thenPlayVideo() throws Exception {
        subject.mViewPager.setCurrentItem(4);

        View cardView = ((CardImageAdapter) subject.mViewPager.getAdapter()).getItemAt(4);
        cardView.findViewById(R.id.card_container).performClick();

        Robolectric.getForegroundThreadScheduler().advanceBy(1000, TimeUnit.MILLISECONDS);

        assertThat(((VideoCardTextureView) cardView.findViewById(R.id.card_video)).isPlaying()).isTrue();
    }

    @Test
    public void whenClickedAddButton_thenMovesToCameraGallerySelectionActivity() throws Exception {
        addCardText.performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CameraGallerySelectionActivity.class.getCanonicalName());
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
    public void whenClickAddCardButton_thenShowMainActivity() throws Exception {

        addCardText.performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CameraGallerySelectionActivity.class.getCanonicalName());
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

    public boolean equals(Bitmap bitmap1, Bitmap bitmap2) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }

    @Test
    public void whenFinishedToMakeNewCard_thenShowsNewAddedCardAtFirstInCardViewPager() throws Exception {
        Intent intent = new Intent();

        intent.putExtra(CardViewPagerActivity.INTENT_KEY_NEW_CARD, true);

        when(repository.getSingleCardListWithCategoryId(anyInt())).thenReturn(getCardListWithCategoryId());

        subject = setupActivityWithIntent(CardViewPagerActivity.class, intent);

        assertThat(subject.mViewPager.getCurrentItem()).isNotEqualTo(0);
        assertThat(subject.mViewPager.getCurrentItem()).isEqualTo(1);

    }

    private ArrayList<CardModel> getCardListWithCategoryId() {
        ArrayList<CardModel> ret = new ArrayList<>();
        addSingleCardModel(ret, "물", "water.png", "20010928_120020", 0, 0, CardModel.CardType.PHOTO_CARD);
        addSingleCardModel(ret, "우유", "milk.png", "20010928_120019", 0, 1, CardModel.CardType.PHOTO_CARD);
        addSingleCardModel(ret, "쥬스", "juice.png", "20010928_120015", 0, 2, CardModel.CardType.PHOTO_CARD);
        addSingleCardModel(ret, "젤리", "haribo.mp4", "20010928_120015", 0, 3, CardModel.CardType.VIDEO_CARD);

        return ret;
    }

    public void addSingleCardModel(ArrayList<CardModel> list, String name, String path, String time, int categoryId, int cardIndex, CardModel.CardType cardType) {
        CardModel model = new CardModel(name, path, time, categoryId, cardIndex, cardType);
        list.add(model);
    }

    private ShadowAlertDialog getShadowAlertDialog() {
        deleteCardButton.performClick();

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