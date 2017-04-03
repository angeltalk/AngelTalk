package act.sds.samsung.angelman.presentation.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.google.common.base.Strings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import act.sds.samsung.angelman.domain.model.CardModel;

import static act.sds.samsung.angelman.presentation.util.FileUtil.copyFile;

public class ContentsUtil {

    public static final String ANGELMAN_FOLDER = "angelman";
    public static final String CONTENT_FOLDER = "contents";
    public static final String VOICE_FOLDER = "voice";
    public static final String TEMP_FOLDER = "temp";
    public static final String CONTENT_FULL_PATH = ANGELMAN_FOLDER + File.separator + CONTENT_FOLDER;
    public static final String VOICE_FULL_PATH = ANGELMAN_FOLDER + File.separator + VOICE_FOLDER;
    public static final String TEMP_FULL_PATH = ANGELMAN_FOLDER + File.separator + TEMP_FOLDER;

    public static String CONTENT_PATH = "content path";
    public static String CARD_TYPE = "card type";

    public static String getContentFolder() {
        return Environment.getExternalStorageDirectory() + File.separator + CONTENT_FULL_PATH;
    }

    public static String getVoiceFolder() {
        return Environment.getExternalStorageDirectory() + File.separator + VOICE_FULL_PATH;
    }

    public static String getTempFolder() {
        return Environment.getExternalStorageDirectory() + File.separator + TEMP_FULL_PATH;
    }

    public static String getImagePath() {
        return getContentFolder() + File.separator + DateUtil.getDateNow() + ".jpg";
    }

    public static String getVideoPath() {
        return getContentFolder() + File.separator + DateUtil.getDateNow() +".mp4";
    }

    public static String getVoicePath() {
        return getVoiceFolder() + File.separator + DateUtil.getDateNow() + ".3gdp";
    }

    public static String getThumbnailPath(String videoPath) {
        return videoPath.replace(".mp4",".jpg");
    }

    public static void saveImage(View decorView, String fileName) {
        Bitmap bitmap = screenShot(decorView);

        saveImage(bitmap, fileName, 490, 112);
    }

    public static void saveImage(Bitmap original, String fileName, int x, int y) {
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

    private static Bitmap screenShot(View decorView) {
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
    public static void saveVideoThumbnail(String videoPath) {
        String thumbNailPath = ContentsUtil.getThumbnailPath(videoPath);
        File fileCacheItem = new File(thumbNailPath);
        OutputStream out = null;

        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);

        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static String getContentNameFromContentPath(String contentPath) {
        String[] splitPath = contentPath.split(File.separator);
        return splitPath[splitPath.length - 1];
    }

    public static File getContentFile(String path) {
        if(Strings.isNullOrEmpty(path)) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    public static void copySharedFiles(CardModel cardModel) {
        File[] files = new File(getTempFolder()).listFiles();
        for (File file : files) {
            try {
                if (file.getAbsolutePath().contains("mp4")) {
                    copyFile(file, new File(cardModel.contentPath));
                } else if (file.getAbsolutePath().contains("jpg") || file.getAbsolutePath().contains("png")) {
                    if (cardModel.cardType == CardModel.CardType.VIDEO_CARD) {
                        copyFile(file, new File(cardModel.thumbnailPath));
                    } else {
                        copyFile(file, new File(cardModel.contentPath));
                    }
                } else if (file.getAbsolutePath().contains("3gdp")) {
                    copyFile(file, new File(cardModel.voicePath));
                }
            } catch (IOException e) {
                Log.e("error", "copyShardFile error : " + e.getStackTrace());
            }
        }
    }
}