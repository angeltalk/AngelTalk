package angeltalk.plus.presentation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowEnvironment;

import java.io.File;

import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.manager.ApplicationInitializer;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ApplicationInitializerTest {

    private Context context;
    private ApplicationInitializer subject;

    @Before
    public void setUp() throws Exception {
        context = ApplicationProvider.getApplicationContext();
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        subject = new ApplicationInitializer(context);
    }

    @Test
    public void whenApplicationInitialize_thenInitExternalStorageFolderTest() throws Exception {
        subject.initializeApplication();

        File rootFolder = new File(context.getFilesDir() + File.separator + ContentsUtil.ANGELMAN_FOLDER);
        File imageFolder = new File(ContentsUtil.getContentFolder(context));
        File voiceFolder = new File(ContentsUtil.getVoiceFolder(context));
        File tempFolder = new File(ContentsUtil.getTempFolder(context));

        assertThat(rootFolder).exists();
        assertThat(imageFolder).exists();
        assertThat(voiceFolder).exists();
        assertThat(tempFolder).exists();
    }

    @Test
    public void givenFirstLaunched_whenApplicationInitialize_thenCopyDefaultAssetImagesToImageFolder() throws Exception {
        subject.initializeApplication();

        File imageFolder = new File(ContentsUtil.getContentFolder(context));
        assertThat(imageFolder.listFiles().length).isEqualTo(context.getAssets().list("contents").length);
    }

    @Test
    public void givenNotFirstLaunched_whenApplicationInitialize_thenSkipCopyDefaultAssetImagesToImageFolder() throws Exception {
        subject.initializeApplication();

        SharedPreferences.Editor edit = context.getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        edit.putBoolean(ApplicationConstants.FIRST_LAUNCH, false);
        edit.commit();

        File imageFolder = new File(ContentsUtil.getContentFolder(context));
        for (File file : imageFolder.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
        subject.initializeApplication();
        assertThat(imageFolder.listFiles().length).isEqualTo(0);
    }
}
