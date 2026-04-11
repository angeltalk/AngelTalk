package angeltalk.plus.presentation.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Rule;
import org.mockito.Mockito;

import angeltalk.plus.dagger.modules.AngelmanModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;


@HiltAndroidTest
@UninstallModules(AngelmanModule.class)
@RunWith(RobolectricTestRunner.class)
public class ResolutionUtilTest {


    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @BindValue
    angeltalk.plus.domain.repository.CardRepository __cardRepoMock = Mockito.mock(angeltalk.plus.domain.repository.CardRepository.class);

    @BindValue
    angeltalk.plus.domain.repository.CategoryRepository __categoryRepoMock = Mockito.mock(angeltalk.plus.domain.repository.CategoryRepository.class);
    @Before
    public void setup() throws Exception{
        hiltRule.inject();
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