package act.angelman.domain.repository;

import java.util.List;

import act.angelman.domain.model.CardModel;

public interface CardRepository {
    long createSingleCardModel(CardModel cardModel);
    boolean deleteSingleCardsWithCategory(int category);
    boolean deleteSingleCardWithCardIndex(int categoryId, int cardIndex);
    boolean updateSingleCardModelHide(CardModel cardModel);
    boolean updateSingleCardName(String cardId, String cardName);
    boolean updateSingleCardContent(String cardId, String cardType, String contentPath, String thumbnailPath);
    boolean updateSingleCardVoice(String cardId, String voicePath);
    boolean updateCategoryCardIndex(List<CardModel> cardModelList);
    CardModel getSingleCard(String cardId);
    List<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId);
    List<CardModel> getSingleCardListWithCategoryId(int selectedCategoryId, boolean isHide);

}
