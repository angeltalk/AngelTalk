package act.angelman.presentation.activity;


import android.content.Intent;
import android.view.View;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowMediaPlayer;
import org.robolectric.shadows.util.DataSource;

import java.util.concurrent.TimeUnit;

import act.angelman.BuildConfig;
import act.angelman.R;
import act.angelman.UITest;
import act.angelman.domain.model.CardModel;
import act.angelman.presentation.shadow.ShadowFileUtil;
import act.angelman.presentation.util.ContentsUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = ShadowFileUtil.class)
public class MakeCardPreviewActivityTest extends UITest {

    private MakeCardPreviewActivity subject;
    private String CONTENT_PATH = "/storage/emulated/0/angelman/contents/amusementpark.mp4";

    @Before
    public void setUp() throws Exception {

        Intent intent = new Intent();
        intent.putExtra(ContentsUtil.CONTENT_PATH, CONTENT_PATH);
        intent.putExtra(ContentsUtil.CARD_TYPE, CardModel.CardType.VIDEO_CARD.getValue());

        ShadowMediaPlayer.addMediaInfo(DataSource.toDataSource(CONTENT_PATH), new ShadowMediaPlayer.MediaInfo(3000, -1));

        subject = setupActivityWithIntent(MakeCardPreviewActivity.class, intent);
    }

    @Test
    public void givenVideoCardPreview_whenStartActivity_thenShowRetakeButtonAndCheckButtonAndPlayButton() throws Exception {
        assertThat(subject.findViewById(R.id.camera_recode_texture).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.preview_play_button).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.retake_button).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.confirm_button).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    @Ignore("UI Test로 Cover 예정")
    public void givenVideoCardPreview_whenClickPlayButton_thenHidePlayButtonAndMediaPlay() throws Exception {
        subject.findViewById(R.id.preview_play_button).performClick();
        assertThat(subject.findViewById(R.id.preview_play_button).getVisibility()).isEqualTo(View.GONE);
        Robolectric.getBackgroundThreadScheduler().advanceBy(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void givenVideoCardPreview_whenRetakeButtonClick_thenBackToVideoActivity() throws Exception {
        subject.findViewById(R.id.retake_button).performClick();
        assertEqualNextStartedActivity(VideoActivity.class.getCanonicalName());
    }

    @Test
    public void givenVideoCardPreview_whenBackButtonClick_thenBackToVideoActivity() throws Exception {
        subject.onBackPressed();
        assertEqualNextStartedActivity(VideoActivity.class.getCanonicalName());
    }

    @Test
    public void givenVideoCardPreview_whenConfirmButtonClick_thenGoToMakeCardActivity() throws Exception {
        subject.findViewById(R.id.confirm_button).performClick();
        assertEqualNextStartedActivity(MakeCardActivity.class.getCanonicalName());
    }

    private void assertEqualNextStartedActivity(String canonicalName) {
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName()).isEqualTo(canonicalName);
    }

}