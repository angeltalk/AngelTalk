package act.angelman.presentation.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import javax.inject.Inject;

import act.angelman.AngelmanApplication;
import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.FontUtil;
import act.angelman.presentation.util.ResolutionUtil;

public class PhotoEditorActivity extends AbstractActivity {

    RequestManager glide;

    @Inject
    CardRepository cardRepository;

    private ImageView imageCapture;
    private ImageView confirmButton;
    private ImageView rotateButton;
    private View frameImage;
    private TextView pictureGuide;

    protected ScaleGestureDetector scaleGestureDetector;
    private float scale = 1f;
    private float px = 0, py = 0;
    private String editCardId;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.photo_edit_confirm:
                    hideViews();
                    String fileName = saveEditedImage();
                    if(editCardId == null) {
                        startMakeCardActivity(fileName);
                    } else {
                        modifyCardImage(fileName);
                        startCardViewPagerActivity();
                    }
                    showViews();
                    finish();
                    break;
                case R.id.rotate_image:
                    rotateImage();
                    break;
            }

        }
    };

    private void modifyCardImage(String imagePath) {
        ContentsUtil.deleteContentAndThumbnail(cardRepository.getSingleCard(editCardId).contentPath);
        cardRepository.updateSingleCardContent(editCardId, CardModel.CardType.PHOTO_CARD.getValue(), imagePath, null);
    }

    private void startCardViewPagerActivity() {
        Intent intent = new Intent(this, CardViewPagerActivity.class);
        intent.putExtra(ApplicationConstants.INTENT_KEY_CARD_EDITED, true);
        startActivity(intent);
    }

    private void rotateImage() {
        float rotationDegree = (imageCapture.getRotation() - 90f) % 360f;
        imageCapture.setRotation(rotationDegree);
    }


    private void startMakeCardActivity(String fileName) {
        Intent intent = new Intent(PhotoEditorActivity.this, MakeCardActivity.class);
        intent.putExtra(ContentsUtil.CONTENT_PATH, fileName);
        intent.putExtra(ContentsUtil.CARD_TYPE, CardModel.CardType.PHOTO_CARD.getValue());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_photo_editor);
        editCardId = getIntent().getStringExtra(ApplicationConstants.EDIT_CARD_ID);

        glide = Glide.with(this);

        imageCapture = (ImageView) findViewById(R.id.image_capture);
        confirmButton = (ImageView) findViewById(R.id.photo_edit_confirm);
        rotateButton = (ImageView) findViewById(R.id.rotate_image);
        frameImage = findViewById(R.id.camera_frame);
        pictureGuide = ((TextView) findViewById(R.id.picture_guide));
        pictureGuide.setTypeface(FontUtil.setFont(this, FontUtil.FONT_DEMILIGHT));

        setImageIntoImageCaptureView();

        scaleGestureDetector = new ScaleGestureDetector(this,new ScaleListener());

        confirmButton.setOnClickListener(onClickListener);
        rotateButton.setOnClickListener(onClickListener);
    }

    public String saveEditedImage() {
        String fileName = ContentsUtil.getImagePath();
        ContentsUtil.saveImage(getWindow().getDecorView(), fileName);
        return fileName;
    }

    private void hideViews() {
        frameImage.setVisibility(View.GONE);
        pictureGuide.setVisibility(View.GONE);
    }
    private void showViews() {
        frameImage.setVisibility(View.VISIBLE);
        pictureGuide.setVisibility(View.VISIBLE);
    }
    private void setImageIntoImageCaptureView() {
        Uri imagePath = getIntent().getParcelableExtra(ApplicationConstants.IMAGE_PATH_EXTRA);
        int loadingImageSize = ResolutionUtil.getDpToPix(getApplicationContext(), 360);

        glide.load(imagePath)
                .asBitmap()
                .override(loadingImageSize, loadingImageSize)
                .into(imageCapture);
    }

    boolean isScaling = false;
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            isScaling = true;
            float scaleFactor = detector.getScaleFactor() - 1;
            scale += scaleFactor;

            if(0.5f <= scale) {
                imageCapture.setScaleX(scale);
                imageCapture.setScaleY(scale);
            }
            return true;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                px = event.getX() - imageCapture.getX();
                py = event.getY() - imageCapture.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isScaling) {
                    float translateX = event.getX() - px;
                    float translateY = event.getY() - py;

                    imageCapture.setTranslationX(translateX);
                    imageCapture.setTranslationY(translateY);
                }
                break;
            case MotionEvent.ACTION_UP:
                px = 0;
                py = 0;
                isScaling = false;
                break;
            default:
                break;
        }
        scaleGestureDetector.onTouchEvent(event);

        return true;
    }
}
