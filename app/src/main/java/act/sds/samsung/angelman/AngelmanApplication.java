package act.sds.samsung.angelman;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.annotation.VisibleForTesting;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

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
import act.sds.samsung.angelman.presentation.custom.AngelmanWidgetProvider;
import act.sds.samsung.angelman.presentation.custom.ChildModeManager;
import act.sds.samsung.angelman.presentation.service.ScreenService;
import act.sds.samsung.angelman.presentation.util.ImageUtil;


@ReportsCrashes(
        mailTo = "act.angeltalk@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resDialogTitle= R.string.bug_report_title,
        resDialogText= R.string.bug_report


)
public class AngelmanApplication extends Application {

    public static final String PRIVATE_PREFERENCE_NAME = "act.sds.samsung.angelman";

    private static final String CHILD_MODE = "childMode";
    public static final String SCREEN_SERVICE_NAME = "act.sds.samsung.angelman.presentation.service.ScreenService";
    private static final String FIRST_LAUNCH = "firstLaunch";
    private AngelmanComponent angelmanComponent;
    private SharedPreferences preferences;
    protected ChildModeManager childModeManager;

    @Override
    public void onCreate() {
        super.onCreate();

        angelmanComponent = DaggerAngelmanComponent.builder()
                .angelmanModule(new AngelmanModule(this))
                .build();

        initExternalStorageFolder();
        childModeManager = new ChildModeManager(getApplicationContext());
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




    public static void changeChildMode(Context context, boolean mode) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();

        if (mode) {
            /* Set Child Mode */
            edit.putBoolean(CHILD_MODE, true);
            if (!isServiceRunningCheck(context)) {
                Intent screenService = new Intent(context, ScreenService.class);
                context.startService(screenService);
            }
            edit.commit();
            Toast.makeText(context, R.string.inform_show_child_mode, Toast.LENGTH_LONG).show();

        } else {
            /* Set NOT Child Mode */
            edit.putBoolean(CHILD_MODE, false);
            ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (service.service.getClassName().contains(SCREEN_SERVICE_NAME)) {
                    Intent stop = new Intent();
                    stop.setComponent(service.service);
                    context.stopService(stop);
                }
            }
            edit.commit();
            Toast.makeText(context, R.string.inform_hide_child_mode, Toast.LENGTH_LONG).show();
        }
    }


    public void setChildMode(){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(CHILD_MODE, true);
        if(!isServiceRunningCheck()) {
            Intent screenService = new Intent(getApplicationContext(), ScreenService.class);
            updateWidgetView(R.drawable.widget_on);
            startService(screenService);
        }
        edit.commit();
        Toast.makeText(this, R.string.inform_show_child_mode, Toast.LENGTH_LONG).show();
    }

    public void setNotChildMode(){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(CHILD_MODE, false);
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains(ScreenService.class.getCanonicalName())) {
                Intent stop = new Intent();
                stop.setComponent(service.service);
                stopService(stop);
            }
        }
        updateWidgetView(R.drawable.widget_off);
        edit.commit();
        Toast.makeText(this, R.string.inform_hide_child_mode, Toast.LENGTH_LONG).show();
    }

    public boolean isChildMode(){
        return preferences.getBoolean(CHILD_MODE, true);
    }

    public void makeChildView(){
        childModeManager.removeAllView();
        childModeManager.createAndAddCategoryMenu();
    }

    public void updateWidgetView(@DrawableRes int drawable) {
        RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_angelman);
        views.setImageViewResource(R.id.angelman_button, drawable);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName thisWidget = new ComponentName(getApplicationContext(), AngelmanWidgetProvider.class);
        appWidgetManager.updateAppWidget(thisWidget, views);
    }

    public AngelmanComponent getAngelmanComponent() {
        return this.angelmanComponent;
    }

    @VisibleForTesting
    public void setComponent(AngelmanComponent angelmanComponent) {
        this.angelmanComponent = angelmanComponent;
    }

    public void setNotFirstLaunched() {
        if(preferences.getBoolean(FIRST_LAUNCH, true)) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(FIRST_LAUNCH, false);
            edit.commit();
        }
    }

    public boolean isFirstLaunched(){
        return preferences.getBoolean(FIRST_LAUNCH, true);
    }

    public boolean isServiceRunningCheck() {
        return isServiceRunningCheck(this);
    }

    public static boolean isServiceRunningCheck(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains(SCREEN_SERVICE_NAME)) {
                return true;
            }
        }
        return false;
    }
}
