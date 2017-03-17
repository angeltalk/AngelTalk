package act.sds.samsung.angelman.presentation.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.util.DialogUtil;

public class CustomConfirmDialog {

    private final AlertDialog dialog;

    public CustomConfirmDialog(Context context, String message, View.OnClickListener positiveOnClickListener, View.OnClickListener negativeOnClickListener) {
        View innerView = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_confirm_dialog, null);
        ((TextView) innerView.findViewById(R.id.alert_message)).setText(message);
        dialog = DialogUtil.buildCustomDialog(context, innerView, positiveOnClickListener, negativeOnClickListener);
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
