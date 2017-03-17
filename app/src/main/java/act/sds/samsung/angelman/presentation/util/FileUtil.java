package act.sds.samsung.angelman.presentation.util;

import android.util.Log;

import java.io.File;

public class FileUtil {
    public static void removeFile(String path){
        try {
            File file = new File(path);
            file.delete();
        }catch (NullPointerException e){
            Log.e("Exception", "Not found file " + path);
        }
    }
}
