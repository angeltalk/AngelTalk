package act.angelman.presentation.shadow;

import android.view.ViewTreeObserver;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(ViewTreeObserver.class)
public class ShadowViewTreeObserver {

    @Implementation
    public void addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener listener) {
    }
}
