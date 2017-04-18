package act.angelman.presentation.custom;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import act.angelman.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardPreviewLayout extends RelativeLayout{

    @BindView(R.id.camera_recode_texture)
    public VideoCardTextureView cameraTextureView;

    @BindView(R.id.preview_play_button)
    public ImageView playButton;

    @BindView(R.id.camera_recode_frame)
    public ImageView cameraRecodeFrame;

    @BindView(R.id.photo_card_preview)
    public ImageView photoCardPreview;

    @BindView(R.id.photo_card_preview_background)
    public ImageView photoCardPreviewBackground;

    @BindView(R.id.camera_recode_guide)
    public FontTextView cameraRecodeGuide;

    private Context context;

    public CardPreviewLayout(Context context) {
        super(context);
        this.context = context;
        inflateLayout(context);
    }

    public CardPreviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateLayout(context);
    }

    public void initLayout(boolean videoCard) {
        if(videoCard) {
            cameraTextureView.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            cameraRecodeFrame.setVisibility(View.VISIBLE);

            photoCardPreview.setVisibility(View.GONE);
            photoCardPreviewBackground.setVisibility(View.GONE);

            cameraRecodeGuide.setText(context.getString(R.string.video_content_check_guide));
        } else {
            cameraTextureView.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);
            cameraRecodeFrame.setVisibility(View.GONE);

            photoCardPreview.setVisibility(View.VISIBLE);
            photoCardPreviewBackground.setVisibility(View.VISIBLE);

            cameraRecodeGuide.setText(context.getString(R.string.photo_content_check_guide));
        }
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

    private void inflateLayout(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.layout_card_preview_view, this, true);
        ButterKnife.bind(this);
    }

}
