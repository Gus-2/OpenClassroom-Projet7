package com.openclassroom.go4lunch.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by de Meeûs Augustin on 2020-02-08
 **/
public class SecurityChecks {

    public static void checkGooglePlayServices(Context context) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                Toast.makeText(context, "Erreur : GooglePlaService is unavailable !", Toast.LENGTH_SHORT);
            }
        }
    }
}
