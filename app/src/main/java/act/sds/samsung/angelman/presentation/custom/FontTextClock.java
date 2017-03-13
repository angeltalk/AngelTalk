package act.sds.samsung.angelman.presentation.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextClock;
import android.widget.TextView;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.util.FontUtil;

public class FontTextClock extends TextClock {

    private Context context;

    public FontTextClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setCustomFont(attrs);
    }

    private void setCustomFont(AttributeSet attrs){
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.FontTextView
        );

        String fontName = attributeArray.getString(R.styleable.FontTextView_font);

        setTypeface(FontUtil.setFont(context, fontName));

        attributeArray.recycle();
    }
}
