package act.sds.samsung.angelman.data.transfer;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;
import act.sds.samsung.angelman.presentation.util.FileShareUtil;
import act.sds.samsung.angelman.presentation.util.FileUtil;

public class CardTransfer {



    @Inject
    FileShareUtil fileShareUtil;

    public static final String DEFAULT_SHARE_REFERENCE = "share";
    public static final String IMAGE_FILE_EXTENSION = ".jpg";

    private DatabaseReference shareDataReference;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private Context context;

    public CardTransfer(Context context) {
        this.context = context;
        ((AngelmanApplication) context).getAngelmanComponent().inject(this);
        storage = FirebaseStorage.getInstance();
        shareDataReference = FirebaseDatabase.getInstance().getReference(DEFAULT_SHARE_REFERENCE);
        storageReference = storage.getReference(DEFAULT_SHARE_REFERENCE);
    }

    public void uploadCard(final CardModel cardModel, final OnSuccessListener<Map<String, String>> onSuccessListener, final OnFailureListener onFailureListener) {
        final String key = fileShareUtil.getShareKey();
        final String zipFilePath = makeShareZipFile(cardModel, key);

        storageReference.child(key).child(key + ".zip").putFile(Uri.fromFile(new File(zipFilePath))) // Zip File upload
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri uploadFileUri ;
                        if(cardModel.cardType == CardModel.CardType.PHOTO_CARD){
                            uploadFileUri = Uri.fromFile(new File( getAbsoluteContentsPath(cardModel.contentPath)));
                        } else{
                            uploadFileUri = Uri.fromFile(new File( getAbsoluteContentsPath(cardModel.thumbnailPath)));
                        }
                        storageReference.child(key).child(key + IMAGE_FILE_EXTENSION)
                                .putFile(uploadFileUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Map<String, String> resultMap = Maps.newHashMap();
                                        resultMap.put("key", key);
                                        resultMap.put("url", taskSnapshot.getDownloadUrl().toString());
                                        onSuccessListener.onSuccess(resultMap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        onFailureListener.onFailure(e);
                                    }
                        });
                        shareDataReference.child(key).child("cardType").setValue(cardModel.cardType.getValue());
                        shareDataReference.child(key).child("name").setValue(cardModel.name);
                        shareDataReference.child(key).child("contentPath").setValue(taskSnapshot.getDownloadUrl().toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailureListener.onFailure(e);
                }
        });

    }

    @NonNull
    private String makeShareZipFile(CardModel cardModel, String key) {
        List<String> filePathList = Lists.newArrayList(getAbsoluteContentsPath(cardModel.contentPath));
        if (cardModel.voicePath != null) {
            filePathList.add(getAbsoluteContentsPath(cardModel.voicePath));
        }
        if (cardModel.cardType == CardModel.CardType.VIDEO_CARD) {
            filePathList.add(getAbsoluteContentsPath(cardModel.thumbnailPath));
        }

        final String zipFilePath = context.getCacheDir() + File.separator + key + ".zip";
        try {
            FileUtil.zip(filePathList.toArray(new String[filePathList.size()]), zipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipFilePath;
    }

    private String getAbsoluteContentsPath(String filePath){
        return ContentsUtil.getContentFileFromContentPath(filePath).getAbsolutePath();
    }

}