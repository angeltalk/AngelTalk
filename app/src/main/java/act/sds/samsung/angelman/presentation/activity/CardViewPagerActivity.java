package act.sds.samsung.angelman.presentation.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.percent.PercentFrameLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.kakaolink.KakaoLink;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.data.transfer.CardTransfer;
import act.sds.samsung.angelman.data.transfer.KaKaoTransfer;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.presentation.adapter.CardImageAdapter;
import act.sds.samsung.angelman.presentation.custom.CardCategoryLayout;
import act.sds.samsung.angelman.presentation.custom.CardView;
import act.sds.samsung.angelman.presentation.custom.CardViewPager;
import act.sds.samsung.angelman.presentation.custom.SnackBar;
import act.sds.samsung.angelman.presentation.util.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.DialogUtil;
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
    CardCategoryLayout titleLayout;

    @BindView(R.id.button_container)
    LinearLayout buttonContainer;

    @BindView(R.id.card_delete_button)
    ImageButton cardDeleteButton;

    @BindView(R.id.card_share_button)
    ImageButton cardShareButton;

    @BindView(R.id.view_pager)
    CardViewPager mViewPager;

    @BindView(R.id.category_item_title)
    TextView categoryTitle;

    @BindView(R.id.add_card_button_text)
    TextView addCardButtonText;

    private Uri downloadUrl;

    @OnClick(R.id.card_delete_button)
    public void deleteButtonOnClick() {
        deleteCard();
    }

    @OnClick(R.id.card_share_button)
    public void shareButtonOnClick() {

        final CardView card = (CardView) adapter.viewCollection.get(mViewPager.getCurrentItem());
        final CardModel cardModel = card.dataModel;

        cardTransfer.uploadCard(cardModel, new OnSuccessListener<Map<String,String>>() {
            @Override
            public void onSuccess(Map<String, String> resultMap) {
                String thumbnailUrl = resultMap.get("url");
                String key = resultMap.get("key");
                kaKaoTransfer.sendKakaoLinkMessage(context, key, thumbnailUrl, cardModel);

            }

        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, R.string.share_fail_message,Toast.LENGTH_SHORT);
            }
        });

    }




    public static String CATEGORY_COLOR = "categoryColor";
    public static final String INTENT_KEY_NEW_CARD = "isNewCard";

    List<CardModel> allCardListInSelectedCategory;
    int currentCardIndex = 0;

    private CategoryModel selectedCategoryModel;
    private CardImageAdapter adapter;
    private AlertDialog dialog;
    private RequestManager glide;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_card_view);
        ButterKnife.bind(this);
        glide = Glide.with(this);
        context = this;
        initializeView();

        if (getIntent().getBooleanExtra(INTENT_KEY_NEW_CARD, false)) {
            showAddNewCardSuccessMessage();
            mViewPager.setCurrentItem(1);
        }
    }

    private void initializeView() {
        applicationManager.setCategoryBackground(
                findViewById(R.id.category_item_container),
                applicationManager.getCategoryModelColor()
        );

        selectedCategoryModel = applicationManager.getCategoryModel();

        allCardListInSelectedCategory = cardRepository.getSingleCardListWithCategoryId(selectedCategoryModel.index);
        titleLayout.setCategoryModelTitle(applicationManager.getCategoryModel().title);
        titleLayout.refreshCardCountText(0, allCardListInSelectedCategory.size() + 1);
        categoryTitle.setText(selectedCategoryModel.title);

        adapter = new CardImageAdapter(this, allCardListInSelectedCategory, glide, applicationManager);
        adapter.addNewCardViewAtFirst();
        mViewPager.setAdapter(adapter);
        OverScrollDecoratorHelper.setUpOverScroll(mViewPager);
        mViewPager.addOnPageChangeListener(viewPagerOnPageChangeListener);

        titleLayout.refreshCardCountText(0, allCardListInSelectedCategory.size());
    }

    private ViewPager.OnPageChangeListener viewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int pos) {
            showAndHideButtonContainerBy(pos);
            currentCardIndex = pos;
            titleLayout.refreshCardCountText(pos, mViewPager.getAdapter().getCount());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private void showAndHideButtonContainerBy(int pos) {
        if (pos == 0) {
            buttonContainer.setVisibility(View.GONE);
            titleLayout.setAddCardTextButtonVisible(View.GONE);
        } else {
            buttonContainer.setVisibility(View.VISIBLE);
            titleLayout.setAddCardTextButtonVisible(View.VISIBLE);
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
                    List<CardModel> cardList = cardRepository.getSingleCardListWithCategoryId((applicationManager.getCategoryModel().index));
                    mViewPager.removeAllViews();
                    adapter = new CardImageAdapter(CardViewPagerActivity.this, cardList, glide, applicationManager);
                    adapter.addNewCardViewAtFirst();
                    mViewPager.setAdapter(adapter);
                    mViewPager.setCurrentItem(currentItem);
                    showAndHideButtonContainerBy(currentItem);
                    titleLayout.refreshCardCountText(mViewPager.getCurrentItem(), adapter.getCount());
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

        dialog = DialogUtil.buildCustomDialog(CardViewPagerActivity.this, innerView, positiveListener, negativeListener);
        dialog.show();
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

    private void showAddNewCardSuccessMessage() {
        PercentFrameLayout rootLayout = (PercentFrameLayout) findViewById(R.id.category_item_container);
        SnackBar.snackBarWithDuration(rootLayout, getApplicationContext().getResources().getString(R.string.add_new_card_success), ApplicationManager.SNACKBAR_DURATION);
    }

}
