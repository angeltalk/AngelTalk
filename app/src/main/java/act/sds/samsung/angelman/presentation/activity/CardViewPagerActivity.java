package act.sds.samsung.angelman.presentation.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.percent.PercentFrameLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.presentation.adapter.CardImageAdapter;
import act.sds.samsung.angelman.presentation.custom.CardCategoryLayout;
import act.sds.samsung.angelman.presentation.custom.CardView;
import act.sds.samsung.angelman.presentation.custom.CardViewPager;
import act.sds.samsung.angelman.presentation.custom.SnackBar;
import act.sds.samsung.angelman.presentation.util.DialogUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class CardViewPagerActivity extends AbstractActivity {

    public  static final String INTENT_KEY_NEW_CARD = "isNewCard";
    public  static String CATEGORY_COLOR = "categoryColor";
    private static final int SNACKBAR_DURATION = 3000;
    int currentCardIndex = 0;

    @Inject
    CardRepository cardRepository;

    @BindView(R.id.view_pager)
    public CardViewPager viewPager;

    @BindView(R.id.card_delete_button)
    public ImageButton deleteButton;

    @BindView(R.id.title_container)
    public CardCategoryLayout titleLayout;

    private CardImageAdapter cardImageAdapter;
    private AlertDialog dialog;
    private RequestManager glide;
    private CategoryModel selectedCategoryModel;
    List<CardModel> allCardListInSelectedCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_card_view);
        ButterKnife.bind(this);

        setCategoryBackground(R.id.category_item_container);

        glide = Glide.with(this);

        Intent intent = getIntent();

        selectedCategoryModel = ((AngelmanApplication) getApplicationContext()).getCategoryModel();

        allCardListInSelectedCategory = cardRepository.getSingleCardListWithCategoryId(selectedCategoryModel.index);
        titleLayout.refreshCategoryItemCount(0, allCardListInSelectedCategory.size() + 1);

        cardImageAdapter = new CardImageAdapter(this, allCardListInSelectedCategory, glide);
        cardImageAdapter.addNewCardViewAtFirst();
        viewPager.setAdapter(cardImageAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(viewPager);

        viewPager.addOnPageChangeListener(pageChangeListener);

        TextView categoryTitle = (TextView) findViewById(R.id.category_item_title);
        categoryTitle.setText(selectedCategoryModel.title);

        titleLayout.refreshCategoryItemCount(0, allCardListInSelectedCategory.size());

        if (intent.getBooleanExtra(INTENT_KEY_NEW_CARD, false)) {
            showAddNewCardSuccessMessage();
            viewPager.setCurrentItem(1);
        }
    }

    @OnClick(R.id.card_delete_button)
    public void onClickDeleteButton(View v){

        final CardView card = (CardView) cardImageAdapter.viewCollection.get(viewPager.getCurrentItem());
        String cardTitle = card.getCardTitleTextView().getText().toString();
        final int cardIndex = card.getDataModel().cardIndex;
        String message = getResources().getString(R.string.delete_alert_message, cardTitle);

        View.OnClickListener positiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = setCurrentItem();
                if (deleteSelectedCard(cardIndex)) {
                    List<CardModel> cardList = cardRepository.getSingleCardListWithCategoryId(((AngelmanApplication) getApplicationContext()).getCategoryModel().index);
                    viewPager.removeAllViews();
                    cardImageAdapter = new CardImageAdapter(CardViewPagerActivity.this, cardList, glide);
                    cardImageAdapter.addNewCardViewAtFirst();
                    viewPager.setAdapter(cardImageAdapter);
                    viewPager.setCurrentItem(currentItem);
                    showOrHideDeleteButtonByIndex(currentItem);
                    titleLayout.refreshCategoryItemCount(viewPager.getCurrentItem(), cardImageAdapter.getCount());
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

        View innerView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView alertMessage = (TextView) innerView.findViewById(R.id.alert_message);
        alertMessage.setText(message);

        dialog = DialogUtil.buildCustomDialog(CardViewPagerActivity.this, innerView, positiveListener, negativeListener);
        dialog.show();
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int pos) {
            showOrHideDeleteButtonByIndex(pos);
            currentCardIndex = pos;
            titleLayout.refreshCategoryItemCount(pos, viewPager.getAdapter().getCount());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private void showOrHideDeleteButtonByIndex(int pos) {
        if(pos == 0){
            deleteButton.setVisibility(View.GONE);
            titleLayout.setAddCardTextButtonVisible(View.GONE);
        }else{
            deleteButton.setVisibility(View.VISIBLE);
            titleLayout.setAddCardTextButtonVisible(View.VISIBLE);
        }
    }


    private int setCurrentItem() {
        int currentItem = viewPager.getCurrentItem();
        if(currentItem == cardImageAdapter.getCount() - 1){
            currentItem--;
        }
        return currentItem;
    }

    private boolean deleteSelectedCard(int cardIndex) {
        return cardRepository.deleteSingleCardWithCardIndex(selectedCategoryModel.index, cardIndex);
    }

    private void showAddNewCardSuccessMessage() {
        PercentFrameLayout rootLayout = (PercentFrameLayout) findViewById(R.id.category_item_container);
        SnackBar.snackBarWithDuration(rootLayout, getApplicationContext().getResources().getString(R.string.add_new_card_success), SNACKBAR_DURATION);
    }
}
