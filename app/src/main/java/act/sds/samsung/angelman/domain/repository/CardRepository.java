package act.sds.samsung.angelman.domain.repository;

import java.util.ArrayList;

import act.sds.samsung.angelman.domain.model.CardModel;

public interface CardRepository {
    ArrayList<CardModel> getSingleCardAllList();
    long createSingleCardModel(CardModel cardModel);

    ArrayList<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId);
    boolean deleteSingleCardsWithCategory(int category);
    boolean deleteSingleCardWithCardIndex(int categoryId, int cardIndex);
}
