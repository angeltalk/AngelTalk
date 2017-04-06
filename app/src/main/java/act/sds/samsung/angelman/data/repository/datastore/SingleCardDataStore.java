package act.sds.samsung.angelman.data.repository.datastore;

import java.util.ArrayList;

import act.sds.samsung.angelman.domain.model.CardModel;

public interface SingleCardDataStore {
    ArrayList<CardModel> getAllCardList();
    long createSingleCardModel(CardModel cardModel);
    ArrayList<CardModel> getCardListWithCategoryId(int selectedCategoryId);
    boolean removeSingleCardModel(int categoryId, int cardIndex);
    boolean removeSingleCardsInCategory(int categoryId);
    boolean updateSingleCardModelHide(int categoryId, int cardIndex, boolean showing);
}
