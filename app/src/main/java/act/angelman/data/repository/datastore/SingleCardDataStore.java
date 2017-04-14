package act.angelman.data.repository.datastore;

import java.util.List;

import act.angelman.domain.model.CardModel;

public interface SingleCardDataStore {

    long createSingleCardModel(CardModel cardModel);
    boolean removeSingleCardModel(int categoryId, int cardIndex);
    boolean removeSingleCardsInCategory(int categoryId);
    boolean updateSingleCardModelHide(int categoryId, int cardIndex, boolean showing);
    List<CardModel> getCardListWithCategoryId(int selectedCategoryId);
    boolean updateCategoryCardIndex(List<CardModel> cardModelList);

}
