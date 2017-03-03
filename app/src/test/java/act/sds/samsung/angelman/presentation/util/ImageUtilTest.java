package act.sds.samsung.angelman.presentation.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;

import act.sds.samsung.angelman.BuildConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ImageUtilTest {

    @Test
    public void givenFileNameAndBitMapExists_whenCallImageSave_thenSaveImage() throws Exception {
        ImageUtil subject = ImageUtil.getInstance();

        String fileName = subject.getImagePath(RuntimeEnvironment.application);
        Bitmap fakeBitmap = Bitmap.createBitmap(1440, 2560, Bitmap.Config.ARGB_8888);//mock(Bitmap.class);
        Context context = RuntimeEnvironment.application.getApplicationContext();
        Window window = mock(Window.class);
        WindowManager windowManager = mock(WindowManager.class);
        Display display = mock(Display.class);
        View view = mock(View.class);
        when(window.getWindowManager()).thenReturn(windowManager);
        when(windowManager.getDefaultDisplay()).thenReturn((display));
        when(window.getDecorView()).thenReturn(view);

        subject.saveImage(fakeBitmap, fileName, 444, 112);

        assertThat(fakeBitmap.getHeight()).isEqualTo(2560);
        assertThat(fakeBitmap.getWidth()).isEqualTo(1440);

        File file = new File(fileName);
        assertThat(file.exists()).isTrue();

    }


}