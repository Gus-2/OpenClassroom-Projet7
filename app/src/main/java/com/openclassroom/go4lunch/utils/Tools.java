package com.openclassroom.go4lunch.utils;

import android.location.Location;

/**
 * Created by de Meeûs Augustin on 2020-03-07
 **/
public class Tools {

    public static int getDistanceBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {
        float[] distance = new float[2];

        Location.distanceBetween( lat1, lon1,
                lat2, lon2, distance);
        return (int) distance[0];
    }
}
