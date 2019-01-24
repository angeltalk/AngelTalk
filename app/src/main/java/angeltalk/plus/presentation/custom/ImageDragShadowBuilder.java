package angeltalk.plus.presentation.custom;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;


public class ImageDragShadowBuilder extends View.DragShadowBuilder {
    private Point mScaleFactor;

    public ImageDragShadowBuilder(View v) {
        super(v);
    }

    @Override
    public void onProvideShadowMetrics (Point size, Point touch) {
        int width;
        int height;

        width = getView().getWidth() * 3;
        height = getView().getHeight() * 3;

        size.set(width, height);
        mScaleFactor = size;

        touch.set(width / 2, height / 2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        canvas.scale(mScaleFactor.x/(float)getView().getWidth(), mScaleFactor.y/(float)getView().getHeight());
        getView().draw(canvas);
    }

}
