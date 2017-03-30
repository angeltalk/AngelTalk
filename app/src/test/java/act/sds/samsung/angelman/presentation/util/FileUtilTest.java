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
        assertThat(ContentsUtil.getContentFolder()).isEqualTo(file.getAbsolutePath());
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
        File imageFolder = new File(ContentsUtil.getContentFolder());
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

        File imageFolder = new File(ContentsUtil.getContentFolder());
        assertThat(imageFolder.listFiles().length).isEqualTo(context.getAssets().list("contents").length);
    }

    @Test
    public void removeFileTest() throws Exception {
        File file = new File(ContentsUtil.getContentFolder());
        file.mkdir();
        assertThat(file.exists()).isTrue();
        FileUtil.removeFile(file.getAbsolutePath());
        assertThat(file.exists()).isFalse();
    }


    @Test
    public void removeFilesInTest() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        Context context = RuntimeEnvironment.application;

        FileUtil.copyDefaultAssetImagesToImageFolder(context);

        File imageFolder = new File(ContentsUtil.getContentFolder());
        assertThat(imageFolder.listFiles()).isNotEmpty();
        FileUtil.removeFilesIn(imageFolder.getAbsolutePath());
        assertThat(imageFolder.listFiles()).isEmpty();
    }

    @Test
    public void copyFileTest() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);

        File fileIn = new File(ContentsUtil.getTempFolder() + File.separator + "file.in");
        fileIn.createNewFile();
        File fileOut = new File(ContentsUtil.getTempFolder() + File.separator + "file.out");

        assertThat(fileOut).doesNotExist();
        FileUtil.copyFile(fileIn, fileOut);
        assertThat(fileOut).exists();
    }

    @Test
    public void givenFilesAndDirectory_whenZip_thenCreateZipFile() throws Exception {
        // given
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);

        File imageFolder = new File(ContentsUtil.getContentFolder());
        String[] files = new String[imageFolder.listFiles().length];

        for (int i=0; i<files.length; i++) {
            files[i] = imageFolder.listFiles()[i].getAbsolutePath();
        }

        String zipFile = imageFolder + File.separator + "temp.zip";

        // when
        FileUtil.zip(files, zipFile);

        // then
        assertThat(new File(zipFile)).exists();
    }

    @Test
    public void givenZipFileAndLocation_whenUnzip_thenUnzipFile() throws Exception {
        // given
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);

        File imageFolder = new File(ContentsUtil.getContentFolder());
        String[] files = new String[imageFolder.listFiles().length];

        for (int i=0; i<files.length; i++) {
            files[i] = imageFolder.listFiles()[i].getAbsolutePath();
        }

        String zipFile = imageFolder + File.separator + "temp.zip";

        FileUtil.zip(files, zipFile);

        // when
        String tempFolder = ContentsUtil.getTempFolder();
        FileUtil.unzip(zipFile, tempFolder);

        // then
        assertThat(new File(tempFolder).listFiles().length).isEqualTo(files.length);
    }
}