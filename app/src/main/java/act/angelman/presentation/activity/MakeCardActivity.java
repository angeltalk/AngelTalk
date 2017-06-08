package act.angelman.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.percent.PercentRelativeLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.common.base.Strings;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import act.angelman.AngelmanApplication;
import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.presentation.custom.CardView;
import act.angelman.presentation.custom.VideoCardTextureView;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.manager.ApplicationConstants.CardEditType;
import act.angelman.presentation.manager.ApplicationManager;
import act.angelman.presentation.util.AngelManGlideTransform;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.FileUtil;
import act.angelman.presentation.util.PlayUtil;
import act.angelman.presentation.util.RecordUtil;
import act.angelman.presentation.util.ResolutionUtil;
import act.angelman.presentation.util.ResourcesUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeCardActivity extends AbstractActivity implements RecordUtil.RecordCallback {

    private static final int STATE_RECORD_NOT_COMPLETE = 0;
    private static final int STATE_RECORD_COMPLETE = 1;
    private static final int INIT_COUNT = 3;
    private static final int COUNT_TEXT_SIZE = 60;
    private static final long COUNT_INTERVAL = 1000;
    private int mCountDown = INIT_COUNT;

    private String contentPath;
    private RequestManager glide;
    private int selectedCategoryId;
    private String voiceFilePath;
    private Handler countHandler = new Handler();
    private CardModel.CardType cardType;
    private CardEditType editType;
    private String editCardId;

    protected int state = STATE_RECORD_NOT_COMPLETE;
    protected InputMethodManager imm;

    RecordUtil recordUtil = RecordUtil.getInstance();
    PlayUtil playUtil;

    private CardModel editCardModel;

    @Inject
    CardRepository cardRepository;

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.root_layout)
    ViewGroup rootLayout;

    @BindView(R.id.show_card_layout)
    ViewGroup showCardLayout;

    @BindView(R.id.record_stop_button)
    Button recordStopButton;

    @BindView(R.id.mic_btn)
    Button micButton;

    @BindView(R.id.waiting_count)
    TextView waitCount;

    @BindView(R.id.counting_scene)
    PercentRelativeLayout countScene;

    @BindView(R.id.rerecord_button)
    Button rerecordButton;

    @BindView(R.id.replay_button)
    Button replayButton;

    @BindView(R.id.card_view_layout)
    CardView cardView;

    @BindView(R.id.confirm_button)
    Button confirmButton;

    @BindView(R.id.card_image_title_edit)
    EditText cardTitleEditText;

    @BindView(R.id.tts_btn)
    Button ttsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_make_card);
        ButterKnife.bind(this);

        glide = Glide.with(this);
        playUtil = PlayUtil.getInstance();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        applicationManager.setCategoryBackground(
                findViewById(R.id.show_card_layout),
                applicationManager.getCategoryModelColor()
        );

        selectedCategoryId = applicationManager.getCategoryModel().index;

        initCardData();
        initCardView();
    }

    @Override
    public void onBackPressed() {
        playUtil.playStop();

        if(editType == CardEditType.VOICE && countScene.getVisibility() == View.GONE) {
            moveToCardViewPagerActivity();
            return;
        }

        if(countScene.getVisibility() == View.GONE){
            switch(cardView.status){
                case CARD_TITLE_SHOWN:
                    cardView.changeCardViewStatus();
                    showKeyboard();
                    break;
                case CARD_TITLE_EDITABLE:
                    finish();
                    break;
                default:
                    break;
            }
        } else {
            prepareLayoutForVoiceRecord();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        cardView.cardTitleEdit.removeTextChangedListener(cardTitleTextWatcher);
    }

    @Override
    public void afterRecord() {
        playCardTitle();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideKeyboard();

        recordUtil.stopRecord();
        playUtil.playStop();
    }

    @OnClick(R.id.record_stop_button)
    public void onClickRecStopButton(View view){
        if (state == STATE_RECORD_NOT_COMPLETE) {
            recordUtil.stopRecord();
            playCardTitle();
        } else {
            if(isCardEditing() && editType.equals(CardEditType.VOICE)) {
                if(editCardModel.voicePath != null ){
                    FileUtil.removeFile(editCardModel.voicePath);
                }
                cardRepository.updateSingleCardVoice(editCardModel._id, voiceFilePath);
                moveToCardViewPagerActivityAfterEditing();
            } else {
                saveCardAndMoveToNextActivity();
            }
        }
    }

    @OnClick(R.id.tts_btn)
    public void onClickTtsButton(View view) {
        showCountingScene();
        changeCountingSceneForRecoding();
        changeCountingSceneForPlay();
        playCardTitle();
    }

    private void changeCountingSceneForRecoding() {
        recordStopButton.setVisibility(View.VISIBLE);
        waitCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
    }

    @OnClick(R.id.mic_btn)
    public void onClickMicButton(View view){
        showCountingScene();
        countHandler.postDelayed(countAction, COUNT_INTERVAL);
    }

    private void showCountingScene() {
        countScene.setVisibility(View.VISIBLE);
        waitCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 62);
        ttsButton.setEnabled(false);
        micButton.setEnabled(false);
    }


    @OnClick(R.id.replay_button)
    public void onClickReplayButton(View view){
        playCardTitle();
    }

    @OnClick(R.id.rerecord_button)
    public void onClickRetakeButton(View view) {
        onBackPressed();
    }

    @OnClick(R.id.confirm_button)
    public void onClickConfirmButton(View view) {
        editCardModel.name = cardView.cardTitleEdit.getText().toString();
        cardRepository.updateSingleCardName(editCardModel._id, editCardModel.name);
        moveToCardViewPagerActivityAfterEditing();
    }

    private void initCardView() {
        cardView.cardImage.setVisibility(View.VISIBLE);
        cardView.setCardViewLayoutMode(CardView.MODE_MAKE_CARD);
        if(cardType.equals(CardModel.CardType.PHOTO_CARD)) {
            cardView.cardVideo.setVisibility(View.GONE);
            cardView.playButton.setVisibility(View.GONE);
            cardView.cardImage.setScaleType(ImageView.ScaleType.FIT_XY);
            glide.load(ContentsUtil.getContentFile(contentPath))
                    .bitmapTransform(new AngelManGlideTransform(this, 10, 0, AngelManGlideTransform.CornerType.TOP))
                    .into(cardView.cardImage);

        } else if (cardType.equals(CardModel.CardType.VIDEO_CARD)) {
            cardView.cardVideo.setVisibility(View.VISIBLE);
            cardView.cardImage.setScaleType(ImageView.ScaleType.FIT_XY);
            glide.load(ContentsUtil.getContentFile(ContentsUtil.getThumbnailPath(contentPath)))
                    .override(280, 280)
                    .into(cardView.cardImage);
            cardView.playButton.setVisibility(View.VISIBLE);
            cardView.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardView.cardImage.setVisibility(View.GONE);
                    cardView.playButton.setVisibility(View.GONE);
                    cardView.cardVideo.play(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            cardView.playButton.setVisibility(View.VISIBLE);
                            cardView.cardVideo.resetPlayer();
                        }
                    });
                }
            });

            cardView.cardVideo.setScaleType(VideoCardTextureView.ScaleType.CENTER_CROP);
            if(FileUtil.isFileExist(contentPath)) {
                cardView.cardVideo.setDataSource(contentPath);
            }
        }

        cardView.findViewById(R.id.card_image_title).setVisibility(View.GONE);
        cardView.findViewById(R.id.card_image_title_edit).setVisibility(View.VISIBLE);

        cardView.cardTitleEdit.setOnEditorActionListener(cardTitleEditorActionListener);
        cardView.cardTitleEdit.addTextChangedListener(cardTitleTextWatcher);

        if (isCardEditing() && CardEditType.NAME.equals(editType)) {
            confirmButton.setVisibility(View.VISIBLE);
        }

        if(CardEditType.VOICE.equals(editType)){
            prepareVoiceEditing();
        } else {
            prepareNameEditing();
        }
    }

    private void prepareLayoutForVoiceRecord() {
        if (state == STATE_RECORD_NOT_COMPLETE) {
            recordUtil.stopRecord();
        }
        countHandler.removeCallbacks(countAction);

        mCountDown = INIT_COUNT;
        state = STATE_RECORD_NOT_COMPLETE;

        waitCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, COUNT_TEXT_SIZE);
        waitCount.setText(String.valueOf(INIT_COUNT));
        countScene.setVisibility(View.GONE);
        micButton.setEnabled(true);
        ttsButton.setEnabled(true);
        recordStopButton.setBackground(ResourcesUtil.getDrawable(getApplicationContext(), R.drawable.record_stop));
        recordStopButton.setVisibility(View.GONE);
        replayButton.setVisibility(View.GONE);
        rerecordButton.setVisibility(View.GONE);
    }

    private void prepareVoiceEditing() {
        cardView.cardTitle.setText(editCardModel.name);
        cardView.cardTitle.setVisibility(View.VISIBLE);
        cardView.cardTitleEdit.setVisibility(View.GONE);

        showRecodingGuideAndMicButton();
    }

    private void initCardData() {
        Intent intent = getIntent();
        editCardId = intent.getStringExtra(ApplicationConstants.EDIT_CARD_ID);

        if(isCardEditing()){
            editType = CardEditType.valueOf(intent.getStringExtra(ApplicationConstants.EDIT_TYPE));
            editCardModel = cardRepository.getSingleCard(editCardId);
            contentPath = editCardModel.contentPath;
            cardType = editCardModel.cardType;
        } else {
            contentPath = intent.getStringExtra(ContentsUtil.CONTENT_PATH);
            cardType = CardModel.CardType.valueOf(intent.getStringExtra(ContentsUtil.CARD_TYPE));
        }
    }
    private void prepareNameEditing() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardView.changeCardViewStatus();
                setCardViewMarginBeforeShowingKeyboard();
                showKeyboard();
            }
        }, 500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
            }
        }, 1000);
    }

    private void setCardViewMarginBeforeShowingKeyboard() {
        PercentRelativeLayout.LayoutParams layoutParams = ((PercentRelativeLayout.LayoutParams) cardView.getLayoutParams());
        layoutParams.setMargins(0, ResolutionUtil.getDpToPix(getApplicationContext(), -130), 0, 0);
        cardView.setLayoutParams(layoutParams);
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if(isSoftKeyboardShowing()) {
                setCardViewMarginBeforeShowingKeyboard();
            } else {
                removeCardViewMargin();
            }
        }

        private boolean isSoftKeyboardShowing() {
            Rect visibleAreaRect = new Rect();
            rootLayout.getWindowVisibleDisplayFrame(visibleAreaRect);
            return rootLayout.getHeight() - visibleAreaRect.height() > 0;
        }

        private void removeCardViewMargin() {
            PercentRelativeLayout.LayoutParams layoutParams = ((PercentRelativeLayout.LayoutParams) cardView.getLayoutParams());
            layoutParams.setMargins(0, 0, 0, 0);
            cardView.setLayoutParams(layoutParams);
        }
    };

    private void changeConfirmButtonByCardNameText() {
        if (checkValidationForText()) {
            confirmButton.setEnabled(true);
            confirmButton.setBackground(getDrawable(R.drawable.btn_complete));
        } else {
            confirmButton.setEnabled(false);
            confirmButton.setBackground(getDrawable(R.drawable.btn_check_disable));
        }
    }

    private TextView.OnEditorActionListener cardTitleEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (event == null && actionId == EditorInfo.IME_ACTION_DONE) {
                completeCardNameEditing();
            } else if (event != null) {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_ENTER:
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            completeCardNameEditing();
                        }
                        break;
                    default:
                        return false;
                }
            }
            return true;
        }
    };

    private TextWatcher cardTitleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            changeConfirmButtonByCardNameText();
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                if(cardTitleEditText.getText().toString().getBytes("euc-kr").length > 16){
                    s.delete(s.length()-1, s.length());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private void completeCardNameEditing() {
        if (checkValidationForText()) {
            hideKeyboard();
            if(!isCardEditing()){
                cardView.changeCardViewStatus();
                showRecodingGuideAndMicButton();
            }
        }
    }

    private void moveToCardViewPagerActivityAfterEditing() {
        Intent intent = new Intent(getApplicationContext(), CardViewPagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApplicationConstants.INTENT_KEY_CARD_EDITED, true);
        getApplicationContext().startActivity(intent);
    }

    private void moveToCardViewPagerActivity() {
        Intent intent = new Intent(getApplicationContext(), CardViewPagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApplicationConstants.INTENT_KEY_LIST_BACK, true);
        getApplicationContext().startActivity(intent);
    }

    private void playCardTitle() {
        changeCountingSceneForPlay();
        if(!Strings.isNullOrEmpty(voiceFilePath)) {
            playUtil.play(voiceFilePath);
        } else {
            playUtil.ttsSpeak(cardTitleEditText.getText().toString());
        }
    }

    private void changeCountingSceneForPlay() {
        waitCount.setText(R.string.check_recorded_voice);

        recordStopButton.setBackground(ResourcesUtil.getDrawable(getApplicationContext(), R.drawable.ic_check_button));
        replayButton.setVisibility(View.VISIBLE);
        rerecordButton.setVisibility(View.VISIBLE);

        state = STATE_RECORD_COMPLETE;
    }

    private void showRecodingGuideAndMicButton() {
        findViewById(R.id.mic_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.tts_btn).setVisibility(View.VISIBLE);
    }

    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(findViewById(R.id.card_image_title_edit).getWindowToken(), 0);
    }

    private void showKeyboard() {
        imm.showSoftInput(findViewById(R.id.card_image_title_edit), InputMethodManager.SHOW_FORCED);
    }

    private boolean checkValidationForText() {
        String cardTitle = ((EditText) cardView.findViewById(R.id.card_image_title_edit)).getText().toString();
        return cardTitle.trim().length() > 0;
    }

    private void saveCardAndMoveToNextActivity() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        CardModel cardModel = CardModel.builder()
                .name(cardView.cardTitleEdit.getText().toString())
                .contentPath(contentPath).voicePath(voiceFilePath)
                .firstTime(dateFormat.format(date))
                .categoryId(selectedCategoryId)
                .cardType(cardType)
                .thumbnailPath(cardType == CardModel.CardType.VIDEO_CARD ? ContentsUtil.getThumbnailPath(contentPath) : null)
                .hide(false).build();

        cardRepository.createSingleCardModel(cardModel);

        Intent intent = new Intent(MakeCardActivity.this, CardViewPagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ApplicationConstants.INTENT_KEY_NEW_CARD, true);
        startActivity(intent);
        finish();
    }

    private void startVoiceRecording() {
        voiceFilePath = RecordUtil.getMediaFilePath(this);
        waitCount.setText(R.string.talk_now);
        changeCountingSceneForRecoding();
        recordUtil.record(voiceFilePath, this);
    }

    private Runnable countAction = new Runnable() {
        @Override
        public void run() {
            if(--mCountDown == 0){
                startVoiceRecording();
            }
            else {
                waitCount.setText(Integer.toString(mCountDown));
                countHandler.postDelayed(this, COUNT_INTERVAL);
            }
        }
    };

    private boolean isCardEditing() {
        return !Strings.isNullOrEmpty(editCardId);
    }
}

