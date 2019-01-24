package angeltalk.plus.presentation.util;

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

import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.manager.ApplicationInitializer;
import androidx.test.core.app.ApplicationProvider;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22)
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

        File rootFolder = new File(ApplicationProvider.getApplicationContext().getFilesDir() + File.separator + ContentsUtil.ANGELMAN_FOLDER);
        File imageFolder = new File(ContentsUtil.getContentFolder(RuntimeEnvironment.application.getApplicationContext()));
        File voiceFolder = new File(ContentsUtil.getVoiceFolder(RuntimeEnvironment.application.getApplicationContext()));
        File tempFolder = new File(ContentsUtil.getTempFolder(RuntimeEnvironment.application.getApplicationContext()));

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

        File imageFolder = new File(ContentsUtil.getContentFolder(ApplicationProvider.getApplicationContext()));
        assertThat(imageFolder.listFiles().length).isEqualTo(context.getAssets().list("contents").length);
    }

    @Test
    public void givenNotFirstLaunched_whenApplicationInitialize_thenSkipCopyDefaultAssetImagesToImageFolder() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        Context context = RuntimeEnvironment.application;

        SharedPreferences.Editor edit = context.getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        edit.putBoolean(ApplicationConstants.FIRST_LAUNCH, false);
        edit.commit();

        File imageFolder = new File(ContentsUtil.getContentFolder(RuntimeEnvironment.application.getApplicationContext()));
        for(File file : imageFolder.listFiles()) {
            if(!file.isDirectory()){
                file.delete();
            }
        }
        subject.initializeApplication();
        assertThat(imageFolder.listFiles().length).isEqualTo(0);
    }
}