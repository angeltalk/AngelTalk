package angeltalk.plus.presentation.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.TextView;

import angeltalk.plus.R;
import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.util.DialogUtil;

public class ShareMessengerSelectDialog {

    private final AlertDialog dialog;
    private final Context context;
    private TextView confirmButton;
    private ApplicationConstants.SHARE_MESSENGER_TYPE messengerType;
    private PackageManager pm;

    public ShareMessengerSelectDialog(Context context, final View.OnClickListener positiveOnClickListener) {
        this.context = context;
        this.pm = context.getPackageManager();
        View innerView = ((Activity) context).getLayoutInflater().inflate(R.layout.messenger_select_dialog, null);
        if(!isKakaotalkInstalled()){
            innerView.findViewById(R.id.item_kakaotalk).setVisibility(View.GONE);
        }
        if(!isWhatsAppInstalled()){
            innerView.findViewById(R.id.item_whatsapp).setVisibility(View.GONE);
        }
        innerView.findViewById(R.id.item_kakaotalk).setOnClickListener(itemClickListener);
        innerView.findViewById(R.id.item_message).setOnClickListener(itemClickListener);
        innerView.findViewById(R.id.item_whatsapp).setOnClickListener(itemClickListener);

        confirmButton = ((TextView) innerView.findViewById(R.id.confirm_button));

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
        int defaultSize = 315;
        if(isKakaotalkInstalled() && isWhatsAppInstalled()){
            defaultSize += 60;
        }

        DialogUtil.show(context, dialog, defaultSize);
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
                ((AppCompatRadioButton) dialog.findViewById(R.id.radio_whatsapp)).setChecked(false);
                messengerType = ApplicationConstants.SHARE_MESSENGER_TYPE.KAKAOTALK;
            }else if(v.getId() == R.id.item_whatsapp){
                ((AppCompatRadioButton) v.findViewById(R.id.radio_whatsapp)).setChecked(true);
                ((AppCompatRadioButton) dialog.findViewById(R.id.radio_message)).setChecked(false);
                ((AppCompatRadioButton) dialog.findViewById(R.id.radio_kakaotalk)).setChecked(false);
                messengerType = ApplicationConstants.SHARE_MESSENGER_TYPE.WHATSAPP;
            }else{
                ((AppCompatRadioButton) v.findViewById(R.id.radio_message)).setChecked(true);
                ((AppCompatRadioButton) dialog.findViewById(R.id.radio_kakaotalk)).setChecked(false);
                ((AppCompatRadioButton) dialog.findViewById(R.id.radio_whatsapp)).setChecked(false);
                messengerType = ApplicationConstants.SHARE_MESSENGER_TYPE.MESSAGE;
            }
        }
    };

    private boolean isKakaotalkInstalled() {
        try {
            if(pm == null){
                pm = context.getPackageManager();
            }
            pm.getPackageInfo(context.getString(R.string.kakao_package_name), PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    private boolean isWhatsAppInstalled() {
        try {
            if(pm == null){
                pm = context.getPackageManager();
            }
            pm.getPackageInfo(context.getString(R.string.whatsapp_package_name), PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }
}
