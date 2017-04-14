package act.angelman.presentation.shadow;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(ThumbnailUtils.class)
public class ShadowThumbnailUtil {

    @Implementation
    public static Bitmap createVideoThumbnail(String filePath, int kind) {
        return Bitmap.createBitmap(10,10,null);
    }
}
