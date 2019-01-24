package angeltalk.plus.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

import angeltalk.plus.AngelmanApplication;
import angeltalk.plus.R;
import angeltalk.plus.domain.model.CardModel;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.domain.repository.CardRepository;
import angeltalk.plus.network.transfer.CardTransfer;
import angeltalk.plus.network.transfer.KaKaoTransfer;
import angeltalk.plus.network.transfer.MessageTransfer;
import angeltalk.plus.presentation.adapter.CardImageAdapter;
import angeltalk.plus.presentation.custom.CardEditSelectDialog;
import angeltalk.plus.presentation.custom.CardTitleLayout;
import angeltalk.plus.presentation.custom.CardView;
import angeltalk.plus.presentation.custom.CardViewPager;
import angeltalk.plus.presentation.custom.CustomConfirmDialog;
import angeltalk.plus.presentation.custom.CustomSnackBar;
import angeltalk.plus.presentation.custom.ShareMessengerSelectDialog;
import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.manager.ApplicationManager;
import angeltalk.plus.presentation.util.ResourcesUtil;
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

    @Inject
    MessageTransfer messageTransfer;

    @BindView(R.id.title_container)
    CardTitleLayout cardTitleLayout;

    @BindView(R.id.button_container)
    LinearLayout buttonContainer;

    @BindView(R.id.card_edit_button)
    ImageButton cardEditButton;

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

    @BindView(R.id.category_item_container)
    ConstraintLayout categoryItemContainer;


    @BindView(R.id.list_card_button)
    ImageView listCardButton;

    @OnClick(R.id.list_card_button)
    public void onClickListCardButtonText(View v) {
        stopPlayingCard();
        Intent intent = new Intent(this, CardListActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.card_delete_button)
    public void deleteButtonOnClick() {
        stopPlayingCard();
        deleteCard();
    }

    @Override
    protected void onPause() {
        isForegroundRunning = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        isForegroundRunning = false;
        super.onDestroy();
    }

    boolean isForegroundRunning = true;

    @OnClick(R.id.card_share_button)
    public void shareButtonOnClick() {
        stopPlayingCard();

        if (!cardTransfer.isConnectedToNetwork()) {
            Toast.makeText(context, R.string.network_disconnected, Toast.LENGTH_LONG).show();
            return;
        }

        new ShareMessengerSelectDialog(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ApplicationConstants.SHARE_MESSENGER_TYPE selectType = ((ApplicationConstants.SHARE_MESSENGER_TYPE) v.getTag());
                final CardModel cardModel = getCardModel(mViewPager.getCurrentItem());
                showLoadingAnimation();

                cardTransfer.uploadCard(cardModel, new OnSuccessListener<Map<String, String>>() {
                    @Override
                    public void onSuccess(Map<String, String> resultMap) {
                        String thumbnailUrl = resultMap.get("url");
                        final String key = resultMap.get("key");
                        if (!isForegroundRunning) {
                            loadingViewLayout.setVisibility(View.GONE);
                            return;
                        }

                        if (selectType == ApplicationConstants.SHARE_MESSENGER_TYPE.KAKAOTALK) {
                            loadingViewLayout.setVisibility(View.GONE);
                            kaKaoTransfer.sendKakaoLinkMessage(context, key, thumbnailUrl, cardModel);
                        } else {
                            messageTransfer.sendMessage(selectType, key, cardModel, new MessageTransfer.OnCompleteListener() {
                                @Override
                                public void onComplete() {
                                    loadingViewLayout.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingViewLayout.setVisibility(View.GONE);
                        Toast.makeText(context, R.string.share_fail_message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).show();
    }

    @Override
    protected void onResume() {
        isForegroundRunning = true;
        super.onResume();
    }

    @OnClick(R.id.card_edit_button)
    public void editButtonOnClick() {
        stopPlayingCard();
        new CardEditSelectDialog(context, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardModel cardModel = ((CardView) cardImageAdapter.viewCollection.get(mViewPager.getCurrentItem())).dataModel;
                if (view.getTag() == ApplicationConstants.CardEditType.CONTENT) {
                    moveToContentEditActivity(cardModel);
                } else if (view.getTag() == ApplicationConstants.CardEditType.NAME) {
                    moveToNameEditActivity(cardModel);
                } else if (view.getTag() == ApplicationConstants.CardEditType.VOICE) {
                    moveToVoiceEditActivity(cardModel);
                }
            }
        }).show();
    }

    private void moveToContentEditActivity(CardModel cardModel) {
        Intent intent = new Intent(context, CameraGallerySelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ApplicationConstants.EDIT_CARD_ID, cardModel._id);
        intent.putExtra(ApplicationConstants.EDIT_TYPE, ApplicationConstants.CardEditType.CONTENT.value());
        startActivity(intent);
    }

    private void moveToNameEditActivity(CardModel cardModel) {
        String cardId = cardModel._id;
        Intent intent = new Intent(context, MakeCardActivity.class);
        intent.putExtra(ApplicationConstants.EDIT_CARD_ID, cardId);
        intent.putExtra(ApplicationConstants.EDIT_TYPE, ApplicationConstants.CardEditType.NAME.value());
        startActivity(intent);
    }

    private void moveToVoiceEditActivity(CardModel cardModel) {
        String cardId = cardModel._id;
        Intent intent = new Intent(context, MakeCardActivity.class);
        intent.putExtra(ApplicationConstants.EDIT_CARD_ID, cardId);
        intent.putExtra(ApplicationConstants.EDIT_TYPE, ApplicationConstants.CardEditType.VOICE.value());
        startActivity(intent);
    }

    List<CardModel> allCardListInSelectedCategory;

    private CategoryModel selectedCategoryModel;
    public CardImageAdapter cardImageAdapter;
    private CustomConfirmDialog dialog;
    private RequestManager glide;
    Context context;

    public CardModel getCardModel(int index) {
        CardView card = (CardView) cardImageAdapter.viewCollection.get(index);
        return card.dataModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        ResourcesUtil.setColorTheme(this, applicationManager.getCategoryModelColor());
        setContentView(R.layout.activity_card_view);
        ButterKnife.bind(this);
        glide = Glide.with(this);
        context = this;
        initializeView();

        if (getIntent().getBooleanExtra(ApplicationConstants.INTENT_KEY_NEW_CARD, false)) {
            showSnackBarMessage(getApplicationContext().getResources().getString(R.string.add_new_card_success));
            mViewPager.setCurrentItem(1);

        } else if (getIntent().getBooleanExtra(ApplicationConstants.INTENT_KEY_REFRESH_CARD, false)) {

            mViewPager.setCurrentItem(0);

        } else if (getIntent().getBooleanExtra(ApplicationConstants.INTENT_KEY_LIST_BACK, false)) {

            if (!setViewPagerCurrentItem(applicationManager.getCurrentCardIndex())) {
                mViewPager.setCurrentItem(1);
            }

        } else if (getIntent().getBooleanExtra(ApplicationConstants.INTENT_KEY_CARD_EDITED, false)) {
            showSnackBarMessage(getApplicationContext().getResources().getString(R.string.card_edit_success_message));
            if (!setViewPagerCurrentItem(applicationManager.getCurrentCardIndex())) {
                mViewPager.setCurrentItem(1);
            }
        }

        applicationManager.setCurrentCardIndex(allCardListInSelectedCategory.get(mViewPager.getCurrentItem()).cardIndex);
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

        allCardListInSelectedCategory = cardRepository.getSingleCardListWithCategoryId(selectedCategoryModel.index, false);
        cardTitleLayout.setCategoryModelTitle(applicationManager.getCategoryModel().title);
        cardTitleLayout.refreshCardCountText(0, allCardListInSelectedCategory.size() + 1);
        cardTitleLayout.categoryTitle.setText(selectedCategoryModel.title);

        cardImageAdapter = new CardImageAdapter(this, allCardListInSelectedCategory, glide);
        cardImageAdapter.addNewCardViewAtFirst();
        mViewPager.setAdapter(cardImageAdapter);
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
        stopPlayingCard();
        Intent intent = new Intent(context, CategoryMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void stopPlayingCard() {
        cardImageAdapter.releaseSpeakHandler();
        cardImageAdapter.stopVideoView();
    }

    private ViewPager.OnPageChangeListener viewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int pos) {
            stopPlayingCard();
            showAndHideButtonContainerBy(pos);
            applicationManager.setCurrentCardIndex(allCardListInSelectedCategory.get(pos).cardIndex);
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
        final CardView card = (CardView) cardImageAdapter.viewCollection.get(mViewPager.getCurrentItem());
        String cardTitle = card.cardTitle.getText().toString();
        final int cardIndex = card.dataModel.cardIndex;
        String message = getResources().getString(R.string.delete_alert_message, cardTitle);

        View.OnClickListener positiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = setCurrentItem();
                if (deleteSelectedCard(cardIndex)) {
                    List<CardModel> cardList = cardRepository.getSingleCardListWithCategoryId((applicationManager.getCategoryModel().index), false);
                    mViewPager.removeAllViews();
                    cardImageAdapter = new CardImageAdapter(CardViewPagerActivity.this, cardList, glide);
                    cardImageAdapter.addNewCardViewAtFirst();
                    mViewPager.setAdapter(cardImageAdapter);
                    mViewPager.setCurrentItem(currentItem);
                    showAndHideButtonContainerBy(currentItem);
                    cardTitleLayout.refreshCardCountText(mViewPager.getCurrentItem(), cardImageAdapter.getCount());
                }
                dialog.dismiss();
            }
        };

        View innerView = getLayoutInflater().inflate(R.layout.custom_confirm_dialog, null);
        TextView alertMessage = (TextView) innerView.findViewById(R.id.alert_message);
        alertMessage.setText(message);

        dialog = new CustomConfirmDialog(this, message, positiveListener);
        dialog.show();
    }

    private int setCurrentItem() {
        int currentItem = mViewPager.getCurrentItem();
        if (currentItem == cardImageAdapter.getCount() - 1) {
            currentItem--;
        }
        return currentItem;
    }

    private boolean deleteSelectedCard(int cardIndex) {
        return cardRepository.deleteSingleCardWithCardIndex(selectedCategoryModel.index, cardIndex);
    }

    private void showSnackBarMessage(String message) {
        CustomSnackBar.styledSnackBarWithDuration(context, findViewById(R.id.category_item_container), message, 2000);
    }

    private void showLoadingAnimation() {
        loadingViewLayout.setVisibility(View.VISIBLE);
        Glide.with(CardViewPagerActivity.this)
                .load(R.drawable.angelee)
                .asGif()
                .crossFade()
                .into(imageLoadingGif);
    }


    private boolean setViewPagerCurrentItem(int beforeCardIndex) {
        for (int i = 0; i < allCardListInSelectedCategory.size(); i++) {
            if (allCardListInSelectedCategory.get(i).cardIndex == beforeCardIndex) {
                mViewPager.setCurrentItem(i);
                return true;
            }
        }
        return false;
    }
}
