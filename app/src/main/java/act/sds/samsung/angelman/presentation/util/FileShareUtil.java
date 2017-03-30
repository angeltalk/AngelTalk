package act.sds.samsung.angelman.presentation.util;

import android.content.Context;
import android.provider.Settings;

import java.io.File;

public class FileShareUtil {

    private String deviceId;

    public FileShareUtil(Context context) {
        this.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getShareKey() {
        return deviceId + System.currentTimeMillis();
    }

    public String getShareReferencePath(String shareKey, String contentsPath) {
        return "share" + File.separator + shareKey + File.separator + ContentsUtil.getFileNameFromFullPath(contentsPath);
    }
}
