package act.sds.samsung.angelman.presentation.adapter;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.UITest;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CardImageAdapterTest extends UITest {

    @Inject
    ApplicationManager applicationManager;
    private CardImageAdapter subject;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);

        List<CardModel> testDataList = getTestDataList();
        subject = new CardImageAdapter(RuntimeEnvironment.application, testDataList, null, applicationManager);
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