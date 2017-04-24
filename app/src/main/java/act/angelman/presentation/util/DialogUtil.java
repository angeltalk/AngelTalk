package act.angelman.presentation.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import act.angelman.R;

public class DialogUtil {

    public static AlertDialog buildCustomDialog(Context context, View innerView, View.OnClickListener positiveClickListener, View.OnClickListener negativeClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        innerView.findViewById(R.id.confirm_button).setOnClickListener(positiveClickListener);
        innerView.findViewById(R.id.cancel_button).setOnClickListener(negativeClickListener);
        builder.setView(innerView);

        return builder.create();
    }

    public static AlertDialog buildCustomDialog(Context context, View innerView, View.OnClickListener negativeClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        innerView.findViewById(R.id.cancel_button).setOnClickListener(negativeClickListener);
        builder.setView(innerView);

        return builder.create();
    }
}
