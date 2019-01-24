package angeltalk.plus.presentation.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import angeltalk.plus.R;
import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.util.DialogUtil;


public class CardEditSelectDialog {

    private final AlertDialog dialog;
    private final Context context;
    private final View.OnClickListener positiveOnClickListener;

    public CardEditSelectDialog (Context context, View.OnClickListener positiveOnClickListener) {
        this.context = context;
        this.positiveOnClickListener = positiveOnClickListener;

        View innerView = ((Activity) context).getLayoutInflater().inflate(R.layout.card_edit_select_dialog, null);
        innerView.findViewById(R.id.card_edit_content_text).setOnClickListener(onItemClickListener);
        innerView.findViewById(R.id.card_edit_name_text).setOnClickListener(onItemClickListener);
        innerView.findViewById(R.id.card_edit_voice_text).setOnClickListener(onItemClickListener);

        dialog = DialogUtil.buildCustomDialog(context, innerView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show() {
        DialogUtil.show(context, dialog, 340);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        switch (view.getId()){
            case R.id.card_edit_content_text :
                view.setTag(ApplicationConstants.CardEditType.CONTENT);
                break;
            case R.id.card_edit_name_text :
                view.setTag(ApplicationConstants.CardEditType.NAME);
                break;
            case R.id.card_edit_voice_text :
                view.setTag(ApplicationConstants.CardEditType.VOICE);
                break;
        }
        positiveOnClickListener.onClick(view);
        dismiss();
        }
    };

}
