package act.sds.samsung.angelman.presentation.util;

import android.content.Context;
import android.os.Environment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowEnvironment;

import java.io.File;

import act.sds.samsung.angelman.BuildConfig;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class FileUtilTest {

    @Test
    public void getImageFolderTest() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + FileUtil.IMAGE_FULL_PATH);
        assertThat(ContentsUtil.getImageFolder()).isEqualTo(file.getAbsolutePath());
    }

    @Test
    public void getVoiceFolderTest() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + FileUtil.VOICE_FULL_PATH);
        assertThat(ContentsUtil.getVoiceFolder()).isEqualTo(file.getAbsolutePath());
    }

    @Test
    public void initExternalStorageFolderTest() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);

        FileUtil.initExternalStorageFolder();

        File rootFolder = new File(Environment.getExternalStorageDirectory() + File.separator + FileUtil.ANGELMAN_FOLDER);
        File imageFolder = new File(ContentsUtil.getImageFolder());
        File voiceFolder = new File(ContentsUtil.getVoiceFolder());
        File tempFolder = new File(ContentsUtil.getTempFolder());

        assertThat(rootFolder).exists();
        assertThat(imageFolder).exists();
        assertThat(voiceFolder).exists();
        assertThat(tempFolder).exists();
    }

    @Test
    public void copyDefaultAssetImagesToImageFolderTest() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        Context context = RuntimeEnvironment.application;

        FileUtil.copyDefaultAssetImagesToImageFolder(context);

        File imageFolder = new File(ContentsUtil.getImageFolder());
        assertThat(imageFolder.listFiles().length).isEqualTo(context.getAssets().list("contents").length);
    }

    @Test
    public void removeFileTest() throws Exception {
        File file = new File(ContentsUtil.getImageFolder());
        file.mkdir();
        assertThat(file.exists()).isTrue();
        FileUtil.removeFile(file.getAbsolutePath());
        assertThat(file.exists()).isFalse();
    }
}