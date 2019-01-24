package angeltalk.plus.presentation.util;

import android.content.Context;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import angeltalk.plus.R;
import angeltalk.plus.presentation.shadow.ShadowResourcesCompat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowResourcesCompat.class}, sdk=22)
public class ResourcesUtilTest {


    @Test
    public void setColorThemeTest() throws Exception {
        Context context = mock(Context.class);

        ResourcesUtil.setColorTheme(context, ResourcesUtil.RED);
        verify(context).setTheme(eq(R.style.AppTheme_Red));

        ResourcesUtil.setColorTheme(context, ResourcesUtil.ORANGE);
        verify(context).setTheme(eq(R.style.AppTheme_Orange));

        ResourcesUtil.setColorTheme(context, ResourcesUtil.YELLOW);
        verify(context).setTheme(eq(R.style.AppTheme_Yellow));

        ResourcesUtil.setColorTheme(context, ResourcesUtil.GREEN);
        verify(context).setTheme(eq(R.style.AppTheme_Green));

        ResourcesUtil.setColorTheme(context, ResourcesUtil.BLUE);
        verify(context).setTheme(eq(R.style.AppTheme_Blue));

        ResourcesUtil.setColorTheme(context, ResourcesUtil.PURPLE);
        verify(context).setTheme(eq(R.style.AppTheme_Purple));
    }

    @Test
    public void getCardViewLayoutBackgroundByTest() throws Exception {
        assertThat(ResourcesUtil.getCardViewLayoutBackgroundBy(ResourcesUtil.RED)).isEqualTo(R.drawable.background_gradient_red);
        assertThat(ResourcesUtil.getCardViewLayoutBackgroundBy(ResourcesUtil.ORANGE)).isEqualTo(R.drawable.background_gradient_orange);
        assertThat(ResourcesUtil.getCardViewLayoutBackgroundBy(ResourcesUtil.YELLOW)).isEqualTo(R.drawable.background_gradient_yellow);
        assertThat(ResourcesUtil.getCardViewLayoutBackgroundBy(ResourcesUtil.GREEN)).isEqualTo(R.drawable.background_gradient_green);
        assertThat(ResourcesUtil.getCardViewLayoutBackgroundBy(ResourcesUtil.BLUE)).isEqualTo(R.drawable.background_gradient_blue);
        assertThat(ResourcesUtil.getCardViewLayoutBackgroundBy(ResourcesUtil.PURPLE)).isEqualTo(R.drawable.background_gradient_purple);
    }

    @Test
    public void setViewBackgroundTest() throws Exception {
        ResourcesUtil.setViewBackground(mock(View.class), ResourcesUtil.PURPLE, RuntimeEnvironment.application.getApplicationContext());
        assertThat(ShadowResourcesCompat.capturedId).isEqualTo(R.drawable.background_gradient_purple);
    }
}