package com.openclassroom.go4lunch.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.openclassroom.go4lunch.R;

/**
 * Created by de Meeûs Augustin on 2020-02-08
 **/
public class SecurityChecks {

    public static void checkGooglePlayServices(Context context) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                Toast.makeText(context, R.string.google_play_service_unavailable, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
