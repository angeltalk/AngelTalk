package act.sds.samsung.angelman.data.firebase;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;

public class FirebaseSynchronizer {


    private DatabaseReference categoryDatabase;
    private DatabaseReference cardDatabase;
    private StorageReference storageReference;


    private Context context;

    public FirebaseSynchronizer(Context context) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        categoryDatabase = firebaseDatabase.getReference("Category");
        cardDatabase = firebaseDatabase.getReference("Card");
        storageReference = FirebaseStorage.getInstance().getReference();
        this.context = context;
    }

    public void uploadDataToFirebase(List<CategoryModel> categoryModelList, List<CardModel> cardModelList) {

        String userId = "MILO";
        uploadData(categoryModelList, cardModelList, userId);
        uploadFile(userId);

    }

    private void uploadFile(String userId) {
        String voicesFolderPath = ((AngelmanApplication) context.getApplicationContext()).getVoiceFolder();
        StorageReference voiceReference = storageReference.child(userId).child("voice/");
        File voicesFolder = new File(voicesFolderPath);
        for (File f : voicesFolder.listFiles()) {
            if (f.isFile())
            {
                voiceReference.child(f.getName()).putFile(Uri.fromFile(f));
            }

        }


        String imagesFolderPath = ((AngelmanApplication) context.getApplicationContext()).getImageFolder();
        StorageReference imagesReference = storageReference.child(userId).child("image/");
        File imagesFolder = new File(imagesFolderPath);
        for (File f : imagesFolder.listFiles()) {
            if (f.isFile())
            {
                imagesReference.child(f.getName()).putFile(Uri.fromFile(f));
            }
        }

    }

    private void uploadData(List<CategoryModel> categoryModelList, List<CardModel> cardModelList, String userId) {
        for (CategoryModel categoryModel : categoryModelList) {
            categoryDatabase.child(userId).child(categoryModel._id).setValue(categoryModel);
        }
        for (CardModel cardModel : cardModelList) {
            cardDatabase.child(userId).child(cardModel._id).setValue(cardModel);
        }
    }


}
