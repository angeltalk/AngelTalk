package act.angelman.presentation.shadow;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@SuppressWarnings({"UnusedDeclaration", "Unchecked"})
@Implements(ResourcesCompat.class)
public class ShadowResourcesCompat {

    public static int capturedId;

    @Implementation
    public static Drawable getDrawable(Resources res, int id, Resources.Theme theme) throws Resources.NotFoundException {
        capturedId = id;
        return res.getDrawable(id);
    }
}
