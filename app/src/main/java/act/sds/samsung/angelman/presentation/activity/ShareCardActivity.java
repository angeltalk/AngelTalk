package act.sds.samsung.angelman.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CardTransferModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.network.transfer.CardTransfer;
import act.sds.samsung.angelman.presentation.adapter.CardImageAdapter;
import act.sds.samsung.angelman.presentation.custom.CardCategoryLayout;
import act.sds.samsung.angelman.presentation.custom.CardViewPager;
import act.sds.samsung.angelman.presentation.custom.CustomConfirmDialog;
import act.sds.samsung.angelman.presentation.listener.OnDownloadCompleteListener;
import act.sds.samsung.angelman.presentation.manager.ApplicationConstants;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;
import act.sds.samsung.angelman.presentation.util.FileUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareCardActivity extends AppCompatActivity {

    @Inject
    CardTransfer cardTransfer;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.title_container)
    CardCategoryLayout titleLayout;

    @BindView(R.id.view_pager)
    CardViewPager mViewPager;

    @BindView(R.id.category_item_title)
    TextView categoryTitle;

    @BindView(R.id.image_angelee_gif)
    ImageView imageLoadingGif;

    @BindView(R.id.on_loading_view)
    LinearLayout loadingViewLayout;


    private CustomConfirmDialog saveConfirmDialog;

    private RequestManager glide;
    private String receiveKey;
    private Context context;

    private CardTransferModel shareCardModel;
    private String shareFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_card);
        ButterKnife.bind(this);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        this.glide = Glide.with(this);
        this.context = getApplicationContext();

        initView();

        if (getString(R.string.kakao_scheme).equals(getIntent().getScheme())) {
            Uri uri = getIntent().getData();
            receiveKey = uri.getQueryParameter("key");
        }

        downloadCard();
    }

    private void initView() {
        showLoadingAnimation();
        titleLayout.setCategoryModelTitle(getApplicationContext().getString(R.string.new_card_title));
        titleLayout.hideCardCountText(true);
    }

    private void showLoadingAnimation() {
        Glide.with(ShareCardActivity.this)
                .load(R.drawable.angelee)
                .asGif()
                .crossFade()
                .into(imageLoadingGif);
    }


    public void downloadCard() {
        cardTransfer.downloadCard(receiveKey, new OnDownloadCompleteListener() {
            @Override
            public void onSuccess(CardTransferModel cardTransferModel, String filePath) {
                try {
                    shareCardModel = cardTransferModel;
                    shareFilePath = filePath;

                    String tempLocation = context.getCacheDir() + File.separator + receiveKey;
                    FileUtil.unzip(filePath, tempLocation);

                    List<CardModel> cardModelList = Lists.newArrayList();
                    CardModel cardModel = ContentsUtil.getTempCardModel(tempLocation, cardTransferModel);

                    cardModelList.add(cardModel);

                    mViewPager.setAdapter(new CardImageAdapter(context, cardModelList, glide, applicationManager));

                    loadingViewLayout.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail() {
                FileUtil.removeFilesIn(ContentsUtil.getTempFolder());
            }
        });
    }

    @OnClick(R.id.card_save_button)
    public void onClickCardSaveButton(View view) {
        saveConfirmDialog = new CustomConfirmDialog(this, context.getApplicationContext().getResources().getString(R.string.save_confirm_message), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shareCardModel != null){
                    try {
                        FileUtil.unzip(shareFilePath, ContentsUtil.getTempFolder());
                        CardModel cardModel = saveNewSharedCard(shareCardModel);

                        ContentsUtil.copySharedFiles(cardModel);

                        FileUtil.removeFilesIn(ContentsUtil.getTempFolder());
                        CategoryModel categoryModel = categoryRepository.getCategoryAllList().get(0);
                        moveToCategoryViewPagerActivity(categoryModel, ApplicationConstants.INTENT_KEY_SHARE_CARD);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(context, R.string.cannot_share_card_save_message,Toast.LENGTH_SHORT);
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveConfirmDialog.dismiss();
            }
        });

    }

    private void moveToCategoryViewPagerActivity(CategoryModel categoryModel, String intentKey) {
        Intent intent = new Intent(getApplicationContext(), CardViewPagerActivity.class);
        applicationManager.setCategoryModel(categoryModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApplicationConstants.CATEGORY_COLOR, categoryModel.color);
        intent.putExtra(intentKey, true);
        getApplicationContext().startActivity(intent);
        finish();
    }


    private CardModel saveNewSharedCard(CardTransferModel cardTransferModel) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        CardModel.CardType cardType = CardModel.CardType.valueOf(cardTransferModel.cardType);

        String contentPath = cardType == CardModel.CardType.VIDEO_CARD ? ContentsUtil.getVideoPath() : ContentsUtil.getImagePath();

        CardModel cardModel = CardModel.builder()
                .name(cardTransferModel.name)
                .contentPath(contentPath)
                .voicePath(ContentsUtil.getVoicePath())
                .firstTime(dateFormat.format(date))
                .categoryId(categoryRepository.getCategoryAllList().get(0).index)
                .cardType(cardType).thumbnailPath(cardType == CardModel.CardType.VIDEO_CARD ? ContentsUtil.getThumbnailPath(contentPath) : null)
                .hide(false)
                .build();

        cardRepository.createSingleCardModel(cardModel);

        return cardModel;
    }

}
