package act.sds.samsung.angelman.presentation.activity;

import android.content.Intent;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowDrawable;
import org.robolectric.util.ReflectionHelpers;

import javax.inject.Inject;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static junit.framework.Assert.assertTrue;
import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
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
    public void whenCameraGallerySelectionViewLaunched_thenShowCameraAndGalleryIconRelatedCategoryColor() throws Exception {
        ImageView cameraIcon = (ImageView) subject.findViewById(R.id.image_camera);
        ImageView galleryIcon = (ImageView) subject.findViewById(R.id.image_gallery);
        ImageView videoIcon = (ImageView) subject.findViewById(R.id.image_video);

        ShadowDrawable shadowCameraDrawable = shadowOf(cameraIcon.getDrawable());
        ShadowDrawable shadowGalleryDrawable = shadowOf(galleryIcon.getDrawable());
        ShadowDrawable shadowVideoDrawable = shadowOf(videoIcon.getDrawable());

        assertThat(shadowCameraDrawable.getCreatedFromResId()).isEqualTo(R.drawable.ic_camera_blue);
        assertThat(shadowGalleryDrawable.getCreatedFromResId()).isEqualTo(R.drawable.ic_gallery_blue);
        assertThat(shadowVideoDrawable.getCreatedFromResId()).isEqualTo(R.drawable.ic_video_blue);
    }

    @Test
    public void whenClickedCameraCard_thenStartCameraActivity() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 23);
        setupActivity(CameraGallerySelectionActivity.class);
        subject.cameraCard.performClick();
        ShadowActivity shadowMainActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowMainActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(Camera2Activity.class.getCanonicalName());
    }

    @Test
    public void whenClickedGalleryCard_thenStartDefaultGallery() throws Exception {
        subject.galleryCard.performClick();
        ShadowActivity shadowActivity = shadowOf(subject);
        ShadowActivity.IntentForResult nextIntentResult = shadowActivity.getNextStartedActivityForResult();
        assertThat(nextIntentResult.intent.getAction()).isEqualTo(Intent.ACTION_CHOOSER);
    }

    @Test
    public void whenClickedVideoCard_thenStartVideoActivity() throws  Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 23);
        setupActivity(CameraGallerySelectionActivity.class);
        subject.videoCard.performClick();
        ShadowActivity shadowMainActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowMainActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(VideoActivity.class.getCanonicalName());
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
        categoryModel.color = ResourcesUtil.BLUE;
        categoryModel.title = "먹을 것";
        return categoryModel;
    }

    private Integer getCategoryModelColor() {
        return ResourcesUtil.getCardViewLayoutBackgroundBy(getCategoryModel().color);
    }

}