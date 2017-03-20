package act.sds.samsung.angelman.presentation.activity;


import android.content.Intent;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowDrawable;
import org.robolectric.shadows.ShadowScaleGestureDetector;

import java.io.File;
import java.lang.reflect.Field;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.presentation.util.ImageUtil;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PhotoEditorActivityTest extends UITest{

    private PhotoEditorActivity subject;

    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(PhotoEditorActivity.IMAGE_PATH_EXTRA, Uri.fromFile(new File("../../res/drawable-hdpi/ic_camera.png")));
        subject = setupActivityWithIntent(PhotoEditorActivity.class, intent);
    }

    @Test
    public void whenLaunched_thenShouldGetImagePathFromIntent() throws Exception {
        Intent intent = subject.getIntent();

        assertThat(intent).hasExtra(PhotoEditorActivity.IMAGE_PATH_EXTRA);
        assertThat(intent.getParcelableExtra(PhotoEditorActivity.IMAGE_PATH_EXTRA)).isNotNull();
    }

    @Test
    public void whenLaunched_thenImageSettingsCorrectly() throws Exception {
        ImageView imageCapture = (ImageView) subject.findViewById(R.id.image_capture);

        ShadowDrawable shadowDrawable = shadowOf(imageCapture.getDrawable());

        assertThat(shadowDrawable.getInputStream().toString()).contains("ic_camera.png");
    }

    @Test
    public void whenDragImage_thenTranslatePosition() throws Exception {

        subject.dispatchTouchEvent(MotionEvent.obtain(1, 1, MotionEvent.ACTION_DOWN, 50, 50, 0));
        subject.dispatchTouchEvent(MotionEvent.obtain(1, 1, MotionEvent.ACTION_MOVE, 100, 100, 0));
        subject.dispatchTouchEvent(MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 100, 100, 0));

        ImageView imageView = (ImageView) subject.findViewById(R.id.image_capture);

        assertThat(imageView.getTranslationX()).isEqualTo(50);
        assertThat(imageView.getTranslationY()).isEqualTo(50);
    }

    @Test
    public void whenPinchZoomOutImage_thenScaleTheImage() throws Exception {
        ScaleGestureDetector detectorMock = mock(ScaleGestureDetector.class);
        when(detectorMock.getScaleFactor()).thenReturn(1.2f);

        ShadowScaleGestureDetector detector = shadowOf(subject.scaleGestureDetector);
        detector.getListener().onScale(detectorMock);

        ImageView imageView = (ImageView) subject.findViewById(R.id.image_capture);
        assertThat(imageView.getScaleX()).isEqualTo(1.2f);
        assertThat(imageView.getScaleY()).isEqualTo(1.2f);
    }

    @Test
    public void whenClickConfirmButton_thenSaveCroppedImageAndShowCardView() throws Exception {

        ImageUtil mockUtil = mock(ImageUtil.class);

        Field imageUtilField = PhotoEditorActivity.class.getDeclaredField("imageUtil");
        imageUtilField.setAccessible(true);
        imageUtilField.set(subject, mockUtil);

        when(mockUtil.getImagePath()).thenReturn("test file name");

        subject.findViewById(R.id.photo_edit_confirm).performClick();

        verify(mockUtil).saveImage(any(View.class), eq("test file name"));

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(MakeCardActivity.class.getCanonicalName());
        assertThat(shadowActivity.isFinishing()).isTrue();


        imageUtilField.setAccessible(false);
    }

    @Test
    public void whenClickRotateButton_thenRotateImage() throws Exception {
        View rotateButton = subject.findViewById(R.id.rotate_image);
        View imageView = subject.findViewById(R.id.image_capture);

        assertThat(imageView.getRotation()).isEqualTo(0f);

        rotateButton.performClick();
        assertThat(imageView.getRotation()).isEqualTo(-90f);

        rotateButton.performClick();
        assertThat(imageView.getRotation()).isEqualTo(-180f);

        rotateButton.performClick();
        assertThat(imageView.getRotation()).isEqualTo(-270f);

        rotateButton.performClick();
        assertThat(imageView.getRotation()).isEqualTo(-0f);
    }
}
