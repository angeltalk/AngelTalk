package angeltalk.plus.presentation.adapter;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;


import angeltalk.plus.UITest;
import angeltalk.plus.domain.model.CardModel;
import angeltalk.plus.presentation.manager.ApplicationManager;

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
public class CardImageAdapterTest extends UITest {


    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @BindValue
    angeltalk.plus.domain.repository.CardRepository __cardRepoMock = Mockito.mock(angeltalk.plus.domain.repository.CardRepository.class);

    @BindValue
    angeltalk.plus.domain.repository.CategoryRepository __categoryRepoMock = Mockito.mock(angeltalk.plus.domain.repository.CategoryRepository.class);
    @BindValue
    ApplicationManager applicationManager = Mockito.mock(ApplicationManager.class);
    private CardImageAdapter subject;

    @Before
    public void setUp() throws Exception {
        hiltRule.inject();

        List<CardModel> testDataList = getTestDataList();
        subject = new CardImageAdapter(RuntimeEnvironment.application, testDataList, null);
    }

    @Test
    public void getCountTest() throws Exception {
        assertThat(subject.getCount()).isEqualTo(2);
    }

    @Test
    public void addNewCardTestAtFirstTest() throws Exception {
        subject.addNewCardViewAtFirst();
        assertThat(subject.getCount()).isEqualTo(3);
    }

    private List<CardModel> getTestDataList() {
        List<CardModel> list = Lists.newArrayList();
        list.add(CardModel.builder().name("젤리").contentPath("haribo.mp4").firstTime("20161018_000002").cardType(CardModel.CardType.VIDEO_CARD).build());
        list.add(CardModel.builder().name("물").contentPath("water.png").firstTime("20161018_000003").categoryId(0).cardIndex(1).cardType(CardModel.CardType.PHOTO_CARD).build());
        return list;
    }
}