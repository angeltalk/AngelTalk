package act.sds.samsung.angelman.data.transfer;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;
import act.sds.samsung.angelman.presentation.util.DateUtil;

public class CardTransfer {


    public static final String DEFAULT_SHARE_REFERENCE = "share";
    private DatabaseReference shareDataReference;
    private StorageReference  storageReference;



    private Context context;

    public CardTransfer() {
        shareDataReference =  FirebaseDatabase.getInstance().getReference(DEFAULT_SHARE_REFERENCE);
        storageReference = FirebaseStorage.getInstance().getReference(DEFAULT_SHARE_REFERENCE);
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

    public void downloadCard(String receiveKey) {

        shareDataReference.child(receiveKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d("KEY : ",snapshot.getKey());
                            Log.d("KEY : ",snapshot.getValue().toString());

                            ContentsUtil contentsUtil = ContentsUtil.getInstance();
                            if(snapshot.getKey().equals("contentPath")){
                                File localFile = new File(contentsUtil.getImagePath());
                                FirebaseStorage.getInstance().getReferenceFromUrl(snapshot.getValue().toString()).getFile(localFile).addOnSuccessListener(new );



                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
        });



    }



}

