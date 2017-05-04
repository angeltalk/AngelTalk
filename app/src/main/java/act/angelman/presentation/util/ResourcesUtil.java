package act.angelman.presentation.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;

import act.angelman.R;

public class ResourcesUtil {
    public static final int RED = 0;
    public static final int ORANGE = 1;
    public static final int YELLOW = 2;
    public static final int GREEN = 3;
    public static final int BLUE = 4;
    public static final int PURPLE = 5;

    @IntDef(flag = true, value = {RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE})
    public @interface BackgroundColors { }

    public static int getCardViewLayoutBackgroundBy(@BackgroundColors int color) {
        int drawable=-1;
        switch (color){
            case RED:
                drawable = R.drawable.background_gradient_red;
                break;
            case ORANGE:
                drawable = R.drawable.background_gradient_orange;
                break;
            case YELLOW:
                drawable = R.drawable.background_gradient_yellow;
                break;
            case GREEN:
                drawable = R.drawable.background_gradient_green;
                break;
            case BLUE:
                drawable = R.drawable.background_gradient_blue;
                break;
            case PURPLE:
                drawable = R.drawable.background_gradient_purple;
                break;
        }
        return drawable;
    }

    public static void setColorTheme(Context context, @BackgroundColors int color) {
        switch (color){
            case RED:
                context.setTheme(R.style.AppTheme_Red);
                break;
            case ORANGE:
                context.setTheme(R.style.AppTheme_Orange);
                break;
            case YELLOW:
                context.setTheme(R.style.AppTheme_Yellow);
                break;
            case GREEN:
                context.setTheme(R.style.AppTheme_Green);
                break;
            case BLUE:
                context.setTheme(R.style.AppTheme_Blue);
                break;
            case PURPLE:
                context.setTheme(R.style.AppTheme_Purple);
                break;
            default:
                break;
        }
    }

    public static void setViewBackground(View view, @BackgroundColors int color, Context context){
        view.setBackground(
                ResourcesCompat.getDrawable(
                        context.getResources(),
                        getCardViewLayoutBackgroundBy(color),
                        context.getTheme()
                )
        );
    }

    public static Drawable getDrawable(Context context, int drawableId) {
        return ResourcesCompat.getDrawable(
                context.getResources(),
                drawableId,
                context.getTheme()
        );
    }
}
