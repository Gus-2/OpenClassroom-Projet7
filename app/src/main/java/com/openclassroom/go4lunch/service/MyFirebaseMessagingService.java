package com.openclassroom.go4lunch.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.UserDataConverter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.database.FirebaseHelper;
import com.openclassroom.go4lunch.models.DataUserConnected;
import com.openclassroom.go4lunch.utils.Checks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by de Meeûs Augustin on 2020-03-06
 **/
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String NOTIFICATION_CHANNEL_ID = "where_am_i_eating_notification";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getData().get("Eating").equals("OK")){
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                FirebaseHelper.getUserData(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataUserConnected dataUserConnected = task.getResult().getDocuments().get(0).toObject(DataUserConnected.class);
                        if(dataUserConnected.getChoosenRestaurantForTheDay() != null && Checks.checkIfGoodDate(dataUserConnected.getDateOfTheChoosenRestaurant())){
                            notificationDialog(dataUserConnected);
                        }
                    }
                });
            }
        }


    }

    private void notificationDialog(DataUserConnected dataUserConnected) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);

            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        String[] names = dataUserConnected.getUserFirstNameAndLastname().split(" ");

        String title = "Hey " + names[0] + ", you're having lunch at " + dataUserConnected.getNameOfTheChoosenRestaurant() + " today ";
        List<String> dataUserConnecteds = new ArrayList<>();
        FirebaseHelper.getUserCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                    DataUserConnected dataUser = queryDocumentSnapshot.toObject(DataUserConnected.class);
                    if(dataUserConnected.getChoosenRestaurantForTheDay() != null && dataUserConnected.getChoosenRestaurantForTheDay().equals(dataUser.getChoosenRestaurantForTheDay()) && Checks.checkIfGoodDate(dataUser.getDateOfTheChoosenRestaurant())){
                        dataUserConnecteds.add(dataUser.getUserFirstNameAndLastname().split(" ")[0]);
                    }
                }

                String text = " Address : " + dataUserConnected.getAddressOfTheChoosenRestaurant() + "\n";

                if(dataUserConnecteds.size() > 0){
                    text += "Colleagues that are joining you : " + dataUserConnecteds.toString().replace("[", "").replace("]", "");
                }

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);
                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker(title);

                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle(title);
                bigTextStyle.bigText(text);

                notificationBuilder.setStyle(bigTextStyle);

                notificationManager.notify(1, notificationBuilder.build());

            }
        });

    }

    public static String getToken(Context context) {
        Log.d("Token ", "" + context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty"));
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }
}
