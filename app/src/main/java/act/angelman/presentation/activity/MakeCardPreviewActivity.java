package act.angelman.presentation.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.presentation.custom.VideoCardTextureView;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.FileUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeCardPreviewActivity extends AppCompatActivity {

    @BindView(R.id.camera_recode_texture)
    public VideoCardTextureView cameraTextureView;

    @BindView(R.id.preview_play_button)
    public ImageView playButton;

    private String contentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_card_preview);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        contentPath = intent.getStringExtra(ContentsUtil.CONTENT_PATH);

        initPreview();
    }

    private void initPreview() {
        if(FileUtil.isFileExist(contentPath)) {
            cameraTextureView.setScaleType(VideoCardTextureView.ScaleType.TOP);
            cameraTextureView.setDataSource(contentPath);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, VideoActivity.class);
        startActivity(intent);
        File contentFile = new File(contentPath);
        if (contentFile.exists()) {
            contentFile.delete();
        }
        File thumbnailFile = new File(ContentsUtil.getThumbnailPath(contentPath));
        if(thumbnailFile.exists()) {
            thumbnailFile.delete();
        }
        finish();
    }

    @OnClick(R.id.retake_button)
    public void onClickRetakeButton (View v) {
        onBackPressed();
    }

    @OnClick(R.id.confirm_button)
    public void onClickConfirmButton (View v) {
        Intent intent = new Intent(this, MakeCardActivity.class);
        intent.putExtra(ContentsUtil.CONTENT_PATH, contentPath);
        intent.putExtra(ContentsUtil.CARD_TYPE, CardModel.CardType.VIDEO_CARD.getValue());
        startActivity(intent);
    }

    @OnClick(R.id.preview_play_button)
    public void onClickPreviewPlayButton (View v) {
        playButton.setVisibility(View.GONE);
        cameraTextureView.play(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playButton.setVisibility(View.VISIBLE);
                cameraTextureView.resetPlayer();
            }
        });
    }

}
