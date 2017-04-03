package act.sds.samsung.angelman.presentation.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;
import act.sds.samsung.angelman.presentation.util.DialogUtil;

public class CustomConfirmDialog {

    private static final int DEFAULT_WIDTH = 310;
    private static final int DEFAULT_HEIGHT = 235;
    private final AlertDialog dialog;

    public CustomConfirmDialog(Context context, String message, View.OnClickListener positiveOnClickListener, View.OnClickListener negativeOnClickListener) {
        View innerView = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_confirm_dialog, null);
        ((TextView) innerView.findViewById(R.id.alert_message)).setText(message);
        dialog = DialogUtil.buildCustomDialog(context, innerView, positiveOnClickListener, negativeOnClickListener);
        dialog.show();
        dialog.getWindow().setLayout((int) ContentsUtil.convertDpToPixel(DEFAULT_WIDTH, context),(int) ContentsUtil.convertDpToPixel(DEFAULT_HEIGHT, context));
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
