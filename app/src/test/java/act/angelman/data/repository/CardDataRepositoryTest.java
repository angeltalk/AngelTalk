package act.angelman.data.repository;

import android.content.ContentValues;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.List;

import act.angelman.BuildConfig;
import act.angelman.data.repository.datastore.SingleCardDataStore;
import act.angelman.data.sqlite.CardColumns;
import act.angelman.domain.model.CardModel;
import act.angelman.presentation.util.ContentsUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk=22)
public class CardDataRepositoryTest {

    private CardDataRepository subject;

    @Before
    public void setUp() throws Exception {
        subject = new CardDataRepository(RuntimeEnvironment.application.getApplicationContext());
        subject.dataStore = mock(SingleCardDataStore.class);
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

    @Test
    public void getSingleCardListWithCategoryIdTest() throws Exception {
        subject.getSingleCardListWithCategoryId(1);
        subject.getSingleCardListWithCategoryId(1,true);
        verify(subject.dataStore, times(2)).getCardListWithCategoryId(1);
    }

    @Test
    public void createSingleCardModelTest() throws Exception {
        CardModel model = getSingleCardModel("물", "water.png", "20010928_120020", 0, 0, CardModel.CardType.PHOTO_CARD, null, false);
        subject.createSingleCardModel(model);
        verify(subject.dataStore).createSingleCardModel(model);
    }

    @Test
    public void deleteSingleCardsWithCategoryTest() throws Exception {
        subject.deleteSingleCardsWithCategory(1);
        verify(subject.dataStore).removeSingleCardsInCategory(1);
    }

    @Test
    public void deleteSingleCardWithCardIndexTest() throws Exception {
        subject.deleteSingleCardWithCardIndex(1, 1);
        verify(subject.dataStore).removeSingleCardModel(1, 1);
    }

    @Test
    public void updateSingleCardModelHideTest() throws Exception {
        CardModel model = getSingleCardModel("물", "water.png", "20010928_120020", 0, 0, CardModel.CardType.PHOTO_CARD, null, false);
        subject.updateSingleCardModelHide(model);
        verify(subject.dataStore).updateSingleCardModelHide(0, 0, false);
    }

    @Test
    public void updateCategoryCardIndexTest() throws Exception {
        List list = getCardListWithCategoryId();
        subject.updateCategoryCardIndex(list);
        verify(subject.dataStore).updateCategoryCardIndex(list);
    }

    @Test
    public void getSingleCardTest() throws Exception {
        subject.getSingleCard("0");
        verify(subject.dataStore).getSingleCard("0");
    }

    @Test
    public void updateSingleCardNameTest() throws Exception {
        subject.updateSingleCardName("1","change");
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardColumns.NAME, "change");
        verify(subject.dataStore).updateSingleCardModel("1", contentValues);
    }

    @Test
    public void updateSingleCardContentTest() throws Exception{
        subject.updateSingleCardContent("1", CardModel.CardType.PHOTO_CARD.getValue(),"/a","/b");
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardColumns.CARD_TYPE, CardModel.CardType.PHOTO_CARD.getValue());
        contentValues.put(CardColumns.CONTENT_PATH, "/a");
        contentValues.put(CardColumns.THUMBNAIL_PATH, "/b");
        verify(subject.dataStore).updateSingleCardModel("1", contentValues);
    }

    @Test
    public void updateSingleCardVoiceTest() throws Exception{
        subject.updateSingleCardVoice("1","/b");
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardColumns.VOICE_PATH, "/b");
        verify(subject.dataStore).updateSingleCardModel("1", contentValues);
    }


    private List<CardModel> getCardListWithCategoryId() {

        String contentFolder = ContentsUtil.getContentFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator;

        List<CardModel> ret = Lists.newArrayList();
        addSingleCardModel(ret, "물", contentFolder+"water.png", "20010928_120020", 0, 0, CardModel.CardType.PHOTO_CARD, null, false);
        addSingleCardModel(ret, "우유", contentFolder+"milk.png", "20010928_120019", 0, 1, CardModel.CardType.PHOTO_CARD, null, false);
        addSingleCardModel(ret, "쥬스", contentFolder+"juice.png", "20010928_120015", 0, 2, CardModel.CardType.PHOTO_CARD, null, false);
        addSingleCardModel(ret, "젤리", contentFolder+"haribo.mp4", "20010928_120015", 0, 3, CardModel.CardType.VIDEO_CARD, contentFolder+"haribo.jpg", true);

        return ret;
    }

    public void addSingleCardModel(List<CardModel> list, String name, String path, String time, int categoryId, int cardIndex, CardModel.CardType cardType, String thumbnailPath ,boolean hide) {
        CardModel model = getSingleCardModel(name, path, time, categoryId, cardIndex, cardType, thumbnailPath, hide);
        list.add(model);
    }

    private CardModel getSingleCardModel(String name, String path, String time, int categoryId, int cardIndex, CardModel.CardType cardType, String thumbnailPath, boolean hide) {
        return CardModel.builder()
                .name(name).contentPath(path)
                .firstTime(time)
                .categoryId(categoryId)
                .cardIndex(cardIndex)
                .cardType(cardType)
                .thumbnailPath(thumbnailPath)
                .hide(hide)
                .build();
    }
}