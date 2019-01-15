package act.angelman.presentation.activity;


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
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowScaleGestureDetector;

import java.io.File;

import javax.inject.Inject;

import act.angelman.R;
import act.angelman.TestAngelmanApplication;
import act.angelman.UITest;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.shadow.ShadowContentUtil;
import act.angelman.presentation.util.ContentsUtil;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22, shadows = ShadowContentUtil.class)
public class PhotoEditorActivityTest extends UITest{

    private PhotoEditorActivity subject;

    @Inject
    CardRepository cardRepository;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);

        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.IMAGE_PATH_EXTRA, Uri.fromFile(new File("../../res/drawable-hdpi/ic_camera.png")));
        subject = setupActivityWithIntent(PhotoEditorActivity.class, intent);
    }

    @Test
    public void whenLaunched_thenShouldGetImagePathFromIntent() throws Exception {
        Intent intent = subject.getIntent();

        assertThat(intent).hasExtra(ApplicationConstants.IMAGE_PATH_EXTRA);
        assertThat(intent.getParcelableExtra(ApplicationConstants.IMAGE_PATH_EXTRA)).isNotNull();
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
        subject.findViewById(R.id.photo_edit_confirm).performClick();
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getStringExtra(ContentsUtil.CONTENT_PATH)).isEqualTo("haribo.mp4");
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(MakeCardActivity.class.getCanonicalName());
        assertThat(shadowActivity.isFinishing()).isTrue();
    }


    @Test
    public void givenLaunchWithEditMode_whenClickConfirmButton_thenUpdateCardModel() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.IMAGE_PATH_EXTRA, Uri.fromFile(new File("../../res/drawable-hdpi/ic_camera.png")));
        intent.putExtra(ApplicationConstants.EDIT_CARD_ID, "1");
        subject = setupActivityWithIntent(PhotoEditorActivity.class, intent);
        when(cardRepository.getSingleCard(anyString())).thenReturn(CardModel.builder()._id("1").name("Card").cardType(CardModel.CardType.PHOTO_CARD).build());

        subject.findViewById(R.id.photo_edit_confirm).performClick();
        verify(cardRepository).updateSingleCardContent(eq("1"),eq(CardModel.CardType.PHOTO_CARD.getValue()),eq("haribo.mp4"),anyString());
    }

    @Test
    public void givenLaunchWithEditMode_whenClickConfirmButton_thenMoveToCardViewPagerActivity() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.IMAGE_PATH_EXTRA, Uri.fromFile(new File("../../res/drawable-hdpi/ic_camera.png")));
        intent.putExtra(ApplicationConstants.EDIT_CARD_ID, "1");
        subject = setupActivityWithIntent(PhotoEditorActivity.class, intent);
        when(cardRepository.getSingleCard(anyString())).thenReturn(CardModel.builder()._id("1").name("Card").cardType(CardModel.CardType.PHOTO_CARD).build());

        subject.findViewById(R.id.photo_edit_confirm).performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getBooleanExtra(ApplicationConstants.INTENT_KEY_CARD_EDITED, false)).isTrue();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(CardViewPagerActivity.class.getCanonicalName());
        assertThat(shadowActivity.isFinishing()).isTrue();
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
