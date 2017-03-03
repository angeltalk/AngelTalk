package act.sds.samsung.angelman.presentation.activity;

import android.content.Intent;
import android.os.Build;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static junit.framework.Assert.assertTrue;
import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CameraGallerySelectionActivityTest extends UITest {

    private CameraGallerySelectionActivity subject;

    @Before
    public void setUp() throws Exception {
        setCategoryModel();
        subject = setupActivity(CameraGallerySelectionActivity.class);
    }

    @Test
    public void whenLaunchedActivity_thenShowCategoryName() throws Exception {
        assertThat(((TextView) subject.findViewById(R.id.category_item_title)).getText()).isEqualTo("먹을 것");
    }

    @Test
    public void whenLaunchedApp_thenSetBackgroundColorChangedToRelatedInCategory() throws Exception {
        assertThat(shadowOf(subject.findViewById(R.id.camera_gallery_selection_container).getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.background_gradient_blue);
    }

    @Test
    public void whenCameraGallerySelectionViewLaunched_thenShowsCameraStartingCard() throws Exception {
        RelativeLayout cameraCard = (RelativeLayout) subject.findViewById(R.id.camera_start_card);
        ImageView cameraIcon = (ImageView) subject.findViewById(R.id.camera_start_icon);
        TextView cameraStartText = (TextView) subject.findViewById(R.id.camera_start_text);
        TextView cameraText = (TextView) subject.findViewById(R.id.camera_text);
        TextView galleryText = (TextView) subject.findViewById(R.id.gallery_text);

        assertThat(cameraCard).isVisible();
        assertThat(cameraIcon).isVisible();
        assertThat(cameraStartText).hasText("카드에 사진을 추가해주세요");
        assertThat(cameraText).hasText("카메라");
        assertThat(galleryText).hasText("갤러리");
    }

    @Test
    public void whenCameraGallerySelectionViewLaunched_thenShowCameraAndGalleryIconRelatedCategoryColor() throws Exception {
        ImageView cameraIcon = (ImageView) subject.findViewById(R.id.camera_start_icon);
        ImageView galleryIcon = (ImageView) subject.findViewById(R.id.gallery_start_icon);

        ShadowDrawable shadowCameraDrawable = shadowOf(cameraIcon.getDrawable());
        ShadowDrawable shadowGalleryDrawable = shadowOf(galleryIcon.getDrawable());

        assertThat(shadowCameraDrawable.getCreatedFromResId()).isEqualTo(R.drawable.ic_camera_blue);
        assertThat(shadowGalleryDrawable.getCreatedFromResId()).isEqualTo(R.drawable.ic_gallery_blue);
    }

    @Test
    public void givenAndroidVersionAboveLollipop_whenClickedCameraCard_thenStartCameraTwoActivity() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 23);
        setupActivity(CameraGallerySelectionActivity.class);

        RelativeLayout cameraCard = (RelativeLayout) subject.findViewById(R.id.camera_start_card);
        cameraCard.performClick();

        ShadowActivity shadowMainActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowMainActivity.getNextStartedActivity();

        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(Camera2Activity.class.getCanonicalName());

    }

    @Test
    public void whenClickedGalleryCard_thenStartDefaultGallery() throws Exception {
        subject.findViewById(R.id.gallery_start_card).performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        ShadowActivity.IntentForResult nextIntentResult = shadowActivity.getNextStartedActivityForResult();

        assertThat(nextIntentResult.intent.getAction()).isEqualTo(Intent.ACTION_CHOOSER);
    }

    @Test
    public void whenClickedBackButton_thenFinishesCardViewPagerActivity() throws Exception {
        ImageView backButton = (ImageView) subject.findViewById(R.id.back_button);

        assertThat(backButton).isVisible();
        backButton.performClick();

        ShadowActivity activityShadow = shadowOf(subject);
        assertTrue(activityShadow.isFinishing());
    }

    private void setCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.color = ResourcesUtil.BLUE;
        categoryModel.title = "먹을 것";
        ((AngelmanApplication) RuntimeEnvironment.application).setCategoryModel(categoryModel);
    }

}