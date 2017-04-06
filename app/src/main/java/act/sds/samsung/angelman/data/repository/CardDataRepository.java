package act.sds.samsung.angelman.data.repository;

import android.content.Context;

import java.util.ArrayList;

import act.sds.samsung.angelman.data.repository.datastore.SingleCardDataStore;
import act.sds.samsung.angelman.data.repository.datastore.SingleCardSqliteDataStore;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;

public class CardDataRepository implements CardRepository {

    private Context context;
    public CardDataRepository(Context context) {
        this.context = context;
    }
    @Override
    public ArrayList<CardModel> getSingleCardAllList() {
        SingleCardDataStore dataStore = new SingleCardSqliteDataStore(context);
        return getDataModels(dataStore.getAllCardList());
    }

    @Override
    public long createSingleCardModel(CardModel cardModel) {
        SingleCardDataStore dataStore = new SingleCardSqliteDataStore(context);
        return dataStore.createSingleCardModel(cardModel);
    }

    @Override
    public ArrayList<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId) {
        SingleCardDataStore dataStore = new SingleCardSqliteDataStore(context);
        return getDataModels(dataStore.getCardListWithCategoryId(selectedCategoryId));
    }

    @Override
    public boolean deleteSingleCardsWithCategory(int category) {
        SingleCardDataStore dataStore = new SingleCardSqliteDataStore(context);
        return dataStore.removeSingleCardsInCategory(category);
    }

    @Override
    public boolean deleteSingleCardWithCardIndex(int categoryId, int cardIndex) {
        SingleCardDataStore dataStore = new SingleCardSqliteDataStore(context);
        return dataStore.removeSingleCardModel(categoryId, cardIndex);
    }

    @Override
    public boolean updateSingleCardModelHide(CardModel cardModel) {
        SingleCardDataStore dataStore = new SingleCardSqliteDataStore(context);
        return dataStore.updateSingleCardModelHide(cardModel.cardIndex, cardModel.hide);
    }

    private ArrayList<CardModel> getDataModels(ArrayList<CardModel> cardModels){
        if(cardModels == null){
            return new ArrayList<>();
        }else{
            return cardModels;
        }
    }
}
