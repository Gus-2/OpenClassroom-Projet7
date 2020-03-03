package com.openclassroom.go4lunch.utils;

import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.NearbyPlaces;

import java.util.List;

/**
 * Created by de Meeûs Augustin on 2020-02-21
 **/
public class RestaurantDetailFormat {

     public static String parseAddress(String type, String addressWithCountry){
         String address = addressWithCountry.substring(0, addressWithCountry.indexOf(","));
         String numberRestaurant = address.substring(address.lastIndexOf(" ")+1, address.length());
         return numberRestaurant + " " + (address.substring(0, address.lastIndexOf(" ")).toLowerCase());
     }

     public static DetailsPlaces getDetailPlacesFromPlaceID(List<DetailsPlaces> detailsPlaces, String placeID){
         for(DetailsPlaces detailPlace : detailsPlaces){
             if(detailPlace.getResult().getPlaceId().equals(placeID)) return detailPlace;
         }
         return null;
     }

    public static int getPositionFromPlaceID(NearbyPlaces nearbyPlaces, String placeID){
        for(int i = 0; i < nearbyPlaces.getResults().size(); i++){
            if(nearbyPlaces.getResults().get(i).getPlaceId().equals(placeID)) return i;
        }
        return -1;
    }



}
