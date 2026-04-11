package angeltalk.plus.presentation.custom;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import angeltalk.plus.R;
import angeltalk.plus.domain.model.CardModel;
public class CardPreviewLayout extends RelativeLayout{

    public VideoCardTextureView cameraRecodeVideo;

    public ImageView previewPlayButton;

    public ImageView cameraRecodeImage;

    public TextView cameraRecodeGuide;

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

    public void initLayout(CardModel.CardType cardType) {
        if(cardType == CardModel.CardType.VIDEO_CARD) {
            cameraRecodeVideo.setVisibility(View.VISIBLE);
            previewPlayButton.setVisibility(View.VISIBLE);
            cameraRecodeImage.setVisibility(View.VISIBLE);

            cameraRecodeGuide.setText(context.getString(R.string.video_content_check_guide));
        } else {
            cameraRecodeVideo.setVisibility(View.GONE);
            previewPlayButton.setVisibility(View.GONE);
            cameraRecodeImage.setVisibility(View.VISIBLE);

            cameraRecodeGuide.setText(context.getString(R.string.photo_content_check_guide));
        }
    }
    // TODO(phase-6): @OnClick wiring removed — wire setOnClickListener in bindViews()
    public void onClickPreviewPlayButton (View v) {
        previewPlayButton.setVisibility(View.GONE);
        cameraRecodeVideo.play(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                previewPlayButton.setVisibility(View.VISIBLE);
                cameraRecodeVideo.resetPlayer();
            }
        });
    }

    private void inflateLayout(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.layout_card_preview_view, this, true);
        bindViews();
    }

    private void bindViews() {
        cameraRecodeVideo = findViewById(R.id.camera_recode_video);
        previewPlayButton = findViewById(R.id.preview_play_button);
        cameraRecodeImage = findViewById(R.id.camera_recode_image);
        cameraRecodeGuide = findViewById(R.id.camera_recode_guide);
        findViewById(R.id.preview_play_button).setOnClickListener(v -> onClickPreviewPlayButton(v));
    }
}
