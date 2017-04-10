package act.sds.samsung.angelman.presentation.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import act.sds.samsung.angelman.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CardListTabButton extends RelativeLayout {
    private Context context;

    @BindView(R.id.button_text_view)
    public FontTextView buttonTextView;

    @BindView(R.id.tab_indicator)
    public View tabIndicator;

    private boolean selected;

    public CardListTabButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        initUI(attrs);
    }

    private void initUI(AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.card_list_tab_button, this);
        ButterKnife.bind(this);

        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.CardListTabButton
        );

        setSelected(attributeArray.getBoolean(R.styleable.CardListTabButton_selected, false));
        buttonTextView.setText(attributeArray.getString(R.styleable.CardListTabButton_buttonText));

        attributeArray.recycle();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if(selected) {
            buttonTextView.setTextColor(context.getResources().getColor(R.color.white));
            tabIndicator.setVisibility(VISIBLE);
        } else {
            buttonTextView.setTextColor(context.getResources().getColor(R.color.white_B2));
            tabIndicator.setVisibility(INVISIBLE);
        }
    }

    public boolean isSelected() {
        return selected;
    }
}
