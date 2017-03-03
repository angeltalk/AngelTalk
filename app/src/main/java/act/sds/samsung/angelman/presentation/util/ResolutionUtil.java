package act.sds.samsung.angelman.presentation.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class ResolutionUtil {
    static String TAG = ResolutionUtil.class.getSimpleName();
    /**
     * 단말기 density 구함
     * @param con
     * 사용법 : if(getDensity(context) == 2f && (float으로 형변환해서 사용 해야함.)
     */
    public static float getDensity(Context con) {
        float density = 0.0f;
        density  = con.getResources().getDisplayMetrics().density;
        Log.d(TAG, "density = " + density);
        return density;
    }

    /**
     * px을 dp로 변환
     * @param con
     * @param px
     * @return dp
     */
    public static int getPxToDp(Context con, int px) {
        float density = 0.0f;
        density  = con.getResources().getDisplayMetrics().density;
        Log.d(TAG, "density = " + density);
        return (int)(px / density);
    }

    /**
     * dp를 px로 변환
     * @param con
     * @param dp
     * @return px
     */
    public static int getDpToPix(Context con, double dp) {
        float density = 0.0f;
        density  = con.getResources().getDisplayMetrics().density;
        Log.d(TAG, "density = " + density);
        return (int)(dp * density + 0.5);
    }

    /**
     * 단말기 가로 해상도 구하기
     * @param activity
     * @return width
     */
    public static int getScreenWidth(Activity activity) {
        int width = 0;
        width = activity.getWindowManager().getDefaultDisplay().getWidth();
        Log.i(TAG, "Screen width = " + width);
        return width;
    }

    /**
     * 단말기 세로 해상도 구하기
     * @param activity
     * @return hight
     */
    public static int getScreenHeight(Activity activity) {
        int height = 0;
        height = activity.getWindowManager().getDefaultDisplay().getHeight();
        Log.i(TAG, "Screen height = " + height);
        return height;
    }

    /**
     * 단말기 가로 해상도 구하기
     * @param context
     */
    public static int getScreenWidth(Context context) {
        Display dis = ((WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = dis.getWidth();
        Log.i(TAG, "Screen Width = " + width);
        return width;
    }

    /**
     * 단말기 세로 해상도 구하기
     * @param context
     */
    public static int getScreenHeight(Context context) {
        Display dis = ((WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = dis.getHeight();
        Log.i(TAG, "Screen height = " + height);
        return height;
    }
}
