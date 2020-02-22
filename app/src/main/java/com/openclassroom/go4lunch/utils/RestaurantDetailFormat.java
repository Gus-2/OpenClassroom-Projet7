package com.openclassroom.go4lunch.utils;

import android.content.Context;

import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.ui.listRestaurant.MyRestaurantAdapter;

/**
 * Created by de Meeûs Augustin on 2020-02-21
 **/
public class RestaurantDetailFormat {

 public static String parseAddress(String type, String addressWithCountry){
     String address = addressWithCountry.substring(0, addressWithCountry.indexOf(","));
     String numberRestaurant = address.substring(address.lastIndexOf(" ")+1, address.length());
     return numberRestaurant + " " + (address.substring(0, address.lastIndexOf(" ")).toLowerCase());
 }

}
