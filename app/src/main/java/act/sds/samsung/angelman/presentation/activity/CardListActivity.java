package act.sds.samsung.angelman.presentation.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.custom.FontTextView;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardListActivity extends AppCompatActivity {

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.category_item_title)
    FontTextView categoryItemTitle;

    @BindView(R.id.change_order_bar)
    View changeOrderBar;

    @BindView(R.id.title_card_list_container)
    RelativeLayout titleCardListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_card_list);
        ButterKnife.bind(this);

        titleCardListContainer.setBackgroundResource(
                ResourcesUtil.getTitleBackgroundColor(
                        applicationManager.getCategoryModelColor()
                )
        );

        categoryItemTitle.setText(applicationManager.getCategoryModel().title);
        changeOrderBar.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.back_button)
    public void onClickBackButton(View v) {
        finish();
    }
}
