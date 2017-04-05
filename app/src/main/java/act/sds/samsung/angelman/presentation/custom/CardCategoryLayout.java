package act.sds.samsung.angelman.presentation.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.activity.CameraGallerySelectionActivity;
import act.sds.samsung.angelman.presentation.activity.CardListActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardCategoryLayout extends RelativeLayout {

    @BindView(R.id.list_card_button)
    public ImageView listCardText;

    @BindView(R.id.category_item_count)
    public TextView cardCount;

    private String categoryModelTitle;

    public CardCategoryLayout(Context context) {
        super(context);
        initUI();
    }

    public CardCategoryLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public void setCategoryModelTitle(String categoryModelTitle) {
        this.categoryModelTitle = categoryModelTitle;
        ((TextView) findViewById(R.id.category_item_title)).setText(categoryModelTitle);
    }

    private void initUI() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_cardcategory_title, this);
        ButterKnife.bind(this);

        String title = "";
        if ( !isInEditMode() ) {
            title = this.categoryModelTitle;
        }

        ((TextView) findViewById(R.id.category_item_title)).setText(title);
    }

    @OnClick(R.id.list_card_button)
    public void onClickListCardButtonText(View v) {
        Intent intent = new Intent(getContext(), CardListActivity.class);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.back_button)
    public void onClickBackButton(View v) {
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).finish();
        }
    }

    public CardCategoryLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    public void refreshCardCountText(int count, int total) {
        if (count == 0) {
            String message = String.format(getResources().getString(R.string.total_card), total - 1);
            cardCount.setText(message);
        } else {
            cardCount.setText(count + " / " + (total - 1));
        }
    }

    public void hideCardCountText(boolean hide) {
        if(hide){
            cardCount.setVisibility(View.GONE);
        } else {
            cardCount.setVisibility(View.VISIBLE);
        }
    }

    public void setAddCardTextButtonVisible(int visible) {
        listCardText.setVisibility(visible);
    }

    public void setCardCountVisible(int visible) {
        cardCount.setVisibility(visible);
    }

    private void startCameraGallerySelectionActivity() {
        Intent intent = new Intent(getContext(), CameraGallerySelectionActivity.class);
        getContext().startActivity(intent);
    }
}
