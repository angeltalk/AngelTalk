package act.sds.samsung.angelman.presentation.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.util.FontUtil;

public class FontEditText extends EditText {

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context context, AttributeSet attrs){
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.FontTextView
        );

        String fontName = attributeArray.getString(R.styleable.FontTextView_font);

        setTypeface(FontUtil.setFont(context, fontName));

        attributeArray.recycle();
    }
}
