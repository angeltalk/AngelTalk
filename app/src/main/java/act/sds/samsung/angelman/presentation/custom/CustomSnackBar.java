package act.sds.samsung.angelman.presentation.custom;

import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.util.FontUtil;

public class CustomSnackBar {

    private static int SNACKBAR_DURATION = 3000;

    public static void snackBarWithDuration(View content, String message){
        final Snackbar snackbar = Snackbar.make(content, message, Snackbar.LENGTH_INDEFINITE);
        ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTypeface(FontUtil.setFont(content.getContext(), FontUtil.FONT_REGULAR));
        snackbar.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, SNACKBAR_DURATION);

    }
}
