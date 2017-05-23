package act.angelman.presentation.util;

import android.content.Context;
import android.util.Log;

public class ResolutionUtil {
    static String TAG = ResolutionUtil.class.getSimpleName();

    public static float getDensity(Context con) {
        float density;
        density  = con.getResources().getDisplayMetrics().density;
        Log.d(TAG, "density = " + density);
        return density;
    }


    public static int getDpToPix(Context con, double dp) {
        float density;
        density  = con.getResources().getDisplayMetrics().density;
        Log.d(TAG, "density = " + density);
        return (int)(dp * density + 0.5);
    }

}
