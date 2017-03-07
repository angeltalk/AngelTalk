package act.sds.samsung.angelman.data.repository;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

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
    public List<CardModel> getSingleCardAllList() {
        SingleCardDataStore dataStore = new SingleCardSqliteDataStore(context);
        return getDataModels(dataStore.getAllCardList());
    }

    @Override
    public long createSingleCardModel(CardModel cardModel) {
        SingleCardDataStore dataStore = new SingleCardSqliteDataStore(context);
        return dataStore.createSingleCardModel(cardModel);
    }

    @Override
    public List<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId) {
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

    private List<CardModel> getDataModels(ArrayList<CardModel> cardModels){
        if(cardModels == null){
            return new ArrayList<>();
        }else{
            return cardModels;
        }
    }
}
