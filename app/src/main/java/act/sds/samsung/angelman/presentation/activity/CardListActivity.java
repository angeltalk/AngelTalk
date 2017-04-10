package act.sds.samsung.angelman.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.presentation.adapter.CardListRecyclerViewAdapter;
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

    @BindView(R.id.change_order_bar)
    View changeOrderBar;

    @BindView(R.id.title_layout)
    RelativeLayout titleLayout;

    @BindView(R.id.card_list_recycler_view)
    RecyclerView cardListRecyclerView;

    private CardListRecyclerViewAdapter cardListRecyclerViewAdapter;
    private List<CardModel> cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_card_list);
        ButterKnife.bind(this);

        cardList = cardRepository.getSingleCardListWithCategoryId(applicationManager.getCategoryModel().index);
        cardListRecyclerViewAdapter = new CardListRecyclerViewAdapter(cardList, applicationManager.getCategoryModelColor(), getApplicationContext(), new OnDataChangeListener(){
            @Override
            public void onHideChange(int position, boolean hide) {
                cardList.get(position).hide = hide;
                cardRepository.updateSingleCardModelHide(cardList.get(position));
            }
        });
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
    public void onAddCardButtonClick(View view) {
        Intent intent = new Intent(this, CameraGallerySelectionActivity.class);
        startActivity(intent);
    }

    private void initView() {
        titleLayout.setBackgroundResource(
                ResourcesUtil.getTitleBackgroundColor(
                        applicationManager.getCategoryModelColor()
                )
        );
        categoryItemTitle.setText(applicationManager.getCategoryModel().title);

        changeOrderBar.setVisibility(View.INVISIBLE);

        cardListRecyclerView.setAdapter(cardListRecyclerViewAdapter);
        cardListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void moveToCardViewPagerActivity() {
        Intent intent = new Intent(getApplicationContext(), CardViewPagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApplicationConstants.INTENT_KEY_REFRESH_CARD, true);
        getApplicationContext().startActivity(intent);
    }
}
