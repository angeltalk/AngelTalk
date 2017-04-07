package act.sds.samsung.angelman.domain.repository;

import java.util.List;

import act.sds.samsung.angelman.domain.model.CardModel;

public interface CardRepository {
    long createSingleCardModel(CardModel cardModel);
    boolean deleteSingleCardsWithCategory(int category);
    boolean deleteSingleCardWithCardIndex(int categoryId, int cardIndex);
    boolean updateSingleCardModelHide(CardModel cardModel);
    List<CardModel> getSingleCardAllList();
    List<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId);
    List<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId, boolean isHide);
}
