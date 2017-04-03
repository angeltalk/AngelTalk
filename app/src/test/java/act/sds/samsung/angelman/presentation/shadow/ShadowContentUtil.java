package act.sds.samsung.angelman.presentation.shadow;

import android.view.View;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import act.sds.samsung.angelman.presentation.util.ContentsUtil;


@Implements(ContentsUtil.class)
public class ShadowContentUtil {

    @Implementation
    public static void saveImage(View decorView, String fileName) {

    }
}
