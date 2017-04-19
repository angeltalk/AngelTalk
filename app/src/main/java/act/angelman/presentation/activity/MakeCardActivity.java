package act.angelman.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.percent.PercentRelativeLayout;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import act.angelman.AngelmanApplication;
import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.presentation.custom.CardView;
import act.angelman.presentation.custom.FontTextView;
import act.angelman.presentation.custom.VideoCardTextureView;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.manager.ApplicationManager;
import act.angelman.presentation.util.AngelManGlideTransform;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.FileUtil;
import act.angelman.presentation.util.FontUtil;
import act.angelman.presentation.util.PlayUtil;
import act.angelman.presentation.util.RecordUtil;
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
    protected int state = STATE_RECORD_NOT_COMPLETE;
    protected InputMethodManager imm;

    private String contentPath;
    private RequestManager glide;

    private int selectedCategoryId;
    private String voiceFile;
    private RelativeLayout.LayoutParams params;
    private Handler countHandler = new Handler();
    private CardModel.CardType cardType;

    CardView cardView;
    RecordUtil recordUtil = RecordUtil.getInstance();
    PlayUtil playUtil;

    @Inject
    CardRepository cardRepository;

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.record_stop_button)
    Button recordStopButton;

    @BindView(R.id.mic_btn)
    Button micButton;

    @BindView(R.id.waiting_count)
    FontTextView waitCount;

    @BindView(R.id.counting_scene)
    PercentRelativeLayout countScene;

    @BindView(R.id.retake_button)
    Button retakeButton;

    @BindView(R.id.replay_button)
    Button replayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_make_card);
        ButterKnife.bind(this);

        glide = Glide.with(this);

        applicationManager.setCategoryBackground(
                findViewById(R.id.show_card_layout),
                applicationManager.getCategoryModelColor()
        );

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Intent intent = getIntent();
        selectedCategoryId = applicationManager.getCategoryModel().index;

        playUtil = PlayUtil.getInstance();
        contentPath = intent.getStringExtra(ContentsUtil.CONTENT_PATH);
        cardType = CardModel.CardType.valueOf(intent.getStringExtra(ContentsUtil.CARD_TYPE));

        cardView = (CardView) findViewById(R.id.card_view_layout);
        cardView.cardImage.setVisibility(View.VISIBLE);
        cardView.setCardViewLayoutMode(CardView.MODE_MAKE_CARD);
        if(cardType.equals(CardModel.CardType.PHOTO_CARD)) {
            cardView.cardVideo.setVisibility(View.GONE);
            cardView.playButton.setVisibility(View.GONE);
            Glide.with(getApplicationContext()).load(ContentsUtil.getContentFile(contentPath)).override(280, 280).bitmapTransform(new AngelManGlideTransform(this, 10, 0, AngelManGlideTransform.CornerType.TOP)).into(cardView.cardImage);
        } else if (cardType.equals(CardModel.CardType.VIDEO_CARD)) {
            cardView.cardVideo.setVisibility(View.VISIBLE);

            glide.load(ContentsUtil.getContentFile(ContentsUtil.getThumbnailPath(contentPath)))
                    .bitmapTransform(new AngelManGlideTransform(this, 10, 0, AngelManGlideTransform.CornerType.TOP))
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

        cardView.cardTitleEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event == null && actionId == EditorInfo.IME_ACTION_DONE) {
                    if (checkValidationForText()) {
                        cardView.changeCardViewStatus();
                        hideKeyboard();
                        showRecodingGuideAndMicButton();
                    }
                } else if (event != null) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_ENTER:
                            if (event.getAction() == KeyEvent.ACTION_UP) {
                                if (checkValidationForText()) {
                                    cardView.changeCardViewStatus();
                                    hideKeyboard();
                                    showRecodingGuideAndMicButton();
                                }
                            }
                            break;
                        default:
                            return false;
                    }
                }
                return true;
            }
        });

        findViewById(R.id.card_image_title).setVisibility(View.GONE);
        findViewById(R.id.card_image_title_edit).setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardView.changeCardViewStatus();
                showKeyboard();
            }
        }, 500);

        params = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
    }

    @OnClick(R.id.record_stop_button)
    public void onClickRecStopButton(View view){
        if (state == STATE_RECORD_NOT_COMPLETE) {
            recordUtil.stopRecord();
            playRecordVoiceFile();
        }
        else saveCardAndMoveToNextActivity();
    }

    @OnClick(R.id.mic_btn)
    public void onClickMicButton(View view){
                countScene.setVisibility(View.VISIBLE);
                view.setEnabled(false);
                countHandler.postDelayed(countAction, COUNT_INTERVAL);
    }

    @OnClick(R.id.replay_button)
    public void onClickReplayButton(View view){
        playRecordVoiceFile();
    }

    private void playRecordVoiceFile() {
        waitCount.setText(R.string.check_recorded_voice);
        waitCount.setFontType(FontUtil.FONT_REGULAR);

        recordStopButton.setBackground(ResourcesUtil.getDrawable(getApplicationContext(), R.drawable.ic_check_button));
        replayButton.setVisibility(View.VISIBLE);
        retakeButton.setVisibility(View.VISIBLE);

        playUtil.play(voiceFile);
        state = STATE_RECORD_COMPLETE;
    }



    private void showRecodingGuideAndMicButton() {
        findViewById(R.id.recoding_guide).setVisibility(View.VISIBLE);
        findViewById(R.id.mic_btn).setVisibility(View.VISIBLE);
    }

    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(findViewById(R.id.card_image_title_edit).getWindowToken(), 0);

    }

    private void showKeyboard() {
        imm.showSoftInput(findViewById(R.id.card_image_title_edit), InputMethodManager.SHOW_FORCED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideKeyboard();

        recordUtil.stopRecord();
        playUtil.playStop();
    }

    @Override
    public void onBackPressed() {
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
            if (state == STATE_RECORD_NOT_COMPLETE) {
                recordUtil.stopRecord();
            }
            countHandler.removeCallbacks(countAction);

            mCountDown = INIT_COUNT;

            waitCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, COUNT_TEXT_SIZE);
            waitCount.setText(String.valueOf(INIT_COUNT));

            if(state == STATE_RECORD_COMPLETE)
                state = STATE_RECORD_NOT_COMPLETE;

            countScene.setVisibility(View.GONE);
            micButton.setEnabled(true);
            recordStopButton.setBackground(ResourcesUtil.getDrawable(getApplicationContext(), R.drawable.record_stop));
            recordStopButton.setVisibility(View.GONE);
            replayButton.setVisibility(View.GONE);
            retakeButton.setVisibility(View.GONE);
            playUtil.playStop();
        }
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
                .contentPath(contentPath).voicePath(voiceFile)
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
        voiceFile = RecordUtil.getMediaFilePath();

        waitCount.setText(R.string.talk_now);
        waitCount.setFontType(FontUtil.FONT_REGULAR);
        waitCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);

        recordStopButton.setVisibility(View.VISIBLE);
        recordUtil.record(voiceFile, this);
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

    @Override
    public void afterRecord() {
        playRecordVoiceFile();
    }
}

