package angeltalk.plus.presentation.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntDef;
import androidx.core.content.res.ResourcesCompat;
import android.view.View;

import angeltalk.plus.R;

public class ResourcesUtil {
    public static final int RED = 0;
    public static final int ORANGE = 1;
    public static final int YELLOW = 2;
    public static final int GREEN = 3;
    public static final int BLUE = 4;
    public static final int PURPLE = 5;
    public static final int PINK = 6;
    public static final int TEAL = 7;
    public static final int NAVY = 8;
    public static final int BROWN = 9;
    public static final int CORAL = 10;
    public static final int AMBER = 11;
    public static final int LIME = 12;
    public static final int CYAN = 13;
    public static final int INDIGO = 14;
    public static final int ROSE = 15;
    public static final int SKYBLUE = 16;
    public static final int SAGE = 17;
    public static final int GOLD = 18;
    public static final int LAVENDER = 19;
    public static final int MINT = 20;
    public static final int SLATE = 21;
    public static final int PEACH = 22;
    public static final int VIOLET = 23;
    public static final int CHERRY = 24;
    public static final int FOREST = 25;
    public static final int OCEAN = 26;
    public static final int RUST = 27;
    public static final int PLUM = 28;
    public static final int CARAMEL = 29;

    @IntDef(flag = true, value = {RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE, PINK, TEAL, NAVY, BROWN,
            CORAL, AMBER, LIME, CYAN, INDIGO, ROSE, SKYBLUE, SAGE, GOLD, LAVENDER,
            MINT, SLATE, PEACH, VIOLET, CHERRY, FOREST, OCEAN, RUST, PLUM, CARAMEL})
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
            case PINK:
                drawable = R.drawable.background_gradient_pink;
                break;
            case TEAL:
                drawable = R.drawable.background_gradient_teal;
                break;
            case NAVY:
                drawable = R.drawable.background_gradient_navy;
                break;
            case BROWN:
                drawable = R.drawable.background_gradient_brown;
                break;
            case CORAL:
                drawable = R.drawable.background_gradient_coral;
                break;
            case AMBER:
                drawable = R.drawable.background_gradient_amber;
                break;
            case LIME:
                drawable = R.drawable.background_gradient_lime;
                break;
            case CYAN:
                drawable = R.drawable.background_gradient_cyan;
                break;
            case INDIGO:
                drawable = R.drawable.background_gradient_indigo;
                break;
            case ROSE:
                drawable = R.drawable.background_gradient_rose;
                break;
            case SKYBLUE:
                drawable = R.drawable.background_gradient_skyblue;
                break;
            case SAGE:
                drawable = R.drawable.background_gradient_sage;
                break;
            case GOLD:
                drawable = R.drawable.background_gradient_gold;
                break;
            case LAVENDER:
                drawable = R.drawable.background_gradient_lavender;
                break;
            case MINT:
                drawable = R.drawable.background_gradient_mint;
                break;
            case SLATE:
                drawable = R.drawable.background_gradient_slate;
                break;
            case PEACH:
                drawable = R.drawable.background_gradient_peach;
                break;
            case VIOLET:
                drawable = R.drawable.background_gradient_violet;
                break;
            case CHERRY:
                drawable = R.drawable.background_gradient_cherry;
                break;
            case FOREST:
                drawable = R.drawable.background_gradient_forest;
                break;
            case OCEAN:
                drawable = R.drawable.background_gradient_ocean;
                break;
            case RUST:
                drawable = R.drawable.background_gradient_rust;
                break;
            case PLUM:
                drawable = R.drawable.background_gradient_plum;
                break;
            case CARAMEL:
                drawable = R.drawable.background_gradient_caramel;
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
