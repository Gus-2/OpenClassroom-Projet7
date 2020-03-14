package com.openclassroom.go4lunch.utils;

import com.openclassroom.go4lunch.models.Location;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.models.Result;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by de Meeûs Augustin on 2020-03-04
 **/
public class Checks {

    public static boolean checkIfGoodDate(Date dateToCheck){
        if(dateToCheck == null) return false;
        Calendar calendarNow;
        Calendar calendarChoosenRestaurant = Calendar.getInstance();
        calendarChoosenRestaurant.setTime(dateToCheck);
        calendarNow = Calendar.getInstance();
        if(calendarNow.get(Calendar.YEAR) == calendarChoosenRestaurant.get(Calendar.YEAR)
                && calendarNow.get(Calendar.DAY_OF_YEAR) == calendarChoosenRestaurant.get(Calendar.DAY_OF_YEAR)) return  true;
        return  false;
    }

    public static boolean isContainedInto(NearbyPlaces nearbyPlaces, Location location){
        for(Result result: nearbyPlaces.getResults()){
            if(result.getGeometry().getLocation().getLat().equals(location.getLat()) &&  result.getGeometry().getLocation().getLng().equals(location.getLng())) return  true;
        }
        return false;
    }
}
