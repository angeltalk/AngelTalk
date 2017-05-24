package act.angelman.presentation.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.google.common.base.Strings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import act.angelman.domain.model.CardModel;
import act.angelman.domain.model.CardTransferModel;
import act.angelman.presentation.manager.ApplicationManager;

import static act.angelman.presentation.util.FileUtil.copyFile;
import static android.graphics.Bitmap.createBitmap;
import static android.media.ThumbnailUtils.OPTIONS_RECYCLE_INPUT;

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

    private static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;

    public static String getContentFolder(Context context) {
        return context.getApplicationContext().getFilesDir() + File.separator + CONTENT_FULL_PATH;
    }

    public static String getVoiceFolder(Context context) {
        return context.getApplicationContext().getFilesDir() + File.separator + VOICE_FULL_PATH;
    }
    public static String getTempFolder(Context context) {

        return context.getApplicationContext().getFilesDir() + File.separator + TEMP_FULL_PATH;
    }

    public static String getImagePath(Context context) {
        return getContentFolder(context) + File.separator + DateUtil.getDateNow() + ".jpg";
    }

    public static String getVideoPath(Context context) {
        return getContentFolder(context) + File.separator + DateUtil.getDateNow() +".mp4";
    }

    public static String getVoicePath(Context context) {
        return getVoiceFolder(context) + File.separator + DateUtil.getDateNow() + ".3gdp";
    }

    public static String getThumbnailPath(String videoPath) {
        return videoPath.replace(".mp4",".jpg");
    }

    public static void saveImage(View decorView, String fileName) {
        Bitmap bitmap = screenShot(decorView);

        saveImage(bitmap, fileName, 490, 112);
    }
    public static boolean deleteContentAndThumbnail(String contentPath) {
        boolean result = true;
        if(Strings.isNullOrEmpty(contentPath)){
            return false;
        }

        File contentFile = new File(contentPath);
        if (contentFile.exists()) {
            result = contentFile.delete();
        }
        File thumbnailFile = new File(ContentsUtil.getThumbnailPath(contentPath));
        if(thumbnailFile.exists()) {
            result =  thumbnailFile.delete();
        }

        return result;
    }


    public static void saveImage(Bitmap original, String fileName, int x, int y) {
        Matrix mtx = new Matrix();
        mtx.postRotate(90);
        Bitmap croppedBitmap = createBitmap(original, x, y, 852, 852, mtx, true);
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

        return createBitmap(
                drawingCache, 0, 0, width, height, matrix, false);
    }

    public static void saveVideoThumbnail(Context context, String videoPath ) {
        String thumbNailPath = ContentsUtil.getThumbnailPath(videoPath);
        File fileCacheItem = new File(thumbNailPath);
        OutputStream out = null;

        Bitmap bitmap = createVideoThumbnail(context, 10,videoPath, MediaStore.Images.Thumbnails.MINI_KIND);

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
    private static boolean hasNavigationBar(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !hasMenuKey && !hasBackKey;
    }

    private static Bitmap createVideoThumbnail(Context context, int time, String filePath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(time);
        } catch (IllegalArgumentException ex) {
        }  finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }

        if (bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int w = 0;
        int h = 0;

        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
            // Scale down the bitmap if it's too large.

            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                w = Math.round(scale * width);
                h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);

            }

            float heightConst = 0.29f;

            if(hasNavigationBar(context)) {
                Resources resources = context.getResources();
                int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    heightConst *= (1- (resources.getDimensionPixelSize(resourceId) / (float)height)- 0.04 );
                }
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                double dmRatio = (double)dm.heightPixels/dm.widthPixels;
                if(dmRatio > 1.72f) {
                    heightConst = 0.30f;
                }

            }

            h = w;
            if (ApplicationManager.getDeviceName().contains("SM-G850")) {
                heightConst = 0.4f;
                h/=1.75f;
            }

            bitmap = createBitmap(bitmap, (int)(w*0.11), (int)(h*heightConst), (int)(w*0.789), (int)(h*0.789));

        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
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

    public static CardModel getTempCardModel(String folderPath, CardTransferModel cardTransferModel) {
        CardModel cardModel = new CardModel();
        cardModel.name = cardTransferModel.name;
        cardModel.cardType = CardModel.CardType.valueOf(cardTransferModel.cardType);

        File[] files = new File(folderPath).listFiles();
        for (File file : files) {
            if (isVideoFile(file)) {
                cardModel.contentPath = file.getAbsolutePath();
            } else if (isImageFile(file)) {
                if (CardModel.CardType.valueOf(cardTransferModel.cardType) == CardModel.CardType.VIDEO_CARD) {
                    cardModel.thumbnailPath = file.getAbsolutePath();
                } else {
                    cardModel.contentPath = file.getAbsolutePath();
                }
            } else if (isVoiceFile(file)) {
                cardModel.voicePath = file.getAbsolutePath();
            }
        }
        return  cardModel;
    }

    public static void copySharedFiles(Context context, CardModel cardModel) {
        File[] files = new File(getTempFolder(context)).listFiles();
        for (File file : files) {
            try {
                if (isVideoFile(file)) {
                    copyFile(file, new File(cardModel.contentPath));
                } else if (isImageFile(file)) {
                    if (cardModel.cardType == CardModel.CardType.VIDEO_CARD) {
                        copyFile(file, new File(cardModel.thumbnailPath));
                    } else {
                        copyFile(file, new File(cardModel.contentPath));
                    }
                } else if (isVoiceFile(file)) {
                    copyFile(file, new File(cardModel.voicePath));
                }
            } catch (IOException e) {
                Log.e("error", "copyShardFile error : " + e.getStackTrace().toString());
            }
        }
    }

    private static boolean isVideoFile(File file) {
        return file.getAbsolutePath().contains("mp4");
    }

    private static boolean isImageFile(File file) {
        return file.getAbsolutePath().contains("jpg") || file.getAbsolutePath().contains("png") || file.getAbsolutePath().contains("jpeg");
    }

    private static boolean isVoiceFile(File file) {
        return file.getAbsolutePath().contains("3gdp");
    }

}