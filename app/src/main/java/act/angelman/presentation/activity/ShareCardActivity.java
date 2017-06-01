package act.angelman.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
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

import act.angelman.AngelmanApplication;
import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.model.CardTransferModel;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.domain.repository.CategoryRepository;
import act.angelman.network.transfer.CardTransfer;
import act.angelman.presentation.adapter.CardImageAdapter;
import act.angelman.presentation.custom.CardTitleLayout;
import act.angelman.presentation.custom.CardViewPager;
import act.angelman.presentation.custom.CategorySelectDialog;
import act.angelman.presentation.custom.CustomConfirmDialog;
import act.angelman.presentation.listener.OnDownloadCompleteListener;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.manager.ApplicationManager;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.FileUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareCardActivity extends AbstractActivity {

    @Inject
    CardTransfer cardTransfer;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.title_container)
    CardTitleLayout titleLayout;

    @BindView(R.id.view_pager)
    CardViewPager mViewPager;

    @BindView(R.id.category_item_title)
    TextView categoryTitle;

    @BindView(R.id.image_angelee_gif)
    ImageView imageLoadingGif;

    @BindView(R.id.on_loading_view)
    LinearLayout loadingViewLayout;

    @BindView(R.id.on_loading_view_text)
    TextView loadingViewText;

    private CategorySelectDialog categorySelectDialog;
    private CustomConfirmDialog cardDownloadFailDialog;
    private RequestManager glide;
    private Context context;
    private List<CardTransferModel> shareCardModelList;
    private List<String> receiveKeys;

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
            receiveKeys = Lists.newArrayList();
            receiveKeys.add(uri.getQueryParameter("key"));
        }else if ("app".equals(getIntent().getScheme())) {
            Uri uri = getIntent().getData();
            receiveKeys = Lists.newArrayList();
            receiveKeys.add(uri.getQueryParameter("key"));
        }else if (getIntent().getBooleanExtra(ApplicationConstants.INTENT_KEY_MULTI_DOWNLOAD, false)) {
            receiveKeys = getIntent().getStringArrayListExtra(ApplicationConstants.INTENT_KEY_MULTI_DOWNLOAD_DATA);
        }

        shareCardModelList = Lists.newArrayList();
        downloadCard();
    }

    @Override
    public void onBackPressed() {
        moveToCategoryMenuActivity();
    }

    private void initView() {
        showLoadingAnimation();
        titleLayout.setCategoryModelTitle(getApplicationContext().getString(R.string.new_card_title));
        titleLayout.hideCardCountText();
        titleLayout.hideListCardButton();
        titleLayout.setBackButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCategoryMenuActivity();
            }
        });
    }

    private void showLoadingAnimation() {
        Glide.with(ShareCardActivity.this)
                .load(R.drawable.angelee)
                .asGif()
                .crossFade()
                .into(imageLoadingGif);
    }

    public void downloadCard() {
        if(!cardTransfer.isConnectedToNetwork()){
            showCardDownloadFailDialog();
            return;
        }

        loadingViewText.setText(getString(R.string.card_loading_message) + "\n(0/" + receiveKeys.size() + ")");

        for(final String receiveKey : receiveKeys) {
            cardTransfer.downloadCard(receiveKey, new OnDownloadCompleteListener() {
                @Override
                public void onSuccess(CardTransferModel cardTransferModel, String filePath) {
                    try {
                        cardTransferModel.downloadedFilePath = filePath;
                        shareCardModelList.add(cardTransferModel);

                        String tempLocation = context.getCacheDir() + File.separator + receiveKey;
                        FileUtil.unzip(filePath, tempLocation);

                        List<CardModel> cardModelList = Lists.newArrayList();
                        CardModel cardModel = ContentsUtil.getTempCardModel(tempLocation, cardTransferModel);

                        cardModelList.add(cardModel);
                        loadingViewText.setText(getString(R.string.card_loading_message) + "\n(" + shareCardModelList.size() + "/" + receiveKeys.size() + ")");

                        if(shareCardModelList.size() == receiveKeys.size()) {
                            mViewPager.setAdapter(new CardImageAdapter(context, cardModelList, glide));
                            loadingViewLayout.setVisibility(View.GONE);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail() {
                    FileUtil.removeFile(ContentsUtil.getTempFolder(context));
                    showCardDownloadFailDialog();
                }
            });
        }
    }


    @OnClick(R.id.card_save_button)
    public void onClickCardSaveButton(View view) {
        categorySelectDialog = new CategorySelectDialog(ShareCardActivity.this, categoryRepository.getCategoryAllList(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CardTransferModel selectTransferModel = shareCardModelList.get(mViewPager.getCurrentItem());
                    CategoryModel selectItem = categorySelectDialog.getSelectItem();
                    if(selectItem == null) {
                        return;
                    }
                    FileUtil.unzip(selectTransferModel.downloadedFilePath, ContentsUtil.getTempFolder(context) + "unzip");
                    CardModel cardModel = saveNewSharedCard(selectTransferModel, selectItem.index);
                    ContentsUtil.copySharedFiles(context, cardModel, ContentsUtil.getTempFolder(context) + "unzip");
                    FileUtil.removeFilesIn(ContentsUtil.getTempFolder(context) + "unzip");

                    if(shareCardModelList.size() == 1) {
                        applicationManager.setCategoryModel(selectItem);
                        applicationManager.setCurrentCardIndex(cardModel.cardIndex);
                        moveToCardListActivity();
                    } else {
                        shareCardModelList.remove(selectTransferModel);
                        ((CardImageAdapter) mViewPager.getAdapter()).removeItemAt(mViewPager.getCurrentItem());
                        mViewPager.getAdapter().notifyDataSetChanged();
                        categorySelectDialog.dismiss();
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.cannot_share_card_save_message,Toast.LENGTH_SHORT).show();
                }
            }
        });
        categorySelectDialog.show();
    }

    private void moveToCategoryMenuActivity() {
        Intent intent = new Intent(context, CategoryMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void moveToCardListActivity() {
        Intent intent = new Intent(getApplicationContext(), CardListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApplicationConstants.INTENT_KEY_SHARE_CARD, true);
        startActivity(intent);
        finish();
    }

    private CardModel saveNewSharedCard(CardTransferModel cardTransferModel, int categoryIndex) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        CardModel.CardType cardType = CardModel.CardType.valueOf(cardTransferModel.cardType);

        String contentPath = cardType == CardModel.CardType.VIDEO_CARD ? ContentsUtil.getVideoPath(context) : ContentsUtil.getImagePath(context);

        CardModel cardModel = CardModel.builder()
                .name(cardTransferModel.name)
                .contentPath(contentPath)
                .voicePath(ContentsUtil.getVoicePath(context))
                .firstTime(dateFormat.format(date))
                .categoryId(categoryIndex)
                .cardType(cardType).thumbnailPath(cardType == CardModel.CardType.VIDEO_CARD ? ContentsUtil.getThumbnailPath(contentPath) : null)
                .hide(false)
                .build();

        cardRepository.createSingleCardModel(cardModel);

        return cardModel;
    }

    private void showCardDownloadFailDialog() {
        cardDownloadFailDialog = new CustomConfirmDialog(this, getString(R.string.card_download_fail), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardDownloadFailDialog.dismiss();
                moveToCategoryMenuActivity();
            }
        });
        cardDownloadFailDialog.show();
    }

    @VisibleForTesting List<String> getReceiveKeys() { return receiveKeys; }
    @VisibleForTesting List<CardTransferModel> getShareCardModelList() { return shareCardModelList; }
}
