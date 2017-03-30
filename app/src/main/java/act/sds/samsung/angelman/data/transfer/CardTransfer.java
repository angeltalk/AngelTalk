package act.sds.samsung.angelman.data.transfer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CardTransferModel;
import act.sds.samsung.angelman.presentation.listener.OnDownloadCompleteListener;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;
import act.sds.samsung.angelman.presentation.util.DateUtil;

public class CardTransfer {

    private static final String DEFAULT_SHARE_REFERENCE = "share";
    private DatabaseReference shareDataReference;
    private StorageReference storageReference;
    private FirebaseStorage storage;


    private Context context;

    public CardTransfer() {
        shareDataReference = FirebaseDatabase.getInstance().getReference(DEFAULT_SHARE_REFERENCE);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference(DEFAULT_SHARE_REFERENCE);
    }

    private DatabaseReference makeUploadCardReference(String key) {
        return shareDataReference.child(key).push();
    }

    public String uploadCard(CardModel cardModel) {

        String key = DateUtil.getDateNow();
        DatabaseReference uploadReference = makeUploadCardReference(key);
        uploadReference.setValue("cardType", cardModel.cardType.getValue());
        uploadReference.setValue("name", cardModel.name);
        uploadReference.setValue("thumbnailPath", cardModel.thumbnailPath);
        uploadReference.setValue("contentPath", cardModel.contentPath);
        uploadReference.setValue("voicePath", cardModel.voicePath);
        uploadReference.setValue("uploadTime", key);
        uploadReference.setValue("downloadTime", null);
        uploadReference.setValue("download", false);

        return key;
    }

    public void downloadCard(String receiveKey, final OnDownloadCompleteListener downloadCompleteListener) {
         shareDataReference.child(receiveKey).addListenerForSingleValueEvent(
             new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     final CardTransferModel cardTransferModel = new CardTransferModel();
                     for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                         setCardModealData(cardTransferModel, snapshot);
                     }

                     final File localFile = new File(ContentsUtil.getTempFolder() + File.separator + "temp.zip");
                     storage.getReferenceFromUrl(cardTransferModel.contentPath)
                         .getFile(localFile)
                         .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                             @Override
                             public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                 downloadCompleteListener.onSuccess(cardTransferModel, localFile.getAbsolutePath());
                             }
                         })
                         .addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Log.d("DOWNLOAD FAIL ", "download fail." );
                                 downloadCompleteListener.onFail();
                             }
                         });
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {
                     Log.d("onCancelled", databaseError.getMessage());
                     downloadCompleteListener.onFail();
                 }
             }
         );
    }

    private void setCardModealData(CardTransferModel newCardModel, DataSnapshot snapshot) {
        if (snapshot.getKey().equals("name")) {
            newCardModel.name = snapshot.getValue().toString();
        } else if (snapshot.getKey().equals("contentPath")) {
            newCardModel.contentPath = snapshot.getValue().toString();
        } else if (snapshot.getKey().equals("cardType")) {
            newCardModel.cardType = snapshot.getValue().toString();
        }
    }
}

