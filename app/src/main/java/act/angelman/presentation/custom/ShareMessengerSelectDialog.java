package act.angelman.presentation.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import act.angelman.R;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.DialogUtil;

public class ShareMessengerSelectDialog {


    private static final int DEFAULT_WIDTH = 350;
    private static final int DEFAULT_HEIGHT = 350;
    private final AlertDialog dialog;


    public ShareMessengerSelectDialog(Context context, View.OnClickListener positiveOnClickListener) {

        View innerView = ((Activity) context).getLayoutInflater().inflate(R.layout.messenger_select_dialog, null);
        dialog = DialogUtil.buildCustomDialog(context, innerView, positiveOnClickListener, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout((int) ContentsUtil.convertDpToPixel(DEFAULT_WIDTH, context), (int) ContentsUtil.convertDpToPixel(DEFAULT_HEIGHT, context));
    }

    public void dismiss() {
        dialog.dismiss();
    }


}
