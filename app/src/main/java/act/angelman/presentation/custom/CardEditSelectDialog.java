package act.angelman.presentation.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import act.angelman.R;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.DialogUtil;


public class CardEditSelectDialog {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 340;
    private final AlertDialog dialog;
    private Context context;

    public CardEditSelectDialog (Context context, final View.OnClickListener positiveOnClickListener) {
        this.context = context;
        View innerView = ((Activity) context).getLayoutInflater().inflate(R.layout.card_edit_select_dialog, null);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.card_edit_content_text :
                        view.setTag(ApplicationConstants.CARD_EDIT_TYPE.CONTENT);
                        break;
                    case R.id.card_edit_name_text :
                        view.setTag(ApplicationConstants.CARD_EDIT_TYPE.NAME);
                        break;
                    case R.id.card_edit_voice_text :
                        view.setTag(ApplicationConstants.CARD_EDIT_TYPE.VOICE);
                        break;
                }
                positiveOnClickListener.onClick(view);
                dismiss();
            }
        };

        innerView.findViewById(R.id.card_edit_content_text).setOnClickListener(onClickListener);
        innerView.findViewById(R.id.card_edit_name_text).setOnClickListener(onClickListener);
        innerView.findViewById(R.id.card_edit_voice_text).setOnClickListener(onClickListener);

        dialog = DialogUtil.buildCustomDialog(context, innerView, new View.OnClickListener() {
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
