package act.angelman.presentation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowEnvironment;

import java.io.File;

import act.angelman.BuildConfig;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.manager.ApplicationInitializer;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ApplicationInitializerTest {

    private ApplicationInitializer subject;

    @Before
    public void setUp() throws Exception {
        subject = new ApplicationInitializer(RuntimeEnvironment.application);
    }

    @Test
    public void whenApplicationInitialize_thenInitExternalStorageFolderTest() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        subject.initializeApplication();

        File rootFolder = new File(Environment.getExternalStorageDirectory() + File.separator + ContentsUtil.ANGELMAN_FOLDER);
        File imageFolder = new File(ContentsUtil.getContentFolder());
        File voiceFolder = new File(ContentsUtil.getVoiceFolder());
        File tempFolder = new File(ContentsUtil.getTempFolder());

        assertThat(rootFolder).exists();
        assertThat(imageFolder).exists();
        assertThat(voiceFolder).exists();
        assertThat(tempFolder).exists();
    }

    @Test
    public void givenFirstLaunched_whenApplicationInitialize_thenCopyDefaultAssetImagesToImageFolder() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        Context context = RuntimeEnvironment.application;

        subject.initializeApplication();

        File imageFolder = new File(ContentsUtil.getContentFolder());
        assertThat(imageFolder.listFiles().length).isEqualTo(context.getAssets().list("contents").length);
    }

    @Test
    public void givenNotFirstLaunched_whenApplicationInitialize_thenSkipCopyDefaultAssetImagesToImageFolder() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        Context context = RuntimeEnvironment.application;

        SharedPreferences.Editor edit = context.getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        edit.putBoolean(ApplicationConstants.FIRST_LAUNCH, false);
        edit.commit();

        File imageFolder = new File(ContentsUtil.getContentFolder());
        for(File file : imageFolder.listFiles()) {
            if(!file.isDirectory()){
                file.delete();
            }
        }
        subject.initializeApplication();
        assertThat(imageFolder.listFiles().length).isEqualTo(0);
    }
}