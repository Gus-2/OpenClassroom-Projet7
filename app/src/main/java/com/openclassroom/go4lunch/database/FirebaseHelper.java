package com.openclassroom.go4lunch.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassroom.go4lunch.models.DataUserConnected;
import com.openclassroom.go4lunch.models.Message;
import com.openclassroom.go4lunch.utils.ConstantString;

import java.util.ArrayList;

/**
 * Created by de Meeûs Augustin on 2020-03-02
 **/
public class FirebaseHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String CHAT_COLLECTION_NAME = "chat_messages";

    public static CollectionReference getUserCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static CollectionReference getChatMessageCollection(){
        return FirebaseFirestore.getInstance().collection(CHAT_COLLECTION_NAME);
    }

    public static DocumentReference getUserDocument(String userId){
        return getUserCollection().document(userId);
    }

    public static Task<Void> createUser(String uid, String firstnameAndLastname, String photoUrl) {
        DataUserConnected newUser = new DataUserConnected(uid, firstnameAndLastname, null, null, null, photoUrl, null, new ArrayList<>());
        return getUserCollection().document(uid).set(newUser);
    }

    public static Task<Void> addMessage(Message message) {
        return getChatMessageCollection().document(message.getId().toString()).set(message);
    }


    public static Task<QuerySnapshot> getUserData(String userId){
        return getUserCollection().whereEqualTo(ConstantString.USER_ID, userId).get();
    }

}
