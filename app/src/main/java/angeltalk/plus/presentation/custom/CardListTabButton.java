package angeltalk.plus.presentation.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import angeltalk.plus.R;
public class CardListTabButton extends RelativeLayout {
    private Context context;

    public TextView buttonTextView;

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
        bindViews();
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
    private void bindViews() {
        buttonTextView = findViewById(R.id.button_text_view);
        tabIndicator = findViewById(R.id.tab_indicator);
    }
}
