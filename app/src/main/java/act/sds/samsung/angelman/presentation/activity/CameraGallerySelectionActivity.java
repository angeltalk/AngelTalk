package act.sds.samsung.angelman.presentation.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.custom.CardCategoryTitleRelativeLayout;
import act.sds.samsung.angelman.presentation.service.ScreenService;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

public class CameraGallerySelectionActivity extends AbstractActivity {

    private Intent screenService;
    private RelativeLayout cameraCard;
    private RelativeLayout galleryCard;

    public static String SCREEN_SERVICE_NAME = "ScreenService";
    private static final int SELECT_PICTURE = 1;

    CardCategoryTitleRelativeLayout titleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_gallery_selection);
        setCategoryBackground(R.id.camera_gallery_selection_container);

        titleLayout = (CardCategoryTitleRelativeLayout) findViewById(R.id.title_container);
        titleLayout.setCardCountVisible(View.GONE);

        setCameraGalleryIconColor();

        cameraCard = (RelativeLayout) findViewById(R.id.camera_start_card);
        galleryCard = (RelativeLayout) findViewById(R.id.gallery_start_card);

        cameraCard.setOnClickListener(onClickListener);
        galleryCard.setOnClickListener(onClickListener);

        screenService = new Intent(getApplicationContext(), ScreenService.class);
        if(!isServiceRunningCheck()) {
            startService(screenService);
        }
    }

    private void setCameraGalleryIconColor() {
        @ResourcesUtil.BackgroundColors
        int color = ((AngelmanApplication) getApplicationContext()).getCategoryModel().color;

        ImageView cameraIcon = (ImageView) findViewById(R.id.camera_start_icon);
        cameraIcon.setImageDrawable(ContextCompat.getDrawable(this, ResourcesUtil.getCameraIconBy(color)));

        ImageView galleryIcon = (ImageView) findViewById(R.id.gallery_start_icon);
        galleryIcon.setImageDrawable(ContextCompat.getDrawable(this, ResourcesUtil.getGalleryIconBy(color)));
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SCREEN_SERVICE_NAME.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.camera_start_card:
                    startNextActivity(Camera2Activity.class);
                    break;
                case R.id.gallery_start_card:
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_picture)), SELECT_PICTURE);
                default:
                    break;
            }
        }
    };

    private void startNextActivity(Class nextClass){
        Intent intent = new Intent(CameraGallerySelectionActivity.this, nextClass);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Intent intent = new Intent(CameraGallerySelectionActivity.this, PhotoEditorActivity.class);
            intent.putExtra(PhotoEditorActivity.IMAGE_PATH_EXTRA, data.getData());
            startActivity(intent);
        }
    }
}
