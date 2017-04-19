package act.angelman.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;

import java.io.File;

import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.presentation.custom.CardPreviewLayout;
import act.angelman.presentation.custom.VideoCardTextureView;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.FileUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeCardPreviewActivity extends AppCompatActivity {

    @BindView(R.id.card_preview_layout)
    public CardPreviewLayout cardPreviewLayout;

    private String contentPath;
    private CardModel.CardType cardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_card_preview);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        contentPath = intent.getStringExtra(ContentsUtil.CONTENT_PATH);
        cardType = CardModel.CardType.valueOf(intent.getStringExtra(ContentsUtil.CARD_TYPE));

        cardPreviewLayout.initLayout(cardType);
        initPreviewContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cardPreviewLayout.initLayout(cardType);
        initPreviewContent();
    }

    private void initPreviewContent() {
        if(cardType == CardModel.CardType.PHOTO_CARD) {
            if (FileUtil.isFileExist(contentPath)) {
                Glide.with(this)
                        .load(ContentsUtil.getContentFile(contentPath))
                        .override(280, 280)
                        .into(cardPreviewLayout.cameraRecodeImage);
            }
        } else if(cardType == CardModel.CardType.VIDEO_CARD) {
            if (FileUtil.isFileExist(contentPath)) {
                Glide.with(this)
                        .load(ContentsUtil.getContentFile(ContentsUtil.getThumbnailPath(contentPath)))
                        .override(280, 280)
                        .into(cardPreviewLayout.cameraRecodeImage);
                cardPreviewLayout.cameraRecodeVideo.setScaleType(VideoCardTextureView.ScaleType.CENTER_CROP);
                cardPreviewLayout.cameraRecodeVideo.setDataSource(contentPath);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(cardType == CardModel.CardType.PHOTO_CARD) {
            Intent intent = new Intent(this, Camera2Activity.class);
            startActivity(intent);
        } else if(cardType == CardModel.CardType.VIDEO_CARD) {
            Intent intent = new Intent(this, VideoActivity.class);
            startActivity(intent);
        }

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
        intent.putExtra(ContentsUtil.CARD_TYPE, cardType.getValue());
        startActivity(intent);
    }
}
