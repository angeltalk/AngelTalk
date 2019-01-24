package angeltalk.plus.presentation.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import angeltalk.plus.AngelmanApplication;
import angeltalk.plus.R;
import angeltalk.plus.presentation.custom.CardTitleLayout;
import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.manager.ApplicationManager;
import angeltalk.plus.presentation.util.ResourcesUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static angeltalk.plus.presentation.manager.ApplicationConstants.CAMERA_PERMISSION_FOR_PHOTO_REQUEST_CODE;
import static angeltalk.plus.presentation.manager.ApplicationConstants.CAMERA_PERMISSION_FOR_VIDEO_REQUEST_CODE;

public class CameraGallerySelectionActivity extends AbstractActivity {

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.layout_camera)
    public RelativeLayout cameraCard;

    @BindView(R.id.layout_gallery)
    public RelativeLayout galleryCard;

    @BindView(R.id.layout_video)
    public RelativeLayout videoCard;

    @BindView(R.id.title_container)
    CardTitleLayout titleLayout;

    private static final int SELECT_PICTURE = 1;
    private String editCardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        ResourcesUtil.setColorTheme(this, applicationManager.getCategoryModelColor());
        setContentView(R.layout.activity_camera_gallery_selection);
        ButterKnife.bind(this);

        applicationManager.setCategoryBackground(
                findViewById(R.id.camera_gallery_selection_container),
                applicationManager.getCategoryModelColor()
        );
        titleLayout.hideCardCountText();
        titleLayout.hideListCardButton();

        editCardId = getIntent().getStringExtra(ApplicationConstants.EDIT_CARD_ID);
        if(editCardId == null) {
            titleLayout.setCategoryModelTitle(applicationManager.getCategoryModel().title);
        } else {
            titleLayout.setCategoryModelTitle(getString(R.string.card_edit_title));
            ((TextView) findViewById(R.id.camera_start_text)).setText(R.string.edit_content_guide_text);
        }
    }

    @OnClick({R.id.layout_camera})
    public void onClickCamera(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_FOR_PHOTO_REQUEST_CODE);
        } else {
            moveToCamera2Activity();
        }
    }

    @OnClick({R.id.layout_gallery})
    public void onClickGallery(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_picture)), SELECT_PICTURE);
    }

    @OnClick({R.id.layout_video})
    public void onClickVideo(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, CAMERA_PERMISSION_FOR_VIDEO_REQUEST_CODE);
        } else {
            moveToVideoActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_FOR_PHOTO_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moveToCamera2Activity();
                } else {
                    return;
                }
                break;
            case CAMERA_PERMISSION_FOR_VIDEO_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    moveToVideoActivity();
                } else {
                    return;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Intent intent = new Intent(CameraGallerySelectionActivity.this, PhotoEditorActivity.class);
            intent.putExtra(ApplicationConstants.IMAGE_PATH_EXTRA, data.getData());
            intent.putExtra(ApplicationConstants.EDIT_CARD_ID, editCardId);
            startActivity(intent);
        }
    }

    private void moveToCamera2Activity() {
        Intent intent = new Intent(this, Camera2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ApplicationConstants.EDIT_CARD_ID, editCardId);
        startActivity(intent);
    }

    private void moveToVideoActivity() {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ApplicationConstants.EDIT_CARD_ID, editCardId);
        startActivity(intent);
    }
}
