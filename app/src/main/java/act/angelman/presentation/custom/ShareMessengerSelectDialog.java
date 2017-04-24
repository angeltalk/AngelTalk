package act.angelman.presentation.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;

import act.angelman.R;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.util.DialogUtil;

public class ShareMessengerSelectDialog {

    private final AlertDialog dialog;
    private final Context context;
    private FontTextView confirmButton;
    private ApplicationConstants.SHARE_MESSENGER_TYPE messengerType;

    public ShareMessengerSelectDialog(Context context, boolean isKakaotalkInstalled, final View.OnClickListener positiveOnClickListener) {
        this.context = context;
        View innerView = ((Activity) context).getLayoutInflater().inflate(R.layout.messenger_select_dialog, null);
        if(!isKakaotalkInstalled){
            innerView.findViewById(R.id.item_kakaotalk).setVisibility(View.GONE);
        }
        innerView.findViewById(R.id.item_kakaotalk).setOnClickListener(itemClickListener);
        innerView.findViewById(R.id.item_message).setOnClickListener(itemClickListener);
        confirmButton = ((FontTextView) innerView.findViewById(R.id.confirm_button));

        dialog = DialogUtil.buildCustomDialog(context, innerView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(messengerType);
                positiveOnClickListener.onClick(v);
                dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show() {
        DialogUtil.show(context, dialog, 320);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    private final View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            confirmButton.setEnabled(true);
            confirmButton.setTextColor(context.getResources().getColor(R.color.simple_background_red));
            if(v.getId() == R.id.item_kakaotalk){
                ((AppCompatRadioButton) v.findViewById(R.id.radio_kakaotalk)).setChecked(true);
                ((AppCompatRadioButton) dialog.findViewById(R.id.radio_message)).setChecked(false);
                messengerType = ApplicationConstants.SHARE_MESSENGER_TYPE.KAKAOTALK;
            }else{
                ((AppCompatRadioButton) v.findViewById(R.id.radio_message)).setChecked(true);
                ((AppCompatRadioButton) dialog.findViewById(R.id.radio_kakaotalk)).setChecked(false);
                messengerType = ApplicationConstants.SHARE_MESSENGER_TYPE.MESSAGE;
            }
        }
    };
}
