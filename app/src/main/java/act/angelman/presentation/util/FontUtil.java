package act.angelman.presentation.util;

import android.content.Context;

import com.tsengvn.typekit.Typekit;

import act.angelman.R;

public class FontUtil {
    public static void overrideDefaultFont(Context context){
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(context, context.getString(R.string.font_regular)))
                .addBold(Typekit.createFromAsset(context, context.getString(R.string.font_medium)))
                .add("light", Typekit.createFromAsset(context, context.getString(R.string.font_demilight)));
    }
}
 