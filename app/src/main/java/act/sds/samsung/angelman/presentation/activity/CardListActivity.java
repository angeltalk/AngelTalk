package act.sds.samsung.angelman.presentation.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.presentation.adapter.ChangeOrderRecyclerViewAdapter;
import act.sds.samsung.angelman.presentation.adapter.ShowHideRecyclerViewAdapter;
import act.sds.samsung.angelman.presentation.custom.CardListTabButton;
import act.sds.samsung.angelman.presentation.custom.FontTextView;
import act.sds.samsung.angelman.presentation.listener.OnDataChangeListener;
import act.sds.samsung.angelman.presentation.manager.ApplicationConstants;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardListActivity extends AppCompatActivity {

    @Inject
    ApplicationManager applicationManager;

    @Inject
    CardRepository cardRepository;

    @BindView(R.id.category_item_title)
    FontTextView categoryItemTitle;

    @BindView(R.id.title_layout)
    RelativeLayout titleLayout;

    @BindView(R.id.show_hide_recycler_view)
    RecyclerView showHideRecyclerView;

    @BindView(R.id.change_order_recycler_view)
    RecyclerView changeOrderRecyclerView;


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
                    keepView.setBackground(keepBackground);
                    keepView.setPadding(0, 0, 0, 0);
                    keepView.refreshDrawableState();
                    cardRepository.updateCategoryCardIndex(cardList);
                }
            } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                keepView = viewHolder.itemView;
                keepBackground = viewHolder.itemView.getBackground();
                viewHolder.itemView.setBackground(getResources().getDrawable(R.drawable.card_item_shadow));
            }
            super.onSelectedChanged(viewHolder, actionState);
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

            changeOrderRecyclerViewAdapter.setCardModelList(cardList);
            changeOrderRecyclerViewAdapter.notifyDataSetChanged();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_card_list);
        ButterKnife.bind(this);
        cardList = cardRepository.getSingleCardListWithCategoryId(applicationManager.getCategoryModel().index);
        initView();
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
            showHideRecyclerViewAdapter = new ShowHideRecyclerViewAdapter(cardList, applicationManager.getCategoryModelColor(), getApplicationContext(), dataChangeListener);
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
            changeOrderRecyclerViewAdapter = new ChangeOrderRecyclerViewAdapter(cardList, applicationManager.getCategoryModelColor(), getApplicationContext());
            changeOrderRecyclerView.setAdapter(changeOrderRecyclerViewAdapter);
            showHideRecyclerView.setVisibility(View.GONE);
            changeOrderRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        titleLayout.setBackgroundResource(
                ResourcesUtil.getTitleBackgroundColor(
                        applicationManager.getCategoryModelColor()
                )
        );
        categoryItemTitle.setText(applicationManager.getCategoryModel().title);
        showHideRecyclerViewAdapter = new ShowHideRecyclerViewAdapter(cardList, applicationManager.getCategoryModelColor(), getApplicationContext(), dataChangeListener);
        showHideRecyclerView.setAdapter(showHideRecyclerViewAdapter);
        showHideRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        changeOrderRecyclerViewAdapter = new ChangeOrderRecyclerViewAdapter(cardList, applicationManager.getCategoryModelColor(), getApplicationContext());
        changeOrderRecyclerView.setAdapter(changeOrderRecyclerViewAdapter);
        changeOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        cardListItemTouchHelper.attachToRecyclerView(changeOrderRecyclerView);
    }

    private void moveToCardViewPagerActivity() {
        Intent intent = new Intent(getApplicationContext(), CardViewPagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApplicationConstants.INTENT_KEY_REFRESH_CARD, true);
        getApplicationContext().startActivity(intent);
    }
}
