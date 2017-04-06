package act.sds.samsung.angelman.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;

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
    public List<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId, boolean isHide) {
        SingleCardDataStore dataStore = new SingleCardSqliteDataStore(context);
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
        return dataStore.updateSingleCardModelHide(cardModel.categoryId, cardModel.cardIndex, cardModel.hide);
    }

    private ArrayList<CardModel> getDataModels(ArrayList<CardModel> cardModels){
        if(cardModels == null){
            return new ArrayList<>();
        }else{
            return cardModels;
        }
    }
}
