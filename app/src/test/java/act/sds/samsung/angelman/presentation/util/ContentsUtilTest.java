package act.sds.samsung.angelman.presentation.util;

import android.graphics.Bitmap;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;

import act.sds.samsung.angelman.BuildConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ContentsUtilTest {

    @Test
    public void givenFileNameAndBitMapExists_whenCallImageSave_thenSaveImage() throws Exception {
        String fileName = ContentsUtil.getImagePath();
        Bitmap fakeBitmap = Bitmap.createBitmap(1440, 2560, Bitmap.Config.ARGB_8888);//mock(Bitmap.class);
        Window window = mock(Window.class);
        WindowManager windowManager = mock(WindowManager.class);
        Display display = mock(Display.class);
        View view = mock(View.class);
        when(window.getWindowManager()).thenReturn(windowManager);
        when(windowManager.getDefaultDisplay()).thenReturn((display));
        when(window.getDecorView()).thenReturn(view);

        ContentsUtil.saveImage(fakeBitmap, fileName, 444, 112);

        assertThat(fakeBitmap.getHeight()).isEqualTo(2560);
        assertThat(fakeBitmap.getWidth()).isEqualTo(1440);

        File file = new File(fileName);
        assertThat(file.exists()).isTrue();
    }

    @Test
    public void givenFileFullPath_whenGetFileNameFromFullPath_thenReturnFileName() throws Exception {
        // given
        String fileFullPath = "/storage/emulated/0/angelman/DCIM/20170329_133245.jpg";
        // when then
        assertThat(ContentsUtil.getContentNameFromContentPath(fileFullPath)).isEqualTo("20170329_133245.jpg");
    }
}