package act.sds.samsung.angelman.presentation.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.activity.CameraGallerySelectionActivity;

public class CardCategoryLayout extends RelativeLayout {

    private TextView addCardText;
    private TextView cardCount;
    private String categoryModelTitle;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_card_button_text:
                    startCameraGallerySelectionActivity();
                    break;
                case R.id.back_button:
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).finish();
                    }
                    break;
                default:
                    break;
            }
        }
    };

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
    }

    private void initUI() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_cardcategory_title, this);

        addCardText = (TextView) findViewById(R.id.add_card_button_text);
        addCardText.setOnClickListener(onClickListener);

        cardCount = ((TextView) findViewById(R.id.category_item_count));

        String title = "";

        if ( !isInEditMode() ) {
            title = this.categoryModelTitle;
        }

        ((TextView) findViewById(R.id.category_item_title)).setText(title);
        findViewById(R.id.back_button).setOnClickListener(onClickListener);
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

    public void setAddCardTextButtonVisible(int visible) {
        addCardText.setVisibility(visible);
    }

    public void setCardCountVisible(int visible) {
        cardCount.setVisibility(visible);
    }

    private void startCameraGallerySelectionActivity() {
        Intent intent = new Intent(getContext(), CameraGallerySelectionActivity.class);
        getContext().startActivity(intent);
    }
}
