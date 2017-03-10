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
        this.context = context;
    }


    private FirebaseSynchronizer() {
        categoryDatabase = FirebaseDatabase.getInstance().getReference("Category");
        cardDatabase = FirebaseDatabase.getInstance().getReference("Card");
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void uploadDataToFirebase(List<CategoryModel> categoryModelList, List<CardModel> cardModelList) {
        String userId = "MILO";

        uploadData(categoryModelList, cardModelList, userId);

        //File upload
        String voicesFolderPath = ((AngelmanApplication) context.getApplicationContext()).getVoiceFolder();
        String imagesFolderPath = ((AngelmanApplication) context.getApplicationContext()).getImageFolder();

        Uri folder = Uri.fromFile(new File());
        StorageReference voiceReference = storageReference.child(userId).child("voice/");
        voiceReference.putFile(Uri.parse(voicesFolderPath));



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
