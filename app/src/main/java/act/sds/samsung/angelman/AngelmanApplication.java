package act.sds.samsung.angelman;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.annotation.VisibleForTesting;
import android.util.DisplayMetrics;
import android.util.Log;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import act.sds.samsung.angelman.dagger.components.AngelmanComponent;
import act.sds.samsung.angelman.dagger.components.DaggerAngelmanComponent;
import act.sds.samsung.angelman.dagger.modules.AngelmanModule;
import act.sds.samsung.angelman.presentation.util.ImageUtil;


@ReportsCrashes(
        mailTo = "act.angeltalk@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resDialogTitle= R.string.bug_report_title,
        resDialogText= R.string.bug_report


)
public class AngelmanApplication extends Application {

    public static final String PRIVATE_PREFERENCE_NAME = "act.sds.samsung.angelman";
    public static final String SCREEN_SERVICE_NAME = "act.sds.samsung.angelman.presentation.service.ScreenService";

    private AngelmanComponent angelmanComponent;
    private SharedPreferences preferences;


    @Override
    public void onCreate() {
        super.onCreate();

        angelmanComponent = DaggerAngelmanComponent.builder()
                .angelmanModule(new AngelmanModule(this))
                .build();

        initExternalStorageFolder();
        preferences = getSharedPreferences(PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this);
    }

    private static final String ANGELMAN_FOLDER = "angelman";
    private static final String VOICE_FOLDER = "voice";

    private static final String IMAGE_FULL_PATH = ANGELMAN_FOLDER + File.separator + ImageUtil.IMAGE_FOLDER;
    private static final String VOICE_FULL_PATH = ANGELMAN_FOLDER + File.separator + VOICE_FOLDER;

    public String getImageFolder() {
        return Environment.getExternalStorageDirectory() + File.separator + IMAGE_FULL_PATH;
    }

    public String getVoiceFolder() {
        return Environment.getExternalStorageDirectory() + File.separator + VOICE_FULL_PATH;
    }

    private void initExternalStorageFolder() {
        File rootFolder = new File(Environment.getExternalStorageDirectory() + File.separator + ANGELMAN_FOLDER);

        if (!rootFolder.exists())
            rootFolder.mkdir();

        File imageFolder = new File(getImageFolder());

        if (!imageFolder.exists()) {
            imageFolder.mkdir();
        }

        File voiceFolder = new File(getVoiceFolder());

        if (!voiceFolder.exists())
            voiceFolder.mkdir();
    }

    public void copyAssetImagesToImageFolder() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("images");
        } catch (IOException e) {
            Log.e("AngelmanApplication", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("images" + File.separator + filename);
                File outFile = new File(getImageFolder(), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("AngelmanApplication", "Failed to copy asset file: " + filename, e);
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

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public int getScreenHeightPixel() {
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public AngelmanComponent getAngelmanComponent() {
        return this.angelmanComponent;
    }

    @VisibleForTesting
    public void setComponent(AngelmanComponent angelmanComponent) {
        this.angelmanComponent = angelmanComponent;
    }



}
