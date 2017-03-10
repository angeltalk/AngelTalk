package act.sds.samsung.angelman.domain.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardModel{
    public String _id;
    public String name;
    public String imagePath;
    public String voicePath;
    public String firstTime;
    public int categoryId;
    public int cardIndex;

    public CardModel() {
    }

    public CardModel(String name, String imagePath, String firstTime) {
        this.name = name;
        this.imagePath = imagePath;
        this.firstTime = firstTime;
    }

    public CardModel(String name, String imagePath, String firstTime, int categoryId) {
        this.name = name;
        this.imagePath = imagePath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
    }

    public CardModel(String name, String imagePath, String firstTime, int categoryId, int cardIndex) {
        this.name = name;
        this.imagePath = imagePath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
        this.cardIndex = cardIndex;
    }

    public CardModel(String _id , String name, String imagePath, String firstTime, int categoryId, int cardIndex) {
        this._id = _id;
        this.name = name;
        this.imagePath = imagePath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
        this.cardIndex = cardIndex;
    }


    public CardModel( String name, String imagePath, String voicePath, String firstTime, int categoryId) {
        this.name = name;
        this.imagePath = imagePath;
        this.voicePath = voicePath;
        this.firstTime = firstTime;
        this.categoryId = categoryId;
    }


}
