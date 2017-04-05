package act.sds.samsung.angelman.presentation.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CardTransferModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.network.transfer.CardTransfer;
import act.sds.samsung.angelman.presentation.adapter.CardImageAdapter;
import act.sds.samsung.angelman.presentation.custom.CardCategoryLayout;
import act.sds.samsung.angelman.presentation.custom.CardViewPager;
import act.sds.samsung.angelman.presentation.listener.OnDownloadCompleteListener;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;
import act.sds.samsung.angelman.presentation.util.FileUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

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

    private RequestManager glide;
    private String receiveKey ;
    private Context context;

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

    public void setCardTransfer(CardTransfer cardTransfer){
        this.cardTransfer = cardTransfer;
    }



}
