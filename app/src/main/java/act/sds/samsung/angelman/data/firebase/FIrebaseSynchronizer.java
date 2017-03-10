package act.sds.samsung.angelman.data.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;

public class FirebaseSynchronizer {


    private DatabaseReference categoryDatabase ;
    private DatabaseReference cardDatabase ;

    private static FirebaseSynchronizer instance;

    private FirebaseSynchronizer () {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        categoryDatabase = database.getReference("Category");
        cardDatabase = database.getReference("Card");

    }

    public static FirebaseSynchronizer getInstance () {

        if(instance == null){
            instance = new FirebaseSynchronizer();
            return instance;
        }
        return instance;

    }

    public void uploadDataToFirebase(List<CategoryModel> categoryModelList , List<CardModel> cardModelList){

        //String userId = sharedPreferences.get("USER_ID"); // 이런식으로 동작이 이루어져야함
        String userId = "MILO";


        //upload category
        for( CategoryModel categoryModel : categoryModelList){
            categoryDatabase.child(userId).child(categoryModel._id).setValue(categoryModel);
        }

        //upload card
        for( CardModel cardModel : cardModelList){
            cardDatabase.child(userId).child(cardModel._id).setValue(cardModel);
        }
    }





}
