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
import android.widget.RelativeLayout;
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
import act.sds.samsung.angelman.presentation.custom.FontTextView;
import act.sds.samsung.angelman.presentation.util.AngelManGlideTransform;
import act.sds.samsung.angelman.presentation.util.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.FontUtil;
import act.sds.samsung.angelman.presentation.util.ImageUtil;
import act.sds.samsung.angelman.presentation.util.PlayUtil;
import act.sds.samsung.angelman.presentation.util.RecordUtil;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

public class MakeCardActivity extends AbstractActivity implements RecordUtil.RecordCallback {

    private static final int STATE_RECORD_NOT_COMPLETE = 0;
    private static final int STATE_RECORD_COMPLETE = 1;
    private static final int INIT_COUNT = 3;
    private static final int COUNT_TEXT_SIZE = 60;
    public static final long COUNT_INTERVAL = 1000;

    protected int state = STATE_RECORD_NOT_COMPLETE;

    private String contentPath;

    CardView cardView;
    protected InputMethodManager imm;
    private int selectedCategoryId;

    private FontTextView waitCount;
    private int mCountDown = INIT_COUNT;
    private Handler countHandler = new Handler();

    RecordUtil recordUtil = RecordUtil.getInstance();
    PlayUtil playUtil;
    private String voiceFile;
    private RelativeLayout.LayoutParams params;

    Button recordStopBtn;
    private Button micButton;
    private PercentRelativeLayout countScene;

    @Inject
    CardRepository cardRepository;

    @Inject
    ApplicationManager applicationManager;
    private CardModel.CardType cardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_card);

        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        applicationManager.setCategoryBackground(
                findViewById(R.id.show_card_layout),
                applicationManager.getCategoryModelColor()
        );

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Intent intent = getIntent();
        selectedCategoryId = applicationManager.getCategoryModel().index;

        playUtil = PlayUtil.getInstance();
        contentPath = intent.getStringExtra(ImageUtil.CONTENT_PATH);
        cardType = CardModel.CardType.valueOf(intent.getStringExtra(ImageUtil.CARD_TYPE));

        cardView = (CardView) findViewById(R.id.card_view_layout);
        cardView.setCardViewLayoutMode(CardView.MODE_MAKE_CARD);
        if(cardType.equals(CardModel.CardType.PHOTO_CARD)) {
            Glide.with(getApplicationContext()).load(new File(contentPath)).override(280, 280).bitmapTransform(new AngelManGlideTransform(this, 10, 0, AngelManGlideTransform.CornerType.TOP)).into(cardView.cardImage);
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

        waitCount = (FontTextView) findViewById(R.id.waiting_count);
        micButton = (Button) findViewById(R.id.mic_btn);
        countScene = (PercentRelativeLayout) findViewById(R.id.counting_scene);

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countScene.setVisibility(View.VISIBLE);
                micButton.setEnabled(false);
                countHandler.postDelayed(countAction, COUNT_INTERVAL);
            }
        });

        recordStopBtn = (Button) findViewById(R.id.record_stop_btn);
        recordStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE_RECORD_NOT_COMPLETE) {
                    recordUtil.stopRecord();
                    playRecordVoiceFile();
                }
                else saveCardAndMoveToNextActivity();
            }
        });

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

        params = (RelativeLayout.LayoutParams) cardView.getLayoutParams();

    }

    private void playRecordVoiceFile() {
        waitCount.setText(R.string.check_recorded_voice);
        waitCount.setFontType(FontUtil.FONT_REGULAR);

        recordStopBtn.setBackground(ResourcesUtil.getDrawable(getApplicationContext(), R.drawable.ic_check_button));

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
            recordStopBtn.setBackground(ResourcesUtil.getDrawable(getApplicationContext(), R.drawable.record_stop));
            recordStopBtn.setVisibility(View.GONE);

        }

    }

    private boolean checkValidationForText() {
        String cardTitle = ((EditText) cardView.findViewById(R.id.card_image_title_edit)).getText().toString();
        return cardTitle.trim().length() > 0;
    }

    private void saveCardAndMoveToNextActivity() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        CardModel cardModel = new CardModel(cardView.cardTitleEdit.getText().toString(),
                contentPath,
                voiceFile,
                dateFormat.format(date),
                selectedCategoryId,
                cardType);

        cardRepository.createSingleCardModel(cardModel);

        Intent intent = new Intent(MakeCardActivity.this, CardViewPagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(CardViewPagerActivity.INTENT_KEY_NEW_CARD, true);
        startActivity(intent);
        finish();
    }

    private void startVoiceRecording() {
        voiceFile = RecordUtil.getMediaFilePath();

        waitCount.setText(R.string.talk_now);
        waitCount.setFontType(FontUtil.FONT_REGULAR);
        waitCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);

        recordStopBtn.setVisibility(View.VISIBLE);
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

