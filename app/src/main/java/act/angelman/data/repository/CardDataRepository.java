package act.angelman.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;

import java.util.List;

import act.angelman.data.repository.datastore.SingleCardDataStore;
import act.angelman.data.repository.datastore.SingleCardSqliteDataStore;
import act.angelman.data.sqlite.CardColumns;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.repository.CardRepository;

public class CardDataRepository implements CardRepository {

    private Context context;
    SingleCardDataStore dataStore;

    public CardDataRepository(Context context) {
        this.context = context;
        this.dataStore = new SingleCardSqliteDataStore(context);
    }

    @Override
    public long createSingleCardModel(CardModel cardModel) {
        return dataStore.createSingleCardModel(cardModel);
    }

    @Override
    public List<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId) {
        return getDataModels(dataStore.getCardListWithCategoryId(selectedCategoryId));
    }

    @Override
    public List<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId, boolean isHide) {
        List<CardModel> lists = getDataModels(dataStore.getCardListWithCategoryId(selectedCategoryId));
        return extractCardListByHide(isHide, lists);
    }

    @NonNull
    public List<CardModel> extractCardListByHide(boolean isHide, List<CardModel> lists) {
        List<CardModel> results = Lists.newArrayList();
        for(CardModel cardModel : lists){
            if(cardModel.hide == isHide){
                results.add(cardModel);
            }
        }
        return results;
    }

    @Override
    public boolean deleteSingleCardsWithCategory(int category) {
        return dataStore.removeSingleCardsInCategory(category);
    }

    @Override
    public boolean deleteSingleCardWithCardIndex(int categoryId, int cardIndex) {
        return dataStore.removeSingleCardModel(categoryId, cardIndex);
    }

    @Override
    public boolean updateSingleCardModelHide(CardModel cardModel) {
        return dataStore.updateSingleCardModelHide(cardModel.categoryId, cardModel.cardIndex, cardModel.hide);
    }

    @Override
    public boolean updateCategoryCardIndex(List<CardModel> cardModelList) {
        return dataStore.updateCategoryCardIndex(cardModelList);
    }

    @Override
    public CardModel getSingleCard(String cardId) {
        return dataStore.getSingleCard(cardId);
    }

    @Override
    public boolean updateSingleCardName(String cardId, String cardName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardColumns.NAME, cardName);
        return dataStore.updateSingleCardModel(cardId, contentValues);
    }

    @Override
    public boolean updateSingleCardContent(String cardId, String cardType, String contentPath, String thumbnailPath) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardColumns.CARD_TYPE, cardType);
        contentValues.put(CardColumns.CONTENT_PATH, contentPath);
        contentValues.put(CardColumns.THUMBNAIL_PATH, thumbnailPath);
        return dataStore.updateSingleCardModel(cardId, contentValues);
    }

    @Override
    public boolean updateSingleCardVoice(String cardId, String voicePath) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardColumns.VOICE_PATH, voicePath);
        return dataStore.updateSingleCardModel(cardId, contentValues);
    }

    private List<CardModel> getDataModels(List <CardModel> cardModels){
        if(cardModels == null){
            return Lists.newArrayList();
        }else{
            return cardModels;
        }
    }
}
