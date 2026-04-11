package angeltalk.plus.presentation.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.charset.Charset;
import java.security.MessageDigest;

public class AngelManGlideTransform extends BitmapTransformation {

    public enum CornerType {
        ALL,
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP, BOTTOM, LEFT, RIGHT,
        OTHER_TOP_LEFT, OTHER_TOP_RIGHT, OTHER_BOTTOM_LEFT, OTHER_BOTTOM_RIGHT,
        DIAGONAL_FROM_TOP_LEFT, DIAGONAL_FROM_TOP_RIGHT
    }

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final int mRadius;
    private final int mDiameter;
    private final int mMargin;
    private final CornerType mCornerType;

    public AngelManGlideTransform(Context context, int radius, int margin, CornerType cornerType) {
        this(radius, margin, cornerType);
    }

    public AngelManGlideTransform(int radius, int margin, CornerType cornerType) {
        mRadius = radius;
        mDiameter = mRadius * 2;
        mMargin = margin;
        mCornerType = cornerType;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap source, int outWidth, int outHeight) {
        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        drawRoundRect(canvas, paint, width, height);
        return bitmap;
    }

    private void drawRoundRect(Canvas canvas, Paint paint, float width, float height) {
        float right = width - mMargin;
        float bottom = height - mMargin;

        switch (mCornerType) {
            case ALL:
                canvas.drawRoundRect(new RectF(mMargin, mMargin, right, bottom), mRadius, mRadius, paint);
                break;
            case TOP:
                drawTopRoundRect(canvas, paint, right, bottom);
                break;
            default:
                canvas.drawRoundRect(new RectF(mMargin, mMargin, right, bottom), mRadius, mRadius, paint);
                break;
        }
    }

    private void drawTopRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, mMargin, right, mMargin + mDiameter), mRadius, mRadius, paint);
        canvas.drawRect(new RectF(mMargin, mMargin + mRadius, right, bottom), paint);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        String id = "RoundedTransformation(radius=" + mRadius + ", margin=" + mMargin
                + ", diameter=" + mDiameter + ", cornerType=" + mCornerType.name() + ")";
        messageDigest.update(id.getBytes(CHARSET));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AngelManGlideTransform)) return false;
        AngelManGlideTransform other = (AngelManGlideTransform) o;
        return mRadius == other.mRadius
                && mMargin == other.mMargin
                && mCornerType == other.mCornerType;
    }

    @Override
    public int hashCode() {
        int result = mRadius;
        result = 31 * result + mMargin;
        result = 31 * result + mCornerType.hashCode();
        return result;
    }
}
