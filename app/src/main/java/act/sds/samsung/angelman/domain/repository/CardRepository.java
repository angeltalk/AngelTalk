package act.sds.samsung.angelman.domain.repository;

import java.util.List;

import act.sds.samsung.angelman.domain.model.CardModel;

public interface CardRepository {
    List<CardModel> getSingleCardAllList();
    long createSingleCardModel(CardModel cardModel);
    List<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId);
    boolean deleteSingleCardsWithCategory(int category);
    boolean deleteSingleCardWithCardIndex(int categoryId, int cardIndex);
    boolean updateSingleCardModelHide(CardModel cardModel);
}
