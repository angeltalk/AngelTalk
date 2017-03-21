package act.sds.samsung.angelman.presentation.util;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;

import act.sds.samsung.angelman.R;

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

    public static int getPlusIconBy(@BackgroundColors int color) {
        int drawable=-1;
        switch (color){
            case RED:
                drawable = R.drawable.plus_red;
                break;
            case ORANGE:
                drawable = R.drawable.plus_orange;
                break;
            case YELLOW:
                drawable = R.drawable.plus_yellow;
                break;
            case GREEN:
                drawable = R.drawable.plus_green;
                break;
            case BLUE:
                drawable = R.drawable.plus_blue;
                break;
            case PURPLE:
                drawable = R.drawable.plus_purple;
                break;
        }
        return drawable;
    }

    public static int getCameraIconBy(@BackgroundColors int color) {
        int drawable=-1;
        switch (color){
            case RED:
                drawable = R.drawable.ic_camera_red;
                break;
            case ORANGE:
                drawable = R.drawable.ic_camera_orange;
                break;
            case YELLOW:
                drawable = R.drawable.ic_camera_yellow;
                break;
            case GREEN:
                drawable = R.drawable.ic_camera_green;
                break;
            case BLUE:
                drawable = R.drawable.ic_camera_blue;
                break;
            case PURPLE:
                drawable = R.drawable.ic_camera_purple;
                break;
        }
        return drawable;
    }

    public static int getGalleryIconBy(@BackgroundColors int color) {
        int drawable=-1;
        switch (color){
            case RED:
                drawable = R.drawable.ic_gallery_red;
                break;
            case ORANGE:
                drawable = R.drawable.ic_gallery_orange;
                break;
            case YELLOW:
                drawable = R.drawable.ic_gallery_yellow;
                break;
            case GREEN:
                drawable = R.drawable.ic_gallery_green;
                break;
            case BLUE:
                drawable = R.drawable.ic_gallery_blue;
                break;
            case PURPLE:
                drawable = R.drawable.ic_gallery_purple;
                break;
        }
        return drawable;
    }

    public static int getVideoIconBy(@BackgroundColors int color) {
        int drawable=-1;
        switch (color){
            case RED:
                drawable = R.drawable.ic_video_red;
                break;
            case ORANGE:
                drawable = R.drawable.ic_video_orange;
                break;
            case YELLOW:
                drawable = R.drawable.ic_video_yellow;
                break;
            case GREEN:
                drawable = R.drawable.ic_video_green;
                break;
            case BLUE:
                drawable = R.drawable.ic_video_blue;
                break;
            case PURPLE:
                drawable = R.drawable.ic_video_purple;
                break;
        }
        return drawable;
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
}
