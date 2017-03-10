package act.sds.samsung.angelman.data.firebase;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;

public class FirebaseSynchronizer {


    private DatabaseReference categoryDatabase;
    private DatabaseReference cardDatabase;


    private Context context;

    public FirebaseSynchronizer(Context context) {

        this.context = context;

    }


    private FirebaseSynchronizer() {
        categoryDatabase = FirebaseDatabase.getInstance().getReference("Category");
        cardDatabase = FirebaseDatabase.getInstance().getReference("Card" );
    }

    public void uploadDataToFirebase(List<CategoryModel> categoryModelList, List<CardModel> cardModelList) {
        String userId = "MILO";

        uploadData(categoryModelList, cardModelList, userId);



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
