package act.sds.samsung.angelman.presentation.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowDrawable;
import org.robolectric.shadows.ShadowInputMethodManager;

import javax.inject.Inject;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.presentation.custom.CardView;
import act.sds.samsung.angelman.presentation.util.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;
import act.sds.samsung.angelman.presentation.util.PlayUtil;
import act.sds.samsung.angelman.presentation.util.RecordUtil;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static act.sds.samsung.angelman.R.id.counting_scene;
import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MakeCardActivityTest extends UITest{

    @Inject
    CardRepository cardRepository;

    @Inject
    ApplicationManager applicationManager;

    private MakeCardActivity subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
    }

    private void setupPhotoCard() {
        Intent intent = new Intent();
        intent.putExtra(ContentsUtil.CONTENT_PATH, "/Users/ssa009/workspace/angelman/app/src/main/assets/bus.jpg");
        intent.putExtra(ContentsUtil.CARD_TYPE, CardModel.CardType.PHOTO_CARD.getValue());
        when(applicationManager.getCategoryModel()).thenReturn(getCategoryModel());
        when(applicationManager.getCategoryModelColor()).thenReturn(getCategoryModelColor());
        subject = setupActivityWithIntent(MakeCardActivity.class, intent);
    }
    private void setupVideoCard() {
        Intent intent = new Intent();
        intent.putExtra(ContentsUtil.CONTENT_PATH, "/Users/ssa009/workspace/angelman/app/src/main/assets/bus.jpg");
        intent.putExtra(ContentsUtil.CARD_TYPE ,CardModel.CardType.VIDEO_CARD.getValue());
        when(applicationManager.getCategoryModel()).thenReturn(getCategoryModel());
        when(applicationManager.getCategoryModelColor()).thenReturn(getCategoryModelColor());
        subject = setupActivityWithIntent(MakeCardActivity.class, intent);

    }

    @Test
    public void whenLaunchedApp_thenSetBackgroundColorChangedToRelatedInCategory() throws Exception {
        setupPhotoCard();
        assertThat(applicationManager.getCategoryModelColor()).isEqualTo(R.drawable.background_gradient_blue);
    }

    @Test
    public void when1SecondAfterLaunched_thenShowKeyboardAndEditTextViewAndHideTextView() throws InterruptedException {
        setupPhotoCard();

        TextView cardImageTitle = (TextView) subject.findViewById(R.id.card_image_title);
        EditText editText = (EditText) subject.findViewById(R.id.card_image_title_edit);

        assertThat(cardImageTitle.isShown()).isFalse();
        assertThat(editText.isShown()).isTrue();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView cardImageTitle = (TextView) subject.findViewById(R.id.card_image_title);
                EditText editText = (EditText) subject.findViewById(R.id.card_image_title_edit);

                assertThat(cardImageTitle.isShown()).isFalse();
                assertThat(editText.isShown()).isTrue();

                assertThat(editText.getText()).isEqualTo(getString(R.string.require_card_name));
                assertThat(editText.isFocused()).isTrue();
            }
        }, 100);
    }

    @Test
    public void givenEditTextIsFocusedAndKeyboardIsShown_whenInputOneMoreCharacterAndEnterClicked_thenShowCardTitleTextView() throws Exception {
        setupPhotoCard();

        EditText cardTitleEdit = (EditText) subject.findViewById(R.id.card_image_title_edit);
        TextView textView = ((TextView) subject.findViewById(R.id.card_image_title));

        assertThat(textView).isGone();
        assertThat(cardTitleEdit).isVisible();

        cardTitleEdit.setText("TEST");

        enterKey(cardTitleEdit);

        assertThat(textView).isGone();
        assertThat(cardTitleEdit).isVisible();
    }

    @Test
    public void givenExistSingleCard_whenClickEnter_thenSaveNewSingleCard() throws Exception {
        setupPhotoCard();


        EditText cardTitleEdit = (EditText) subject.findViewById(R.id.card_image_title_edit);
        TextView textView = ((TextView) subject.findViewById(R.id.card_image_title));
        subject.cardView.status = CardView.CardViewStatus.CARD_TITLE_EDITABLE;
        cardTitleEdit.setText("치킨");
        enterKey(cardTitleEdit);

        assertThat(textView.getText().toString()).isEqualTo(cardTitleEdit.getText().toString());
    }

    @Test
    public void whenCompletedCardTitleTyping_thenShowMicButton() throws Exception {
        setupPhotoCard();

        EditText cardTitleEdit = (EditText) subject.findViewById(R.id.card_image_title_edit);
        cardTitleEdit.setText("TEST");

        enterKey(cardTitleEdit);

        assertThat(subject.findViewById(R.id.recoding_guide)).isVisible();
        assertThat(subject.findViewById(R.id.mic_btn)).isVisible();
    }

    @Test
    public void givenShowMicButton_whenClickMicButton_thenShowCountingScene() throws Exception {
        setupPhotoCard();

        Button micBtn = (Button) subject.findViewById(R.id.mic_btn);
        micBtn.setVisibility(View.VISIBLE);

        micBtn.performClick();

        assertThat(subject.findViewById(counting_scene)).isVisible();
    }

    @Test
    public void whenShowCountingScene_thenCountDown3To1() throws Exception {
        setupPhotoCard();

        Button micBtn = (Button) subject.findViewById(R.id.mic_btn);
        micBtn.setVisibility(View.VISIBLE);
        micBtn.performClick();

        TextView waitingCount = (TextView) subject.findViewById(R.id.waiting_count);
        assertThat(waitingCount.getText().toString()).isEqualTo("3");
        Robolectric.flushForegroundThreadScheduler();
        assertThat(waitingCount.getText().toString()).isEqualTo("2");
        Robolectric.flushForegroundThreadScheduler();
        assertThat(waitingCount.getText().toString()).isEqualTo("1");
    }

    @Test
    public void whenCountDown3To1_thenStartVoiceRecordAndShowRecordStopButton() throws Exception {
        setupPhotoCard();
        subject.recordUtil = mock(RecordUtil.class);
        subject.playUtil = mock(PlayUtil.class);

        Button micBtn = (Button) subject.findViewById(R.id.mic_btn);
        micBtn.setVisibility(View.VISIBLE);
        micBtn.performClick();

        TextView recordGuide = (TextView) subject.findViewById(R.id.waiting_count);
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertThat(recordGuide.getText()).isEqualTo("지금 말해주세요!");

        verify(subject.recordUtil).record(anyString(), any(RecordUtil.RecordCallback.class));

        Button recordStopBtn = (Button) subject.findViewById(R.id.record_stop_btn);

        assertThat(recordStopBtn).isVisible();

        recordStopBtn.performClick();
        verify(subject.recordUtil).stopRecord();

        assertThat(((TextView) subject.findViewById(R.id.waiting_count)).getText()).isEqualTo("녹음된 음성을 확인하세요");

    }

    @Test
    public void whenStopRecord_thenShowsCheckRecordButtonAndText() throws Exception {
        setupPhotoCard();

        recordComplete();
        assertThat(((TextView) subject.findViewById(R.id.waiting_count)).getText()).isEqualTo("녹음된 음성을 확인하세요");
        ShadowDrawable shadowDrawable = shadowOf(subject.findViewById(R.id.record_stop_btn).getBackground());
        assertThat(R.drawable.ic_check_button).isEqualTo(shadowDrawable.getCreatedFromResId());
    }

    @Test
    public void whenFinishToTakeAPictureThenShowEditTextOfCardNameAndKeyboardWithKoreanCompleteKey() throws Exception {
        setupPhotoCard();

        EditText cardTitleEdit = (EditText) subject.findViewById(R.id.card_image_title_edit);
        assertThat(cardTitleEdit.getMaxLines()).isEqualTo(1);
    }

    @Test
    public void whenStopRecordAndCheckButtonIsClicked_thenCreateNewCard() throws Exception {
        setupPhotoCard();

        recordComplete();
        subject.findViewById(R.id.record_stop_btn).performClick();
        verify(cardRepository).createSingleCardModel((CardModel) anyObject());
    }

    @Test
    public void whenStopRecordAndCheckButtonIsClicked_thenFinishMakeCardAndShowCardList() throws Exception {
        setupPhotoCard();

        recordComplete();
        subject.findViewById(R.id.record_stop_btn).performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();

        assertThat(nextStartedActivity.getComponent().getClassName()).contains("CardViewPagerActivity");
        assertThat(nextStartedActivity.getExtras().get(CardViewPagerActivity.INTENT_KEY_NEW_CARD)).isNotNull();
        assertThat(subject).isFinishing();
    }

    @Test
    public void givenCardTitleEditableMode_whenClickedHardwareBackButton_thenFinishMakeCardActivity() throws Exception {
        setupPhotoCard();

        EditText cardTitleEdit = (EditText) subject.findViewById(R.id.card_image_title_edit);
        subject.cardView.status = CardView.CardViewStatus.CARD_TITLE_EDITABLE;
        cardTitleEdit.setText("치킨");

        subject.onBackPressed();

        ShadowActivity shadowActivity = shadowOf(subject);
        assertThat(shadowActivity.isFinishing()).isTrue();

    }

    @Test
    public void givenCardTitleShownMode_whenClickedHardwareBackButton_thenChangeCardStatusAndShowKeyBoard() throws Exception {
        setupPhotoCard();
        EditText cardTitleEdit = (EditText) subject.findViewById(R.id.card_image_title_edit);
        TextView cardTitle = (TextView) subject.findViewById(R.id.card_image_title);

        subject.cardView.status = CardView.CardViewStatus.CARD_TITLE_EDITABLE;
        cardTitleEdit.setText("치킨");
        enterKey(cardTitleEdit);

        subject.onBackPressed();

        assertThat(cardTitleEdit).isVisible();
        assertThat(cardTitle).isGone();

        ShadowInputMethodManager shadowIMM = shadowOf(subject.imm);
        assertThat(shadowIMM.isSoftInputVisible());

    }

    @Test
    public void givenClickedMicButtonAndWhenStartedCountDown_whenClickedHardwareBackButton_then() throws Exception {
        setupPhotoCard();
        Button micBtn = (Button) subject.findViewById(R.id.mic_btn);
        micBtn.setVisibility(View.VISIBLE);

        micBtn.performClick();

        assertThat(subject.findViewById(counting_scene)).isVisible();

        subject.onBackPressed();

        assertThat(subject.findViewById(counting_scene)).isGone();
    }

    @Test
    public void givenClickedMicButtonAndFinishedToRecord_whenClickedHardwareBackButton_then() throws Exception {
        setupPhotoCard();
        subject.recordUtil = mock(RecordUtil.class);
        subject.playUtil = mock(PlayUtil.class);

        Button micBtn = (Button) subject.findViewById(R.id.mic_btn);
        micBtn.setVisibility(View.VISIBLE);

        micBtn.performClick();

        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        (subject.findViewById(R.id.record_stop_btn)).performClick();
        assertThat(subject.state).isEqualTo(1);

        subject.onBackPressed();

        assertThat(subject.findViewById(counting_scene)).isGone();
        assertThat(subject.state).isEqualTo(0);

        assertThat(shadowOf((subject.findViewById(R.id.record_stop_btn)).getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.record_stop);
    }

    @Test
    public void whenShowMakeVideoCardActivity_thenShowVideoPreviewAndPlayButton() throws Exception {
        setupVideoCard();
        assertThat(subject.findViewById(R.id.card_image)).isGone();
        assertThat(subject.findViewById(R.id.card_video)).isVisible();
        assertThat(subject.findViewById(R.id.play_button)).isVisible();
    }

    @Test
    public void whenShowMakePhotoCardActivity_thenShowImageView() throws Exception {
        setupPhotoCard();
        assertThat(subject.findViewById(R.id.card_image)).isVisible();
        assertThat(subject.findViewById(R.id.card_video)).isGone();
        assertThat(subject.findViewById(R.id.play_button)).isGone();
    }

    private void recordComplete() {
        PlayUtil mockPlayUtil = mock(PlayUtil.class);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                return null;
            }
        }).when(mockPlayUtil).play(anyString());

        subject.playUtil = mockPlayUtil;

        subject.findViewById(R.id.counting_scene).setVisibility(View.VISIBLE);
        subject.findViewById(R.id.record_stop_btn).setVisibility(View.VISIBLE);
        subject.findViewById(R.id.record_stop_btn).performClick();
    }

    private void enterKey(EditText cardTitleEdit) {
        cardTitleEdit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        cardTitleEdit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
    }

    private CategoryModel getCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.color =  ResourcesUtil.BLUE;
        return categoryModel;
    }

    private Integer getCategoryModelColor() {
        return ResourcesUtil.getCardViewLayoutBackgroundBy(getCategoryModel().color);
    }
}