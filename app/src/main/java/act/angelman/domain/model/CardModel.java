package act.angelman.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardModel{
    public String _id;
    public String name;
    public String contentPath;
    public String voicePath;
    public String firstTime;
    public String thumbnailPath;
    public int categoryId;
    public int cardIndex;
    @Builder.Default
    public CardType cardType = CardType.PHOTO_CARD;
    public boolean hide;

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
