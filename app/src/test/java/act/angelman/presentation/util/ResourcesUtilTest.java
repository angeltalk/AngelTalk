package act.angelman.presentation.util;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import act.angelman.BuildConfig;
import act.angelman.R;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ResourcesUtilTest {

    private ResourcesUtil subject;

    @Test
    public void setColorThemeTest() throws Exception {
        subject = new ResourcesUtil();
        Context context = mock(Context.class);

        subject.setColorTheme(context, ResourcesUtil.RED);
        verify(context).setTheme(eq(R.style.AppTheme_Red));

        subject.setColorTheme(context, ResourcesUtil.ORANGE);
        verify(context).setTheme(eq(R.style.AppTheme_Orange));

        subject.setColorTheme(context, ResourcesUtil.YELLOW);
        verify(context).setTheme(eq(R.style.AppTheme_Yellow));

        subject.setColorTheme(context, ResourcesUtil.GREEN);
        verify(context).setTheme(eq(R.style.AppTheme_Green));

        subject.setColorTheme(context, ResourcesUtil.BLUE);
        verify(context).setTheme(eq(R.style.AppTheme_Blue));

        subject.setColorTheme(context, ResourcesUtil.PURPLE);
        verify(context).setTheme(eq(R.style.AppTheme_Purple));
    }
}