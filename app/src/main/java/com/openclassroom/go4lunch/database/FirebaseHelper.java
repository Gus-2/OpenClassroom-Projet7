package com.openclassroom.go4lunch.database;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.UserDataConverter;
import com.openclassroom.go4lunch.models.DataUserConnected;

import java.util.ArrayList;

/**
 * Created by de Meeûs Augustin on 2020-03-02
 **/
public class FirebaseHelper {

    private static final String COLLECTION_NAME = "users";

    private static CollectionReference getUserCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static DocumentReference getUserDocument(String userId){
        return getUserCollection().document(userId);
    }

    // --- CREATE ---
    public static Task<Void> createUser(String uid, String firstnameAndLastname) {
        DataUserConnected newUser = new DataUserConnected(uid, firstnameAndLastname, null, null, new ArrayList<>());
        return getUserCollection().document(uid).set(newUser);
    }


    public static Task<QuerySnapshot> getUserData(String userId){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).whereEqualTo("userId", userId).get();
    }


}
