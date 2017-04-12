package act.sds.samsung.angelman.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.network.transfer.CardTransfer;
import act.sds.samsung.angelman.network.transfer.KaKaoTransfer;
import act.sds.samsung.angelman.presentation.adapter.CardImageAdapter;
import act.sds.samsung.angelman.presentation.custom.CardTitleLayout;
import act.sds.samsung.angelman.presentation.custom.CardView;
import act.sds.samsung.angelman.presentation.custom.CardViewPager;
import act.sds.samsung.angelman.presentation.custom.CustomConfirmDialog;
import act.sds.samsung.angelman.presentation.custom.CustomSnackBar;
import act.sds.samsung.angelman.presentation.manager.ApplicationConstants;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class CardViewPagerActivity extends AbstractActivity {

    @Inject
    CardRepository cardRepository;

    @Inject
    ApplicationManager applicationManager;

    @Inject
    CardTransfer cardTransfer;

    @Inject
    KaKaoTransfer kaKaoTransfer;

    @BindView(R.id.title_container)
    CardTitleLayout cardTitleLayout;

    @BindView(R.id.button_container)
    LinearLayout buttonContainer;

    @BindView(R.id.card_delete_button)
    ImageButton cardDeleteButton;

    @BindView(R.id.card_share_button)
    ImageButton cardShareButton;

    @BindView(R.id.view_pager)
    CardViewPager mViewPager;

    @BindView(R.id.on_loading_view)
    LinearLayout loadingViewLayout;

    @BindView(R.id.image_angelee_gif)
    ImageView imageLoadingGif;


    @OnClick(R.id.card_delete_button)
    public void deleteButtonOnClick() {
        deleteCard();
    }

    @OnClick(R.id.card_share_button)
    public void shareButtonOnClick() {

        final CardModel cardModel = getCardModel(mViewPager.getCurrentItem());
        showLoadingAnimation();
        cardTransfer.uploadCard(cardModel, new OnSuccessListener<Map<String,String>>() {
            @Override
            public void onSuccess(Map<String, String> resultMap) {
                String thumbnailUrl = resultMap.get("url");
                String key = resultMap.get("key");

                kaKaoTransfer.sendKakaoLinkMessage(context, key, thumbnailUrl, cardModel);
                loadingViewLayout.setVisibility(View.GONE);
            }

        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingViewLayout.setVisibility(View.GONE);
                Toast.makeText(context, R.string.share_fail_message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    List<CardModel> allCardListInSelectedCategory;
    int currentCardIndex = 0;

    private CategoryModel selectedCategoryModel;
    private CardImageAdapter adapter;
    private CustomConfirmDialog dialog;
    private RequestManager glide;
    Context context;

    public CardModel getCardModel(int index) {
        CardView card = (CardView) adapter.viewCollection.get(index);
        return card.dataModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_card_view);
        ButterKnife.bind(this);
        glide = Glide.with(this);
        context = this;
        initializeView();

        if (getIntent().getBooleanExtra(ApplicationConstants.INTENT_KEY_NEW_CARD, false)) {
            showSnackBarMessage(ApplicationConstants.INTENT_KEY_NEW_CARD);
            mViewPager.setCurrentItem(1);
        }

        if (getIntent().getBooleanExtra(ApplicationConstants.INTENT_KEY_REFRESH_CARD, false)) {
            mViewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onBackPressed() {
        moveToCategoryMenuActivity();
    }

    private void initializeView() {
        applicationManager.setCategoryBackground(
                findViewById(R.id.category_item_container),
                applicationManager.getCategoryModelColor()
        );

        selectedCategoryModel = applicationManager.getCategoryModel();

        allCardListInSelectedCategory = cardRepository.getSingleCardListWithCategoryId(selectedCategoryModel.index,false);
        cardTitleLayout.setCategoryModelTitle(applicationManager.getCategoryModel().title);
        cardTitleLayout.refreshCardCountText(0, allCardListInSelectedCategory.size() + 1);
        cardTitleLayout.categoryTitle.setText(selectedCategoryModel.title);

        adapter = new CardImageAdapter(this, allCardListInSelectedCategory, glide, applicationManager);
        adapter.addNewCardViewAtFirst();
        mViewPager.setAdapter(adapter);
        OverScrollDecoratorHelper.setUpOverScroll(mViewPager);
        mViewPager.addOnPageChangeListener(viewPagerOnPageChangeListener);

        cardTitleLayout.refreshCardCountText(0, allCardListInSelectedCategory.size());
        cardTitleLayout.setBackButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCategoryMenuActivity();
            }
        });
    }

    private void moveToCategoryMenuActivity() {
        Intent intent = new Intent(context, CategoryMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private ViewPager.OnPageChangeListener viewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int pos) {
            adapter.releaseSpeakHandler();
            adapter.stopVideoView();
            showAndHideButtonContainerBy(pos);
            currentCardIndex = pos;
            cardTitleLayout.refreshCardCountText(pos, mViewPager.getAdapter().getCount());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private void showAndHideButtonContainerBy(int pos) {
        if (pos == 0) {
            buttonContainer.setVisibility(View.GONE);
        } else {
            buttonContainer.setVisibility(View.VISIBLE);
        }
    }

    private void deleteCard() {
        final CardView card = (CardView) adapter.viewCollection.get(mViewPager.getCurrentItem());
        String cardTitle = card.cardTitle.getText().toString();
        final int cardIndex = card.dataModel.cardIndex;
        String message = getResources().getString(R.string.delete_alert_message, cardTitle);

        View.OnClickListener positiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = setCurrentItem();
                if (deleteSelectedCard(cardIndex)) {
                    List<CardModel> cardList = cardRepository.getSingleCardListWithCategoryId((applicationManager.getCategoryModel().index),false);
                    mViewPager.removeAllViews();
                    adapter = new CardImageAdapter(CardViewPagerActivity.this, cardList, glide, applicationManager);
                    adapter.addNewCardViewAtFirst();
                    mViewPager.setAdapter(adapter);
                    mViewPager.setCurrentItem(currentItem);
                    showAndHideButtonContainerBy(currentItem);
                    cardTitleLayout.refreshCardCountText(mViewPager.getCurrentItem(), adapter.getCount());
                }
                dialog.dismiss();
            }
        };

        View.OnClickListener negativeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };

        View innerView = getLayoutInflater().inflate(R.layout.custom_confirm_dialog, null);
        TextView alertMessage = (TextView) innerView.findViewById(R.id.alert_message);
        alertMessage.setText(message);

        dialog = new CustomConfirmDialog(this, message, positiveListener, negativeListener);

    }

    private int setCurrentItem() {
        int currentItem = mViewPager.getCurrentItem();
        if (currentItem == adapter.getCount() - 1) {
            currentItem--;
        }
        return currentItem;
    }

    private boolean deleteSelectedCard(int cardIndex) {
        return cardRepository.deleteSingleCardWithCardIndex(selectedCategoryModel.index, cardIndex);
    }

    private void showSnackBarMessage(String intentKey) {
        PercentRelativeLayout rootLayout = (PercentRelativeLayout) findViewById(R.id.category_item_container);
        if(ApplicationConstants.INTENT_KEY_NEW_CARD.equals(intentKey)) {
            CustomSnackBar.styledSnackBarWithDuration(this, rootLayout, getApplicationContext().getResources().getString(R.string.add_new_card_success), 2000);
        }
    }

    private void showLoadingAnimation(){
        loadingViewLayout.setVisibility(View.VISIBLE);
        Glide.with(CardViewPagerActivity.this)
                .load(R.drawable.angelee)
                .asGif()
                .crossFade()
                .into(imageLoadingGif);
    }

}
