package angeltalk.plus.presentation.custom;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import angeltalk.plus.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CardTitleLayout extends RelativeLayout {

    @BindView(R.id.list_card_button)
    public ImageView listCardButton;

    @BindView(R.id.category_item_count)
    public TextView cardCount;

    @BindView(R.id.category_item_title)
    public TextView categoryTitle;

    private String categoryModelTitle;

    public CardTitleLayout(Context context, AttributeSet attrs) {
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
        findViewById(R.id.back_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }
            }
        });
    }

    public void setBackButtonOnClickListener(OnClickListener onClickListener) {
        findViewById(R.id.back_button).setOnClickListener(onClickListener);
    }

    public void refreshCardCountText(int count, int total) {
        if (count == 0) {
            String message = String.format(getResources().getString(R.string.total_card), total - 1);
            cardCount.setText(message);
        } else {
            cardCount.setText(count + " / " + (total - 1));
        }
    }

    public void hideCardCountText() {
        cardCount.setVisibility(View.GONE);
    }
    public void hideListCardButton() {
        listCardButton.setVisibility(View.GONE);
    }

}
