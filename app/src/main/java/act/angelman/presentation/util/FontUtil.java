package act.angelman.presentation.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FontUtil {
    public static String FONT_DEMILIGHT = "NotoSansKR-DemiLight-Hestia.otf";
    public static String FONT_MEDIUM = "NotoSansKR-Medium-Hestia.otf";
    public static String FONT_REGULAR = "NotoSansKR-Regular-Hestia.otf";

    public static Typeface setFont(Context context, String font){
        return Typeface.createFromAsset(context.getAssets(), font);
    }

    public static void setGlobalFont(View view, String font) {
        if (view != null) {
            if(view instanceof ViewGroup){
                ViewGroup vg = (ViewGroup)view;
                int vgCnt = vg.getChildCount();
                for(int i=0; i < vgCnt; i++){
                    View v = vg.getChildAt(i);
                    if(v instanceof TextView){
                        ((TextView) v).setTypeface(FontUtil.setFont(view.getContext(), font));
                    }
                    setGlobalFont(v, font);
                }
            }
        }
    }
}
 