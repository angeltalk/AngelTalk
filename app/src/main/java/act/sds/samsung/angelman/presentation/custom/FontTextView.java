package act.sds.samsung.angelman.presentation.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.util.FontUtil;

public class FontTextView extends TextView {

    private Context context;

    public FontTextView(Context context, AttributeSet attrs) {
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

    public void setFontType(String font){
        setTypeface(FontUtil.setFont(context, font));
    }
}
