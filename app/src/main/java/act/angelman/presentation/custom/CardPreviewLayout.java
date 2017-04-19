package act.angelman.presentation.custom;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import act.angelman.R;
import act.angelman.domain.model.CardModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardPreviewLayout extends RelativeLayout{

    @BindView(R.id.camera_recode_video)
    public VideoCardTextureView cameraRecodeVideo;

    @BindView(R.id.preview_play_button)
    public ImageView previewPlayButton;

    @BindView(R.id.camera_recode_image)
    public ImageView cameraRecodeImage;

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

    @OnClick(R.id.preview_play_button)
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
        ButterKnife.bind(this);
    }

}
