package act.sds.samsung.angelman.presentation.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static act.sds.samsung.angelman.presentation.util.FileUtil.IMAGE_FULL_PATH;
import static act.sds.samsung.angelman.presentation.util.FileUtil.VOICE_FULL_PATH;

public class ContentsUtil {

    public static final String IMAGE_FOLDER = "DCIM";
    public static String CONTENT_PATH = "content path";
    public static String CARD_TYPE = "card type";
    private static ContentsUtil instance = null;

    private ContentsUtil() {}

    public static ContentsUtil getInstance() {
        if (instance == null)
            instance = new ContentsUtil();

        return instance;
    }

    public static String getImageFolder() {
        return Environment.getExternalStorageDirectory() + File.separator + IMAGE_FULL_PATH;
    }

    public static String getVoiceFolder() {
        return Environment.getExternalStorageDirectory() + File.separator + VOICE_FULL_PATH;
    }

    public String getImagePath() {
        return getImageFolder() + File.separator + DateUtil.getDateNow() +".jpg";
    }

    public String getVideoPath() {
        return getImageFolder() + File.separator + DateUtil.getDateNow() +".mp4";
    }

    public String getThumbnailPath(String videoPath) {
        return videoPath.replace(".mp4",".jpg");
    }

    public String makeImagePathForAsset(String imgFileName){
        return "file:///android_asset/" + imgFileName;
    }

    public void saveImage(View decorView, String fileName){
        Bitmap bitmap = screenShot(decorView);

        saveImage(bitmap, fileName, 490, 112);
    }

    public void saveImage(Bitmap original, String fileName, int x, int y) {
        Matrix mtx = new Matrix();
        mtx.postRotate(90);
        Bitmap croppedBitmap = Bitmap.createBitmap(original, x, y, 852, 852, mtx, true);

        FileOutputStream output = null;
        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            file.createNewFile();
            output = new FileOutputStream(file);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap screenShot(View decorView) {
        int width = decorView.getWidth();
        int height = decorView.getHeight();
        decorView.buildDrawingCache();
        Bitmap drawingCache = decorView.getDrawingCache();

        Matrix matrix = new Matrix();
        float scaleX = 1080 / (float)width;
        float scaleY = 1920 / (float)height;
        matrix.postScale(scaleX, scaleY);
        matrix.postRotate(-90);

        return Bitmap.createBitmap(
                drawingCache, 0, 0, width, height, matrix, false);
    }
}