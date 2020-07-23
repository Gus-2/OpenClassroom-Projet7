package com.openclassroom.go4lunch.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.database.FirebaseHelper;
import com.openclassroom.go4lunch.models.DataUserConnected;
import com.openclassroom.go4lunch.utils.Checks;
import com.openclassroom.go4lunch.utils.ConstantString;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by de Meeûs Augustin on 2020-03-06
 **/
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override

    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().get(ConstantString.EATING).equals(ConstantString.OK_UPPERCASE)){
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

    @SuppressWarnings("ConstantConditions")
    private void notificationDialog(DataUserConnected dataUserConnected) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(ConstantString.NOTIFICATION_CHANNEL_ID, ConstantString.MY_NOTIFICATIONS, NotificationManager.IMPORTANCE_MAX);

            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        String[] names = dataUserConnected.getUserFirstNameAndLastname().split(" ");

        List<String> dataUserConnecteds = new ArrayList<>();
        FirebaseHelper.getUserCollection().get().addOnCompleteListener(task -> {
            if(task.getResult() != null){
                for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                    DataUserConnected dataUser = queryDocumentSnapshot.toObject(DataUserConnected.class);
                    if(dataUserConnected.getChoosenRestaurantForTheDay() != null && dataUserConnected.getChoosenRestaurantForTheDay().equals(dataUser.getChoosenRestaurantForTheDay()) && Checks.checkIfGoodDate(dataUser.getDateOfTheChoosenRestaurant())){
                        dataUserConnecteds.add(dataUser.getUserFirstNameAndLastname().split(" ")[0]);
                    }
                }
            }

            String text = String.format(getResources().getString(R.string.address_lunch_at), dataUserConnected.getAddressOfTheChoosenRestaurant());

            if(dataUserConnecteds.size() > 0){
                text += String.format(getResources().getString(R.string.colleague_joinig_lunch_at), dataUserConnecteds.toString().replace("[", "").replace("]", ""));
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), ConstantString.NOTIFICATION_CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(String.format(getResources().getString(R.string.having_lunch_at),names[0], dataUserConnected.getNameOfTheChoosenRestaurant()));

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(String.format(getResources().getString(R.string.having_lunch_at),names[0], dataUserConnected.getNameOfTheChoosenRestaurant()));
            bigTextStyle.bigText(text);
            notificationBuilder.setStyle(bigTextStyle);
            notificationManager.notify(1, notificationBuilder.build());
        });
    }

    public static void getToken(Context context) {
        Log.d("Token ", "" + context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty"));
        context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }
}
