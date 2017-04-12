package act.sds.samsung.angelman.presentation.custom;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.util.FontUtil;
import act.sds.samsung.angelman.presentation.util.ResolutionUtil;

public class CustomSnackBar {

    private final static double CUSTOM_SNACKBAR_HEIGHT = 73.5;

    public static void styledSnackBarWithDuration(Context context, View content, String message, int duration){
        final Snackbar snackbar = makeStyledSnackBar(context, content, message);

        snackbar.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, duration);
    }

    @NonNull
    private static Snackbar makeStyledSnackBar(Context context, View content, String message) {
        final Snackbar snackbar = Snackbar.make(content, message, Snackbar.LENGTH_INDEFINITE);
        View snackbarView = snackbar.getView();
        ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
        layoutParams.height = ResolutionUtil.getDpToPix(context, CUSTOM_SNACKBAR_HEIGHT);
        snackbarView.setLayoutParams(layoutParams);
        ((TextView) snackbarView.findViewById(R.id.snackbar_text)).setTypeface(FontUtil.setFont(content.getContext(), FontUtil.FONT_REGULAR));
        return snackbar;
    }
}
