package act.sds.samsung.angelman.domain.repository;

import java.util.List;

import act.sds.samsung.angelman.domain.model.CardModel;

public interface CardRepository {
    List<CardModel> getSingleCardAllList();
    List<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId);
    long createSingleCardModel(CardModel cardModel);
    boolean deleteSingleCardsWithCategory(int category);
    boolean deleteSingleCardWithCardIndex(int categoryId, int cardIndex);
}
