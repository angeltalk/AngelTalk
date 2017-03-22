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
import act.sds.samsung.angelman.presentation.util.ApplicationManager;

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
        list.add(new CardModel("젤리", "haribo.mp4", "20161018_000002", 0, 0, CardModel.CardType.VIDEO_CARD));
        list.add(new CardModel("물", "water.png", "20161018_000003", 0, 1, CardModel.CardType.PHOTO_CARD));
        return list;
    }
}