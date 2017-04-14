package act.angelman.presentation.shadow;

import android.view.View;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import act.angelman.presentation.util.ContentsUtil;


@Implements(ContentsUtil.class)
public class ShadowContentUtil {

    @Implementation
    public static void saveImage(View decorView, String fileName) {

    }
}
