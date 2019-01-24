package angeltalk.plus.presentation.custom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import angeltalk.plus.R;
import angeltalk.plus.presentation.activity.CameraGallerySelectionActivity;

public class AddCardView extends RelativeLayout implements View.OnClickListener{

    private Context context;
    public AddCardView(Context context) {
        super(context);
        this.context = context;
        initLayout();
        setOnClickListener(this);
    }

    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        mInflater.inflate(R.layout.layout_add_card_view, this, true);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, CameraGallerySelectionActivity.class);
        context.startActivity(intent);
    }
}
