package act.sds.samsung.angelman.presentation.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.activity.CameraGallerySelectionActivity;

public class CardCategoryLayout extends RelativeLayout {
    private TextView addCardButton;
    private View backButton;
    private TextView categoryItemCountTextView;
    private TextView categoryItemTitleTextView;

    private View.OnClickListener addCardButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startCameraGallerySelectionActivity();
        }
    };
    private OnClickListener backButtonOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).finish();
            }
        }
    };

    public CardCategoryLayout(Context context) {
        super(context);
        initLayout();
    }

    public CardCategoryLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public CardCategoryLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    public void refreshCategoryItemCount(int count, int total) {
        if (count == 0) {
            String itemCount = String.format(getResources().getString(R.string.total_card), total - 1);
            categoryItemCountTextView.setText(itemCount);
        } else {
            categoryItemCountTextView.setText(count + " / " + (total - 1));
        }
    }

    public void setAddCardTextButtonVisible(int visible) {
        addCardButton.setVisibility(visible);
    }

    public void setCardCountVisible(int visible) {
        categoryItemCountTextView.setVisibility(visible);
    }

    private void initLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_cardcategory_title, this);

        addCardButton = (TextView) findViewById(R.id.add_card_button_text);
        backButton = findViewById(R.id.back_button);
        categoryItemCountTextView = ((TextView) findViewById(R.id.category_item_count));
        categoryItemTitleTextView = (TextView) findViewById(R.id.category_item_title);

        addCardButton.setOnClickListener(addCardButtonOnClickListener);
        backButton.setOnClickListener(backButtonOnClickListener);

        if (!isInEditMode()) {
            categoryItemTitleTextView.setText(((AngelmanApplication) getContext().getApplicationContext()).getCategoryModel().title);
        }
    }

    private void startCameraGallerySelectionActivity() {
        Intent intent = new Intent(getContext(), CameraGallerySelectionActivity.class);
        getContext().startActivity(intent);
    }
}
