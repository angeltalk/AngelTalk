package act.sds.samsung.angelman.presentation.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import act.sds.samsung.angelman.R;

public class DialogUtil {

    public static AlertDialog buildCustomDialog(Context context, View innerView, View.OnClickListener positiveClickListener, View.OnClickListener negativeClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        innerView.findViewById(R.id.confirm).setOnClickListener(positiveClickListener);
        innerView.findViewById(R.id.cancel).setOnClickListener(negativeClickListener);
        builder.setView(innerView);

        return builder.create();
    }
}
