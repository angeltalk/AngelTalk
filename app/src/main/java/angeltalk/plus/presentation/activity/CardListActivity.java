package angeltalk.plus.presentation.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import angeltalk.plus.AngelmanApplication;
import angeltalk.plus.R;
import angeltalk.plus.domain.model.CardModel;
import angeltalk.plus.domain.repository.CardRepository;
import angeltalk.plus.presentation.adapter.ChangeOrderRecyclerViewAdapter;
import angeltalk.plus.presentation.adapter.ShowHideRecyclerViewAdapter;
import angeltalk.plus.presentation.custom.CardListTabButton;
import angeltalk.plus.presentation.custom.CustomSnackBar;
import angeltalk.plus.presentation.listener.OnDataChangeListener;
import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.manager.ApplicationManager;
import angeltalk.plus.presentation.util.ResolutionUtil;
import angeltalk.plus.presentation.util.ResourcesUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardListActivity extends AbstractActivity {

    @Inject
    ApplicationManager applicationManager;

    @Inject
    CardRepository cardRepository;

    @BindView(R.id.category_item_title)
    TextView categoryItemTitle;

    @BindView(R.id.title_layout)
    RelativeLayout titleLayout;

    @BindView(R.id.show_hide_recycler_view)
    RecyclerView showHideRecyclerView;

    @BindView(R.id.change_order_recycler_view)
    RecyclerView changeOrderRecyclerView;

    @BindView(R.id.add_card_button)
    ImageView addCardButton;

    @BindView(R.id.show_hide_tab_button)
    CardListTabButton showHideTabButton;

    @BindView(R.id.change_order_tab_button)
    CardListTabButton changeOrderTabButton;

    private List<CardModel> cardList;
    private ShowHideRecyclerViewAdapter showHideRecyclerViewAdapter;
    private ChangeOrderRecyclerViewAdapter changeOrderRecyclerViewAdapter;
    public ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.Callback() {

        View keepView;
        Drawable keepBackground;

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.ACTION_STATE_DRAG | ItemTouchHelper.ACTION_STATE_IDLE;
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            Collections.swap(cardList, source.getAdapterPosition(), target.getAdapterPosition());
            changeOrderRecyclerViewAdapter.notifyItemMoved(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
        }


        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                changeOrderRecyclerViewAdapter.onItemSelected();
                if (keepView != null) {
                    ViewGroup.LayoutParams layoutParams = keepView.getLayoutParams();
                    layoutParams.height -= ResolutionUtil.getDpToPix(getApplicationContext(), 8);
                    keepView.setLayoutParams(layoutParams);
                    keepView.setBackground(keepBackground);
                    keepView.setPadding(0, 0, 0, 0);
                    keepView.refreshDrawableState();
                    setCurrentCardIndex();
                    cardRepository.updateCategoryCardIndex(cardList);
                }
            } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                keepView = viewHolder.itemView;
                keepBackground = keepView.getBackground();
                keepView.setBackground(getResources().getDrawable(R.drawable.card_item_shadow));
                ViewGroup.LayoutParams layoutParams = keepView.getLayoutParams();
                layoutParams.height += ResolutionUtil.getDpToPix(getApplicationContext(), 8);
                keepView.setLayoutParams(layoutParams);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        private void setCurrentCardIndex() {
            for(int i=0;i<cardList.size();i++){
                if(cardList.get(i).cardIndex == applicationManager.getCurrentCardIndex()) {
                    applicationManager.setCurrentCardIndex(cardList.size() - i);
                    break;
                }
            }
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }
    };
    private ItemTouchHelper cardListItemTouchHelper =   new ItemTouchHelper(itemTouchHelperCallback);

    OnDataChangeListener dataChangeListener = new OnDataChangeListener() {
        @Override
        public void onHideChange(int position, boolean hide) {
            cardList.get(position).hide = hide;
            cardRepository.updateSingleCardModelHide(cardList.get(position));

        }
    };

    public RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (recyclerView.getAdapter().getItemCount() > 6 && !recyclerView.canScrollVertically(1)) {
                addCardButton.setVisibility(View.GONE);
            } else {
                addCardButton.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        ResourcesUtil.setColorTheme(this, applicationManager.getCategoryModelColor());
        setContentView(R.layout.activity_card_list);
        ButterKnife.bind(this);
        cardList = cardRepository.getSingleCardListWithCategoryId(applicationManager.getCategoryModel().index);
        initView();

        if (getIntent().getBooleanExtra(ApplicationConstants.INTENT_KEY_SHARE_CARD, false)) {
            CustomSnackBar.styledSnackBarWithDuration(this, findViewById(R.id.activity_card_list),
                    getApplicationContext().getResources().getString(R.string.add_share_card_success), 2000);
        }
    }


    @Override
    public void onBackPressed() {
        moveToCardViewPagerActivity();
        finish();
    }

    @OnClick(R.id.back_button)
    public void onClickBackButton(View v) {
        moveToCardViewPagerActivity();
        finish();
    }

    @OnClick(R.id.add_card_button)
    public void onClickAddCardButton(View view) {
        Intent intent = new Intent(this, CameraGallerySelectionActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.show_hide_tab_button)
    public void onClickShowHideTabButton(View view) {
        if (!showHideTabButton.isSelected()) {
            showHideTabButton.setSelected(true);
            changeOrderTabButton.setSelected(false);
            showHideRecyclerViewAdapter = new ShowHideRecyclerViewAdapter(cardList, getApplicationContext(), dataChangeListener);
            showHideRecyclerView.setAdapter(showHideRecyclerViewAdapter);
            changeOrderRecyclerView.setVisibility(View.GONE);
            showHideRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.change_order_tab_button)
    public void onClickChangeOrderTabButton(View view) {
        if (!changeOrderTabButton.isSelected()) {
            showHideTabButton.setSelected(false);
            changeOrderTabButton.setSelected(true);
            changeOrderRecyclerViewAdapter = new ChangeOrderRecyclerViewAdapter(cardList, getApplicationContext(), cardListItemTouchHelper);
            changeOrderRecyclerView.setAdapter(changeOrderRecyclerViewAdapter);
            showHideRecyclerView.setVisibility(View.GONE);
            changeOrderRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        categoryItemTitle.setText(applicationManager.getCategoryModel().title);
        showHideRecyclerViewAdapter = new ShowHideRecyclerViewAdapter(cardList, getApplicationContext(), dataChangeListener);
        showHideRecyclerView.setAdapter(showHideRecyclerViewAdapter);
        showHideRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        showHideRecyclerView.addOnScrollListener(onScrollListener);

        changeOrderRecyclerViewAdapter = new ChangeOrderRecyclerViewAdapter(cardList, getApplicationContext(), cardListItemTouchHelper);
        changeOrderRecyclerView.setAdapter(changeOrderRecyclerViewAdapter);
        changeOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        changeOrderRecyclerView.addOnScrollListener(onScrollListener);
        cardListItemTouchHelper.attachToRecyclerView(changeOrderRecyclerView);
    }

    private void moveToCardViewPagerActivity() {
        Intent intent = new Intent(getApplicationContext(), CardViewPagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApplicationConstants.INTENT_KEY_LIST_BACK, true);
        getApplicationContext().startActivity(intent);
    }
}
