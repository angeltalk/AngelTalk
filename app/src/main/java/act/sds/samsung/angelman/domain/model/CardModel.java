package act.sds.samsung.angelman.domain.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardModel{
    public String _id;
    public String name;
    public String contentPath;
    public String voicePath;
    public String firstTime;
    public String thumbnailPath;
    public int categoryId;
    public int cardIndex;
    public CardType cardType = CardType.PHOTO_CARD;
    public boolean showing;

    public CardModel() {
    }

    public CardModel(String name, String contentPath, String firstTime) {
        this.name = name;
        this.contentPath = contentPath;
        this.firstTime = firstTime;
    }

    public CardModel(String name, String contentPath, String firstTime, int categoryId) {
        this.name = name;
        this.contentPath = contentPath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
    }

    public CardModel(String name, String contentPath, String firstTime, int categoryId, int cardIndex) {
        this.name = name;
        this.contentPath = contentPath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
        this.cardIndex = cardIndex;
    }

    public CardModel(String _id , String name, String contentPath, String firstTime, int categoryId, int cardIndex) {
        this._id = _id;
        this.name = name;
        this.contentPath = contentPath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
        this.cardIndex = cardIndex;
    }

    public CardModel(String name, String contentPath, String voicePath, String firstTime, int categoryId) {
        this.name = name;
        this.contentPath = contentPath;
        this.voicePath = voicePath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
    }

    public CardModel(String name, String contentPath, String firstTime, int categoryId, int cardIndex, CardType cardType) {
        this.name = name;
        this.contentPath = contentPath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
        this.cardIndex = cardIndex;
        this.cardType = cardType;
    }

    public CardModel(String name, String contentPath, String firstTime, int categoryId, int cardIndex, CardType cardType, String thumbnailPath) {
        this.name = name;
        this.contentPath = contentPath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
        this.cardIndex = cardIndex;
        this.cardType = cardType;
        this.thumbnailPath = thumbnailPath;
    }

    public CardModel(String name, String contentPath, String firstTime, int categoryId, int cardIndex, CardType cardType, String thumbnailPath, boolean showing) {
        this.name = name;
        this.contentPath = contentPath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
        this.cardIndex = cardIndex;
        this.cardType = cardType;
        this.thumbnailPath = thumbnailPath;
        this.showing = showing;
    }

    public CardModel(String name, String contentPath, String voicePath, String firstTime, int categoryId, CardType cardType) {
        this.name = name;
        this.contentPath = contentPath;
        this.voicePath = voicePath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
        this.cardType = cardType;
    }
    public CardModel(String name, String contentPath, String voicePath, String firstTime, int categoryId, CardType cardType, String thumbnailPath) {
        this.name = name;
        this.contentPath = contentPath;
        this.voicePath = voicePath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
        this.cardType = cardType;
        this.thumbnailPath = thumbnailPath;
    }

    public enum CardType {
        PHOTO_CARD("PHOTO_CARD"),
        VIDEO_CARD("VIDEO_CARD");

        private String value;
        CardType(String cardTypeValue) {
            this.value = cardTypeValue;
        }
        public String getValue() {
            return value;
        }
    }

}
