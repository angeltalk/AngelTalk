package act.angelman.presentation.activity;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.View;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowEnvironment;
import org.robolectric.shadows.ShadowMediaPlayer;
import org.robolectric.shadows.util.DataSource;

import java.io.File;

import javax.inject.Inject;

import act.angelman.R;
import act.angelman.TestAngelmanApplication;
import act.angelman.UITest;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.presentation.custom.VideoCardTextureView;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.shadow.ShadowFileUtil;
import act.angelman.presentation.util.ContentsUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22, shadows = ShadowFileUtil.class)
public class MakeCardPreviewActivityTest extends UITest {

    @Inject
    CardRepository cardRepository;

    private String VIDEO_CONTENT_PATH = ContentsUtil.getContentFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator + "amusementpark.mp4";
    private String VIDEO_THUMBNAIL_PATH = ContentsUtil.getContentFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator + "amusementpark.jpg";
    private String PHOTO_CONTENT_PATH = ContentsUtil.getContentFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator + "bus.jpg";

    @Before
    public void setUp() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
    }

    @Test
    public void givenVideoCardIntent_whenStartActivity_thenShowRetakeButtonAndCheckButtonAndPlayButton() throws Exception {
        MakeCardPreviewActivity subject = setUpWithVideoContent();

        assertThat(subject.cardPreviewLayout.cameraRecodeVideo.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.cardPreviewLayout.previewPlayButton.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.cardPreviewLayout.cameraRecodeImage.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.rerecord_button).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.confirm_button).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.cardPreviewLayout.cameraRecodeGuide.getText()).contains("영상");
    }

    @Test
    @Ignore("UI Test로 Cover 예정")
    public void givenVideoCardIntent_whenClickPlayButton_thenHidePlayButtonAndMediaPlay() throws Exception {
        MakeCardPreviewActivity subject = setUpWithVideoContent();

        subject.findViewById(R.id.preview_play_button).performClick();
        assertThat(subject.findViewById(R.id.preview_play_button).getVisibility()).isEqualTo(View.GONE);
        Robolectric.getBackgroundThreadScheduler().advanceBy(1000);
    }

    @Test
    public void givenVideoCardIntent_whenRetakeButtonClick_thenBackToVideoActivity() throws Exception {
        MakeCardPreviewActivity subject = setUpWithVideoContent();

        subject.findViewById(R.id.rerecord_button).performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(VideoActivity.class.getCanonicalName());
    }

    @Test
    public void givenVideoCardIntent_whenBackButtonClick_thenBackToVideoActivity() throws Exception {
        MakeCardPreviewActivity subject = setUpWithVideoContent();

        subject.onBackPressed();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(VideoActivity.class.getCanonicalName());
    }

    @Test
    public void givenVideoCardIntent_whenBackToVideoActivity_thenDeleteContentAndThumbnailFile() throws Exception {
        MakeCardPreviewActivity subject = setUpWithVideoContent();

        assertThat(new File(VIDEO_CONTENT_PATH)).exists();
        assertThat(new File(VIDEO_THUMBNAIL_PATH)).exists();

        subject.onBackPressed();

        assertThat(new File(VIDEO_CONTENT_PATH)).doesNotExist();
        assertThat(new File(VIDEO_THUMBNAIL_PATH)).doesNotExist();
    }

    @Test
    public void givenVideoCardIntent_whenConfirmButtonClick_thenGoToMakeCardActivity() throws Exception {
        MakeCardPreviewActivity subject = setUpWithVideoContent();

        subject.findViewById(R.id.confirm_button).performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent intent = shadowActivity.getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).isEqualTo(MakeCardActivity.class.getCanonicalName());
        assertThat(intent.getStringExtra(ContentsUtil.CARD_TYPE)).isEqualTo(CardModel.CardType.VIDEO_CARD.getValue());
    }

    @Test
    public void givenPhotoCardIntent_whenLaunched_thenShowPhotoCardPreview() throws Exception {
        MakeCardPreviewActivity subject = setUpWithPhotoContent();

        assertThat(subject.cardPreviewLayout.cameraRecodeVideo.getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.cardPreviewLayout.previewPlayButton.getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.cardPreviewLayout.cameraRecodeImage.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.cardPreviewLayout.cameraRecodeGuide.getText()).contains("사진");
    }

    @Test
    public void givenPhotoCardIntent_whenRetakeButtonClick_thenBackToCamera2Activity() throws Exception {
        MakeCardPreviewActivity subject = setUpWithPhotoContent();

        subject.findViewById(R.id.rerecord_button).performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(Camera2Activity.class.getCanonicalName());
    }

    @Test
    public void givenPhotoCardIntent_whenBackButtonClick_thenBackToCamera2Activity() throws Exception {
        MakeCardPreviewActivity subject = setUpWithPhotoContent();

        subject.onBackPressed();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(Camera2Activity.class.getCanonicalName());
    }

    @Test
    public void givenPhotoCardIntent_whenBackToVideoActivity_thenDeleteContentFile() throws Exception {
        MakeCardPreviewActivity subject = setUpWithPhotoContent();
        assertThat(new File(PHOTO_CONTENT_PATH)).exists();
        subject.onBackPressed();
        assertThat(new File(PHOTO_CONTENT_PATH)).doesNotExist();
    }

    @Test
    public void givenPhotoCardIntent_whenConfirmButtonClick_thenGoToMakeCardActivity() throws Exception {
        MakeCardPreviewActivity subject = setUpWithPhotoContent();

        subject.findViewById(R.id.confirm_button).performClick();

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent intent = shadowActivity.getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).isEqualTo(MakeCardActivity.class.getCanonicalName());
        assertThat(intent.getStringExtra(ContentsUtil.CARD_TYPE)).isEqualTo(CardModel.CardType.PHOTO_CARD.getValue());
    }

    @Test
    public void givenPhotoCardEditIntent_whenConfirmButtonClick_thenDeleteContentFile() throws Exception {
        MakeCardPreviewActivity subject = setUpWithPhotoContentEdit();
        when(cardRepository.getSingleCard(anyString()))
                .thenReturn(CardModel.builder().contentPath(PHOTO_CONTENT_PATH).build());
        subject.findViewById(R.id.confirm_button).performClick();
        File contentFile = new File(subject.getIntent().getStringExtra(ContentsUtil.CONTENT_PATH));
        assertThat(contentFile.exists()).isFalse();
    }

    @Test
    public void givenPhotoCardEditIntent_whenConfirmButtonClick_thenGoToCardViewActivity() throws Exception {
        MakeCardPreviewActivity subject = setUpWithPhotoContentEdit();
        when(cardRepository.getSingleCard(anyString()))
                .thenReturn(CardModel.builder().contentPath(PHOTO_CONTENT_PATH).build());
        subject.findViewById(R.id.confirm_button).performClick();
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent intent = shadowActivity.getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).isEqualTo(CardViewPagerActivity.class.getCanonicalName());
    }

    @Test
    public void givenPhotoCardEditIntent_whenConfirmButtonClick_thenDataChange() throws Exception {
        MakeCardPreviewActivity subject = setUpWithPhotoContentEdit();
        when(cardRepository.getSingleCard(anyString()))
                .thenReturn(CardModel.builder().contentPath(PHOTO_CONTENT_PATH).build());
        subject.findViewById(R.id.confirm_button).performClick();

        verify(cardRepository).updateSingleCardContent(eq("1"), eq(CardModel.CardType.PHOTO_CARD.getValue()),eq(PHOTO_CONTENT_PATH),anyString());
    }

    @Test
    public void givenVideoCardEditIntent_whenConfirmButtonClick_thenDataChange() throws Exception {
        MakeCardPreviewActivity subject = setUpWithVideoContentEdit();
        when(cardRepository.getSingleCard(anyString()))
                .thenReturn(CardModel.builder().contentPath(PHOTO_CONTENT_PATH).build());
        subject.findViewById(R.id.confirm_button).performClick();

        verify(cardRepository).updateSingleCardContent(eq("1"), eq(CardModel.CardType.VIDEO_CARD.getValue()),eq(VIDEO_CONTENT_PATH),eq(VIDEO_THUMBNAIL_PATH));
    }

    @Test
    public void givenVideoCardEditIntent_whenClickPlayButton_thenHidePlayButton() throws Exception {
        final MakeCardPreviewActivity subject = setUpWithVideoContentEdit();
        assertThat(subject.cardPreviewLayout.previewPlayButton.getVisibility()).isEqualTo(View.VISIBLE);

        VideoCardTextureView mockVideoCardTextureView = mock(VideoCardTextureView.class);
        subject.cardPreviewLayout.cameraRecodeVideo = mockVideoCardTextureView;
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MediaPlayer.OnCompletionListener onCompletionListener = ((MediaPlayer.OnCompletionListener) invocation.getArguments()[0]);
                assertThat(subject.cardPreviewLayout.previewPlayButton.getVisibility()).isEqualTo(View.GONE);
                onCompletionListener.onCompletion(null);
                return null;
            }
        }).when(mockVideoCardTextureView).play(any(MediaPlayer.OnCompletionListener.class));

        subject.cardPreviewLayout.previewPlayButton.performClick();
        assertThat(subject.cardPreviewLayout.previewPlayButton.getVisibility()).isEqualTo(View.VISIBLE);
        verify(mockVideoCardTextureView).resetPlayer();
    }

    private MakeCardPreviewActivity setUpWithVideoContent() {
        Intent intent = new Intent();
        intent.putExtra(ContentsUtil.CONTENT_PATH, VIDEO_CONTENT_PATH);
        intent.putExtra(ContentsUtil.CARD_TYPE, CardModel.CardType.VIDEO_CARD.getValue());

        ShadowMediaPlayer.addMediaInfo(DataSource.toDataSource(VIDEO_CONTENT_PATH), new ShadowMediaPlayer.MediaInfo(3000, -1));

        return setupActivityWithIntent(MakeCardPreviewActivity.class, intent);
    }

    private MakeCardPreviewActivity setUpWithPhotoContent() {
        Intent intent = new Intent();
        intent.putExtra(ContentsUtil.CONTENT_PATH, PHOTO_CONTENT_PATH);
        intent.putExtra(ContentsUtil.CARD_TYPE, CardModel.CardType.PHOTO_CARD.getValue());

        return setupActivityWithIntent(MakeCardPreviewActivity.class, intent);
    }

    private MakeCardPreviewActivity setUpWithPhotoContentEdit() {
        Intent intent = new Intent();
        intent.putExtra(ContentsUtil.CONTENT_PATH, PHOTO_CONTENT_PATH);
        intent.putExtra(ContentsUtil.CARD_TYPE, CardModel.CardType.PHOTO_CARD.getValue());
        intent.putExtra(ApplicationConstants.EDIT_CARD_ID, "1");

        return setupActivityWithIntent(MakeCardPreviewActivity.class, intent);
    }

    private MakeCardPreviewActivity setUpWithVideoContentEdit() {
        Intent intent = new Intent();
        intent.putExtra(ContentsUtil.CONTENT_PATH, VIDEO_CONTENT_PATH);
        intent.putExtra(ContentsUtil.CARD_TYPE, CardModel.CardType.VIDEO_CARD.getValue());
        intent.putExtra(ApplicationConstants.EDIT_CARD_ID, "1");

        ShadowMediaPlayer.addMediaInfo(DataSource.toDataSource(VIDEO_CONTENT_PATH), new ShadowMediaPlayer.MediaInfo(3000, -1));

        return setupActivityWithIntent(MakeCardPreviewActivity.class, intent);
    }
}