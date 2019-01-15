package act.angelman.presentation.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import act.angelman.R;
import act.angelman.domain.model.CardModel;

public class CardView extends ConstraintLayout {
    public static final int MODE_MAKE_CARD = 0;
    public static final int MODE_VIEW_CARD = 1;

    public ImageView cardImage;
    public VideoCardTextureView cardVideo;
    public EditText cardTitleEdit;
    public TextView cardTitle;
    public ImageView playButton;

    public CardModel dataModel;
    public CardViewStatus status;
    public int mode = CardView.MODE_VIEW_CARD;

    public void setDataModel(CardModel dataModel) {
        this.dataModel = dataModel;
    }

    public void setCardViewLayoutMode(int mode) {
        this.mode = mode;
    }

    public enum CardViewStatus {
        CARD_TITLE_EDITABLE, CARD_TITLE_SHOWN
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initLayout(context);
    }

    public CardView(Context context) {
        super(context);
        initLayout(context);
    }

    private void initLayout(Context context) {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        mInflater.inflate(R.layout.layout_card_view, this, true);

        this.status = CardViewStatus.CARD_TITLE_SHOWN;

        cardImage = (ImageView)findViewById(R.id.card_image);
        cardVideo = (VideoCardTextureView) findViewById(R.id.card_video);

        playButton = (ImageView) findViewById(R.id.play_button);
        cardTitle = (TextView) findViewById(R.id.card_image_title);
        cardTitleEdit = (EditText) findViewById(R.id.card_image_title_edit);
    }

    public void setImageBitmap(Bitmap imageBitmap){
        cardImage.setImageBitmap(imageBitmap);
    }

    public void changeCardViewStatus() {
        switch(this.status) {
            case CARD_TITLE_EDITABLE:
                this.status = CardViewStatus.CARD_TITLE_SHOWN;
                cardTitleEdit.setVisibility(View.GONE);
                cardTitle.setVisibility(VISIBLE);
                cardTitle.setText(cardTitleEdit.getText().toString());
                break;
            case CARD_TITLE_SHOWN:
                this.status = CardViewStatus.CARD_TITLE_EDITABLE;
                cardTitleEdit.setVisibility(VISIBLE);
                cardTitleEdit.requestFocus();
                cardTitle.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
