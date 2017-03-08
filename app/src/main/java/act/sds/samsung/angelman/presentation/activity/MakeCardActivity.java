package act.sds.samsung.angelman.presentation.activity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.presentation.custom.CardView;
import act.sds.samsung.angelman.presentation.custom.FontEditText;
import act.sds.samsung.angelman.presentation.custom.FontTextView;
import act.sds.samsung.angelman.presentation.util.AngelManGlideTransform;
import act.sds.samsung.angelman.presentation.util.FontUtil;
import act.sds.samsung.angelman.presentation.util.ImageUtil;
import act.sds.samsung.angelman.presentation.util.PlayUtil;
import act.sds.samsung.angelman.presentation.util.RecordUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class MakeCardActivity extends AbstractActivity implements RecordUtil.RecordCallback {

    private static final int STATE_RECORD_NOT_COMPLETE = 0;
    private static final int STATE_RECORD_COMPLETE = 1;
    private static final int INIT_COUNT = 3;
    private static final int COUNT_TEXT_SIZE = 60;
    public static final long COUNT_INTERVAL = 1000;

    protected int state = STATE_RECORD_NOT_COMPLETE;

    private String imagePath;

    protected InputMethodManager imm;

    private int selectedCategoryId;

    private int mCountDown = INIT_COUNT;

    private Handler countHandler = new Handler();
    RecordUtil recordUtil = RecordUtil.getInstance();

    PlayUtil playUtil;
    private String voiceFile;

    @BindView(R.id.card_view_layout)
    public CardView cardView;

    @BindView(R.id.waiting_count)
    public FontTextView waitCountTextView;

    @BindView(R.id.record_stop_btn)
    public Button recordStopButton;

    @BindView(R.id.mic_btn)
    public Button micButton;

    @BindView(R.id.counting_scene)
    public PercentRelativeLayout countSceneLayout;

    @BindView(R.id.card_image_title_edit)
    public FontEditText imageTitleEdit;

    @Inject
    CardRepository cardRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_card);
        ButterKnife.bind(this);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        setCategoryBackground(R.id.show_card_layout);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Intent intent = getIntent();
        selectedCategoryId = ((AngelmanApplication) getApplicationContext()).getCategoryModel().index;

        playUtil = PlayUtil.getInstance();
        imagePath = intent.getStringExtra(ImageUtil.IMAGE_PATH);

        cardView.setCardViewLayoutMode(CardView.CardViewMode.CARD_MAKE_MODE);
        Glide.with(getApplicationContext()).load(new File(imagePath)).override(280, 280).bitmapTransform(new AngelManGlideTransform(this, 10, 0, AngelManGlideTransform.CornerType.TOP)).into(cardView.getCardImage());

        Handler handler = new Handler();

        findViewById(R.id.card_image_title).setVisibility(View.GONE);
        findViewById(R.id.card_image_title_edit).setVisibility(View.VISIBLE);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardView.changeCardViewStatus();
                showKeyboard();
            }
        }, 500);
    }

    private void playRecordVoiceFile() {
        waitCountTextView.setText(R.string.check_recorded_voice);
        waitCountTextView.setFontType(FontUtil.FONT_REGULAR);
        recordStopButton.setBackground(getResources().getDrawable(R.drawable.ic_check_button));
        playUtil.play(voiceFile);
        state = STATE_RECORD_COMPLETE;
    }

    private void showRecodingGuideAndMicButton() {
        findViewById(R.id.recoding_guide).setVisibility(View.VISIBLE);
        findViewById(R.id.mic_btn).setVisibility(View.VISIBLE);
    }

    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(imageTitleEdit.getWindowToken(), 0);

    }


    private void showKeyboard() {
        imm.showSoftInput(imageTitleEdit, InputMethodManager.SHOW_FORCED);
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
        if (countSceneLayout.getVisibility() == View.GONE) {
            switch (cardView.getStatus()) {
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

            waitCountTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, COUNT_TEXT_SIZE);
            waitCountTextView.setText(String.valueOf(INIT_COUNT));

            if (state == STATE_RECORD_COMPLETE) {
                state = STATE_RECORD_NOT_COMPLETE;
            }

            countSceneLayout.setVisibility(View.GONE);
            micButton.setEnabled(true);
            recordStopButton.setBackground(getResources().getDrawable(R.drawable.record_stop));
            recordStopButton.setVisibility(View.GONE);

        }

    }

    @OnClick(R.id.mic_btn)
    public void onClickMicButton(View v) {
        countSceneLayout.setVisibility(View.VISIBLE);
        micButton.setEnabled(false);
        countHandler.postDelayed(countAction, COUNT_INTERVAL);
    }

    @OnClick(R.id.record_stop_btn)
    public void onClickRecordStopButton(View v) {
        if (state == STATE_RECORD_NOT_COMPLETE) {
            recordUtil.stopRecord();
            playRecordVoiceFile();
        } else saveCardAndMoveToNextActivity();
    }

    @OnEditorAction(R.id.card_image_title_edit)
    public boolean onEditorActionImageTitleEdit(TextView v, int actionId, KeyEvent event) {
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

    private boolean checkValidationForText() {
        String cardTitle = ((EditText) cardView.findViewById(R.id.card_image_title_edit)).getText().toString();
        return cardTitle.trim().length() > 0;
    }

    private void saveCardAndMoveToNextActivity() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        CardModel cardModel = new CardModel(cardView.getCardTitleEditText().getText().toString(),
                imagePath,
                voiceFile,
                dateFormat.format(date),
                selectedCategoryId);

        cardRepository.createSingleCardModel(cardModel);

        Intent intent = new Intent(MakeCardActivity.this, CardViewPagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(CardViewPagerActivity.INTENT_KEY_NEW_CARD, true);
        startActivity(intent);
        finish();
    }

    private void startVoiceRecording() {
        voiceFile = RecordUtil.getMediaFilePath(this);
        waitCountTextView.setText(R.string.talk_now);
        waitCountTextView.setFontType(FontUtil.FONT_REGULAR);
        waitCountTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        recordStopButton.setVisibility(View.VISIBLE);
        recordUtil.record(voiceFile, this);
    }

    private Runnable countAction = new Runnable() {
        @Override
        public void run() {
            if (--mCountDown == 0) {
                startVoiceRecording();
            } else {
                waitCountTextView.setText(Integer.toString(mCountDown));
                countHandler.postDelayed(this, COUNT_INTERVAL);
            }
        }
    };

    @Override
    public void afterRecord() {
        playRecordVoiceFile();
    }
}

