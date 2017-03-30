package act.sds.samsung.angelman.presentation.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static act.sds.samsung.angelman.presentation.util.ContentsUtil.getImageFolder;

public class FileUtil {

    public static final String ANGELMAN_FOLDER = "angelman";
    public static final String VOICE_FOLDER = "voice";
    public static final String TEMP_FOLDER = "temp";

    public static final String IMAGE_FULL_PATH = ANGELMAN_FOLDER + File.separator + ContentsUtil.IMAGE_FOLDER;
    public static final String VOICE_FULL_PATH = ANGELMAN_FOLDER + File.separator + VOICE_FOLDER;
    public static final String TEMP_FULL_PATH = ANGELMAN_FOLDER + File.separator + TEMP_FOLDER;

    public static void initExternalStorageFolder() {
        File rootFolder = new File(Environment.getExternalStorageDirectory() + File.separator + ANGELMAN_FOLDER);

        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }

        File imageFolder = new File(ContentsUtil.getImageFolder());
        if (!imageFolder.exists()) {
            imageFolder.mkdir();
        }

        File voiceFolder = new File(ContentsUtil.getVoiceFolder());
        if (!voiceFolder.exists()) {
            voiceFolder.mkdir();
        }

        File tempFolder = new File(ContentsUtil.getTempFolder());
        if(!tempFolder.exists()) {
            tempFolder.mkdir();
        }
    }

    public static void copyDefaultAssetImagesToImageFolder(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("contents");
        } catch (IOException e) {
            Log.e("AngelmanApplication", "Failed to get asset file list.", e);
        }
        if (files != null) for (String fileName : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("contents" + File.separator + fileName);
                File outFile = new File(getImageFolder(), fileName);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("AngelmanApplication", "Failed to copy asset file: " + fileName, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e("AngelmanApplication", "Failed to close image input file.", e);
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        Log.e("AngelmanApplication", "Failed to close image output file.", e);
                    }
                }
            }
        }
    }

    public static void removeFile(String path){
        try {
            File file = new File(path);
            file.delete();
        }catch (NullPointerException e){
            Log.e("Exception", "Not found file " + path);
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static boolean isContentExist(String contentPath){
        return  new File(contentPath).exists();
    }

}
