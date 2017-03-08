package act.sds.samsung.angelman.presentation.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;

public class CardView extends PercentRelativeLayout {

    private ImageView cardImage;
    private EditText cardTitleEditText;
    private TextView cardTitleTextView;
    private CardViewStatus status;
    private CardModel dataModel;
    private CardViewMode cardViewMode;

    public CardView(Context context) {
        super(context);
        initLayout();
        this.cardViewMode = CardViewMode.CARD_VIEW_MODE;
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initLayout();
        this.cardViewMode = CardViewMode.CARD_VIEW_MODE;
    }

    public ImageView getCardImage() {
        return cardImage;
    }

    public EditText getCardTitleEditText() {
        return cardTitleEditText;
    }

    public TextView getCardTitleTextView() {
        return cardTitleTextView;
    }

    public CardViewStatus getStatus() {
        return status;
    }

    public void setStatus(CardViewStatus status) {
        this.status = status;
    }

    public CardModel getDataModel() {
        return dataModel;
    }

    public CardViewMode getCardViewMode() {
        return cardViewMode;
    }

    public void setDataModel(CardModel dataModel) {
        this.dataModel = dataModel;
    }

    public void setCardViewLayoutMode(CardViewMode mode) {
        this.cardViewMode = mode;
    }

    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        mInflater.inflate(R.layout.layout_card_view, this, true);

        this.status = CardViewStatus.CARD_TITLE_SHOWN;

        cardImage = (ImageView)findViewById(R.id.card_image);

        cardTitleTextView = (TextView) findViewById(R.id.card_image_title);
        cardTitleEditText = (EditText) findViewById(R.id.card_image_title_edit);
    }

    public void setImageBitmap(Bitmap imageBitmap){
        cardImage.setImageBitmap(imageBitmap);
    }

    public void changeCardViewStatus() {
        switch(this.status) {
            case CARD_TITLE_EDITABLE:
                this.status = CardViewStatus.CARD_TITLE_SHOWN;
                cardTitleEditText.setVisibility(View.GONE);
                cardTitleTextView.setVisibility(VISIBLE);
                cardTitleTextView.setText(cardTitleEditText.getText().toString());
                break;
            case CARD_TITLE_SHOWN:
                this.status = CardViewStatus.CARD_TITLE_EDITABLE;
                cardTitleEditText.setVisibility(VISIBLE);
                cardTitleEditText.requestFocus();
                cardTitleTextView.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public enum CardViewStatus {
        CARD_TITLE_EDITABLE, CARD_TITLE_SHOWN
    }

    public enum CardViewMode {
        CARD_MAKE_MODE, CARD_VIEW_MODE
    }
}
