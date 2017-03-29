package act.sds.samsung.angelman.data.transfer;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.presentation.util.DateUtil;

/**
 * Created by devcraft47 on 2017. 3. 29..
 */

public class CardTransfer {


    public static final String DEFAULT_SHARE_REFERENCE = "share";
    private DatabaseReference shareDataReference;


    private Context context;

    public CardTransfer() {
        shareDataReference =  FirebaseDatabase.getInstance().getReference(DEFAULT_SHARE_REFERENCE);
    }

    private DatabaseReference makeUploadCardReference(String key){
        return shareDataReference.child(key).push();
    }

    public String uploadCard(CardModel cardModel){

        String key = DateUtil.getDateNow();
        DatabaseReference uploadReference = makeUploadCardReference(key);

        uploadReference.setValue("cardType",cardModel.cardType.getValue());
        uploadReference.setValue("name",cardModel.name);
        uploadReference.setValue("thumbnailPath",cardModel.thumbnailPath);
        uploadReference.setValue("contentPath",cardModel.contentPath);
        uploadReference.setValue("voicePath",cardModel.voicePath);
        uploadReference.setValue("uploadTime", key);
        uploadReference.setValue("downloadTime", null);
        uploadReference.setValue("download", false);

        return key;
    }

}
