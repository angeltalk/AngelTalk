package angeltalk.plus.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import angeltalk.plus.AngelmanApplication;
import angeltalk.plus.R;
import angeltalk.plus.domain.model.CardModel;
import angeltalk.plus.domain.repository.CardRepository;
import angeltalk.plus.presentation.custom.CardPreviewLayout;
import angeltalk.plus.presentation.custom.VideoCardTextureView;
import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.util.ContentsUtil;
import angeltalk.plus.presentation.util.FileUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeCardPreviewActivity extends AbstractActivity {

    @Inject
    public CardRepository cardRepository;

    @BindView(R.id.card_preview_layout)
    public CardPreviewLayout cardPreviewLayout;

    private String previewContentPath;
    private CardModel.CardType previewCardType;
    private String editCardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_make_card_preview);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        previewContentPath = intent.getStringExtra(ContentsUtil.CONTENT_PATH);
        previewCardType = CardModel.CardType.valueOf(intent.getStringExtra(ContentsUtil.CARD_TYPE));
        editCardId = intent.getStringExtra(ApplicationConstants.EDIT_CARD_ID);

        cardPreviewLayout.initLayout(previewCardType);
        initPreviewContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cardPreviewLayout.initLayout(previewCardType);
        initPreviewContent();
    }

    private void initPreviewContent() {
        if(previewCardType == CardModel.CardType.PHOTO_CARD) {
            if (FileUtil.isFileExist(previewContentPath)) {
                Glide.with(this)
                        .load(ContentsUtil.getContentFile(previewContentPath))
                        .into(cardPreviewLayout.cameraRecodeImage);
            }
        } else if(previewCardType == CardModel.CardType.VIDEO_CARD) {
            if (FileUtil.isFileExist(previewContentPath)) {
                Glide.with(this)
                        .load(ContentsUtil.getContentFile(ContentsUtil.getThumbnailPath(previewContentPath)))
                        .override(280, 280)
                        .into(cardPreviewLayout.cameraRecodeImage);
                cardPreviewLayout.cameraRecodeVideo.setScaleType(VideoCardTextureView.ScaleType.CENTER_CROP);
                cardPreviewLayout.cameraRecodeVideo.setDataSource(previewContentPath);
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if(previewCardType == CardModel.CardType.PHOTO_CARD) {
                Intent intent = new Intent(this, Camera2Activity.class);
                intent.putExtra(ApplicationConstants.EDIT_CARD_ID, editCardId);
                startActivity(intent);
            } else if(previewCardType == CardModel.CardType.VIDEO_CARD) {
                Intent intent = new Intent(this, VideoActivity.class);
                intent.putExtra(ApplicationConstants.EDIT_CARD_ID, editCardId);
                startActivity(intent);
        }

        ContentsUtil.deleteContentAndThumbnail(previewContentPath);
        finish();
    }

    @OnClick(R.id.rerecord_button)
    public void onClickRetakeButton (View v) {
        onBackPressed();
    }

    @OnClick(R.id.confirm_button)
    public void onClickConfirmButton (View v) {
        if (editCardId == null) {
            Intent intent = new Intent(this, MakeCardActivity.class);
            intent.putExtra(ContentsUtil.CONTENT_PATH, previewContentPath);
            intent.putExtra(ContentsUtil.CARD_TYPE, previewCardType.getValue());
            startActivity(intent);
        } else {
            ContentsUtil.deleteContentAndThumbnail(cardRepository.getSingleCard(editCardId).contentPath);

            String thumbnailPath = null;
            if(previewCardType == CardModel.CardType.VIDEO_CARD) {
                thumbnailPath = ContentsUtil.getThumbnailPath(previewContentPath);
            }
            cardRepository.updateSingleCardContent(editCardId, previewCardType.getValue(), previewContentPath, thumbnailPath);

            Intent intent = new Intent(this, CardViewPagerActivity.class);
            intent.putExtra(ApplicationConstants.INTENT_KEY_CARD_EDITED, true);
            startActivity(intent);
        }
    }


}
