package act.angelman.presentation.shadow;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import act.angelman.presentation.util.FileUtil;


@Implements(FileUtil.class)
public class ShadowFileUtil {

    @Implementation
    public static boolean isFileExist(String filePath) {
        return true;
    }
}
