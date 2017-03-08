package act.sds.samsung.angelman.presentation.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.util.FontUtil;
import act.sds.samsung.angelman.presentation.util.ImageUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoEditorActivity extends AbstractActivity {
    public static final String IMAGE_PATH_EXTRA = "imagePath";

    @BindView(R.id.image_capture)
    public ImageView imageCapture;

    @BindView(R.id.camera_frame)
    public View frameImage;

    @BindView(R.id.picture_guide)
    public TextView pictureGuide;

    protected ScaleGestureDetector scaleGestureDetector;
    private Float scale = 1f;
    private float px = 0, py = 0;
    private ImageUtil imageUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        ButterKnife.bind(this);

        pictureGuide.setTypeface(FontUtil.setFont(this, FontUtil.FONT_DEMILIGHT));

        setImageIntoImageCaptureView();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        imageUtil = ImageUtil.getInstance();
    }

    @OnClick({R.id.photo_edit_confirm, R.id.rotate_image})
    void onClickButton(View v){
        switch (v.getId()) {
            case R.id.photo_edit_confirm:
                hideViews();
                String fileName = saveEditedImage();
                startShowCardActivity(fileName);
                showViews();
                finish();
                break;
            case R.id.rotate_image:
                rotateImage();
                break;
        }
    }

    private void rotateImage() {
        float rotationDegree = (imageCapture.getRotation() - 90f) % 360f;
        imageCapture.setRotation(rotationDegree);
    }


    private void startShowCardActivity(String fileName) {
        Intent intent = new Intent(PhotoEditorActivity.this, MakeCardActivity.class);
        intent.putExtra(ImageUtil.IMAGE_PATH, fileName);
        startActivity(intent);
    }


    public String saveEditedImage() {
        String fileName = imageUtil.getImagePath(PhotoEditorActivity.this);
        imageUtil.saveImage(getWindow().getDecorView(), fileName);
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
        Uri imagePath = getIntent().getParcelableExtra(IMAGE_PATH_EXTRA);
        imageCapture.setImageURI(imagePath);
    }

    boolean isScaling = false;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            isScaling = true;
            float scaleFactor = detector.getScaleFactor() - 1;
            scale += scaleFactor;

            if (0.5f <= scale) {
                imageCapture.setScaleX(scale);
                imageCapture.setScaleY(scale);
            }
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                px = event.getX() - imageCapture.getX();
                py = event.getY() - imageCapture.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isScaling) {
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
