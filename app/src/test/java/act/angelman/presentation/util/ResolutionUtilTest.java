package act.angelman.presentation.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import act.angelman.BuildConfig;
import act.angelman.TestAngelmanApplication;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk=22)
public class ResolutionUtilTest {

    @Before
    public void setup() throws Exception{
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
    }

    @Test
    public void getDensityFunctionTest(){
        assertThat(ResolutionUtil.getDensity(RuntimeEnvironment.application.getApplicationContext())).isNotNull();
    }

    @Test
    public void getDpToPixFunctionTest(){
        Double dp = 200d;
        assertThat(ResolutionUtil.getDpToPix(RuntimeEnvironment.application.getApplicationContext(),dp)).isNotNull();
    }

}