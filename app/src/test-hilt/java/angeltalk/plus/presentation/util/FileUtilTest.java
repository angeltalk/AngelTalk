package angeltalk.plus.presentation.util;

import android.content.Context;
import android.os.Environment;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowEnvironment;

import java.io.File;

import angeltalk.plus.presentation.manager.ApplicationInitializer;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class FileUtilTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        new ApplicationInitializer(context).initializeApplication();
    }

    @Test
    public void getImageFolderTest() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File file = new File(context.getFilesDir() + File.separator + ContentsUtil.CONTENT_FULL_PATH);
        assertThat(ContentsUtil.getContentFolder(context.getApplicationContext())).isEqualTo(file.getAbsolutePath());
    }

    @Test
    public void getVoiceFolderTest() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File file = new File(context.getFilesDir() + File.separator + ContentsUtil.VOICE_FULL_PATH);
        assertThat(ContentsUtil.getVoiceFolder(context.getApplicationContext())).isEqualTo(file.getAbsolutePath());
    }

    @Test
    public void removeFileTest() throws Exception {
        File file = new File(ContentsUtil.getTempFolder(context.getApplicationContext()) + File.separator + "file.in");
        file.createNewFile();

        assertThat(file.exists()).isTrue();
        FileUtil.removeFile(file.getAbsolutePath());
        assertThat(file.exists()).isFalse();
    }

    @Test
    public void removeFilesInTest() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File fileFolder =  new File(ContentsUtil.getTempFolder(context.getApplicationContext()));
        File file = new File(ContentsUtil.getTempFolder(context.getApplicationContext()) + File.separator + "file.in");
        file.createNewFile();
        assertThat(fileFolder.listFiles()).isNotEmpty();
        FileUtil.removeFilesIn(fileFolder.getAbsolutePath());
        assertThat(fileFolder.listFiles()).isEmpty();
    }

    @Test
    public void copyFileTest() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);

        File fileIn = new File(ContentsUtil.getTempFolder(context.getApplicationContext()) + File.separator + "file.in");
        fileIn.createNewFile();
        File fileOut = new File(ContentsUtil.getTempFolder(context.getApplicationContext()) + File.separator + "file.out");

        assertThat(fileOut).doesNotExist();
        FileUtil.copyFile(fileIn, fileOut);
        assertThat(fileOut).exists();
    }

    @Test
    public void givenFilesAndDirectory_whenZip_thenCreateZipFile() throws Exception {
        // given
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);

        File imageFolder = new File(ContentsUtil.getContentFolder(context.getApplicationContext()));
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

        File imageFolder = new File(ContentsUtil.getContentFolder(context.getApplicationContext()));
        String[] files = new String[imageFolder.listFiles().length];

        for (int i=0; i<files.length; i++) {
            files[i] = imageFolder.listFiles()[i].getAbsolutePath();
        }

        String zipFile = imageFolder + File.separator + "temp.zip";

        FileUtil.zip(files, zipFile);

        // when
        String tempFolder = ContentsUtil.getTempFolder(context.getApplicationContext());
        FileUtil.unzip(zipFile, tempFolder);

        // then
        assertThat(new File(tempFolder).listFiles().length).isEqualTo(files.length);
    }
}