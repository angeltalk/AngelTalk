package act.angelman.presentation.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import act.angelman.R;
import act.angelman.presentation.util.DialogUtil;

public class CustomConfirmDialog {

    private final AlertDialog dialog;
    private final Context context;

    public CustomConfirmDialog(Context context, String message, View.OnClickListener positiveOnClickListener) {
        this.context = context;
        View innerView = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_confirm_dialog, null);
        ((TextView) innerView.findViewById(R.id.alert_message)).setText(message);
        dialog = DialogUtil.buildCustomDialog(context, innerView, positiveOnClickListener, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void show() {
        DialogUtil.show(context, dialog);
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
