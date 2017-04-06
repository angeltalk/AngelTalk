package act.sds.samsung.angelman.data.repository;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.List;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CardDataRepositoryTest {

    private CardDataRepository subject;

    @Before
    public void setUp() throws Exception {
        subject = new CardDataRepository(RuntimeEnvironment.application.getApplicationContext());
    }

    @Test
    public void whenExtractCardListWithShow_thenVerifyListSize() throws Exception {

        List<CardModel> cardModelList = getCardListWithCategoryId();

        assertThat(cardModelList.size()).isEqualTo(4);
        List<CardModel> extractedList = subject.extractCardListByHide(false, cardModelList);
        assertThat(extractedList.size()).isEqualTo(3);
    }

    @Test
    public void whenExtractCardListWithHide_thenVerifyListSize() throws Exception {

        List<CardModel> cardModelList = getCardListWithCategoryId();

        assertThat(cardModelList.size()).isEqualTo(4);
        List<CardModel> extractedList = subject.extractCardListByHide(true, cardModelList);
        assertThat(extractedList.size()).isEqualTo(1);
    }

    private List<CardModel> getCardListWithCategoryId() {

        String contentFolder = ContentsUtil.getContentFolder() + File.separator;

        List<CardModel> ret = Lists.newArrayList();
        addSingleCardModel(ret, "물", contentFolder+"water.png", "20010928_120020", 0, 0, CardModel.CardType.PHOTO_CARD, null, false);
        addSingleCardModel(ret, "우유", contentFolder+"milk.png", "20010928_120019", 0, 1, CardModel.CardType.PHOTO_CARD, null, false);
        addSingleCardModel(ret, "쥬스", contentFolder+"juice.png", "20010928_120015", 0, 2, CardModel.CardType.PHOTO_CARD, null, false);
        addSingleCardModel(ret, "젤리", contentFolder+"haribo.mp4", "20010928_120015", 0, 3, CardModel.CardType.VIDEO_CARD, contentFolder+"haribo.jpg", true);

        return ret;
    }

    public void addSingleCardModel(List<CardModel> list, String name, String path, String time, int categoryId, int cardIndex, CardModel.CardType cardType, String thumbnailPath ,boolean hide) {
        CardModel model = new CardModel(name, path, time, categoryId, cardIndex, cardType, thumbnailPath, hide);
        list.add(model);
    }
}