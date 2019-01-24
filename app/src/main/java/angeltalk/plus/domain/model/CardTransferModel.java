package angeltalk.plus.domain.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CardTransferModel {
    public String cardType;
    public String name;
    public String contentPath;
    public String thumbnailPath;
    public String downloadedFilePath;
}

