package act.angelman.presentation.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import javax.inject.Inject;

import act.angelman.BuildConfig;
import act.angelman.R;
import act.angelman.TestAngelmanApplication;
import act.angelman.UITest;
import act.angelman.domain.model.CategoryModel;
import act.angelman.presentation.manager.ApplicationManager;
import act.angelman.presentation.util.ResourcesUtil;

import static act.angelman.presentation.manager.ApplicationConstants.CAMERA_PERMISSION_FOR_PHOTO_REQUEST_CODE;
import static act.angelman.presentation.manager.ApplicationConstants.CAMERA_PERMISSION_FOR_VIDEO_REQUEST_CODE;
import static junit.framework.Assert.assertTrue;
import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk=22)
public class CameraGallerySelectionActivityTest extends UITest {

    @Inject
    ApplicationManager applicationManager;

    private CameraGallerySelectionActivity subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(applicationManager.getCategoryModel()).thenReturn(getCategoryModel());
        when(applicationManager.getCategoryModelColor()).thenReturn(getCategoryModelColor());
        subject = setupActivity(CameraGallerySelectionActivity.class);
    }

    @Test
    public void whenLaunched_thenHideCardCountTitleAndListCardButton() throws Exception {
        assertThat(subject.titleLayout.cardCount.getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.titleLayout.listCardButton.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void whenLaunchedActivity_thenShowCategoryName() throws Exception {
        assertThat(applicationManager.getCategoryModel().title).isEqualTo("먹을 것");
    }

    @Test
    public void whenLaunchedApp_thenSetBackgroundColorChangedToRelatedInCategory() throws Exception {
        assertThat(applicationManager.getCategoryModelColor()).isEqualTo(R.drawable.background_gradient_blue);
    }

    @Test
    public void whenCameraGallerySelectionViewLaunched_thenShowsCameraStartingCard() throws Exception {
        ImageView cameraIcon = (ImageView) subject.findViewById(R.id.image_camera);
        TextView cameraStartText = (TextView) subject.findViewById(R.id.camera_start_text);
        TextView cameraText = (TextView) subject.findViewById(R.id.text_take_picture);
        TextView galleryText = (TextView) subject.findViewById(R.id.text_select_gallery);
        TextView videoText = (TextView) subject.findViewById(R.id.text_take_video);

        assertThat(subject.cameraCard).isVisible();
        assertThat(cameraIcon).isVisible();
        assertThat(cameraStartText).hasText("카드에 사진이나 영상을\n 추가해주세요");
        assertThat(cameraText).hasText("사진 찍기");
        assertThat(galleryText).hasText("사진 선택");
        assertThat(videoText).hasText("동영상 촬영");
    }

    @Test
    public void whenOnRequestPermissionsWithCameraPermission_thenStartCameraActivity() throws Exception {
        setupActivity(CameraGallerySelectionActivity.class);

        int[] grantResults = {PackageManager.PERMISSION_GRANTED};
        subject.onRequestPermissionsResult(CAMERA_PERMISSION_FOR_PHOTO_REQUEST_CODE, null, grantResults);

        ShadowActivity shadowMainActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowMainActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(Camera2Activity.class.getCanonicalName());
    }

    @Test
    public void whenOnRequestPermissionsWithoutCameraPermission_thenDoNotStartCameraActivity() throws Exception {
        setupActivity(CameraGallerySelectionActivity.class);

        int[] grantResults = {PackageManager.PERMISSION_DENIED};
        subject.onRequestPermissionsResult(CAMERA_PERMISSION_FOR_PHOTO_REQUEST_CODE, null, grantResults);

        ShadowActivity shadowMainActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowMainActivity.getNextStartedActivity();
        assertThat(nextStartedActivity).isNull();
    }

    @Test
    public void whenClickedGalleryCard_thenStartDefaultGallery() throws Exception {
        subject.galleryCard.performClick();
        ShadowActivity shadowActivity = shadowOf(subject);
        ShadowActivity.IntentForResult nextIntentResult = shadowActivity.getNextStartedActivityForResult();
        assertThat(nextIntentResult.intent.getAction()).isEqualTo(Intent.ACTION_CHOOSER);
    }

    @Test
    public void whenOnRequestPermissionWithCameraPermission_thenStartVideoActivity() throws  Exception {
        setupActivity(CameraGallerySelectionActivity.class);

        int[] grantResults = {PackageManager.PERMISSION_GRANTED};
        subject.onRequestPermissionsResult(CAMERA_PERMISSION_FOR_VIDEO_REQUEST_CODE, null, grantResults);

        ShadowActivity shadowMainActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowMainActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(VideoActivity.class.getCanonicalName());
    }

    @Test
    public void whenOnRequestPermissionWithoutCameraPermission_thenStartVideoActivity() throws  Exception {
        setupActivity(CameraGallerySelectionActivity.class);

        int[] grantResults = {PackageManager.PERMISSION_DENIED};
        subject.onRequestPermissionsResult(CAMERA_PERMISSION_FOR_VIDEO_REQUEST_CODE, null, grantResults);

        ShadowActivity shadowMainActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowMainActivity.getNextStartedActivity();
        assertThat(nextStartedActivity).isNull();
    }

    @Test
    public void whenClickedBackButton_thenFinishesCardViewPagerActivity() throws Exception {
        ImageView backButton = (ImageView) subject.findViewById(R.id.back_button);
        assertThat(backButton).isVisible();
        backButton.performClick();
        ShadowActivity activityShadow = shadowOf(subject);
        assertTrue(activityShadow.isFinishing());
    }

    private CategoryModel getCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.index = 0;
        categoryModel.color = ResourcesUtil.BLUE;
        categoryModel.title = "먹을 것";
        return categoryModel;
    }

    private Integer getCategoryModelColor() {
        return ResourcesUtil.getCardViewLayoutBackgroundBy(getCategoryModel().color);
    }

}