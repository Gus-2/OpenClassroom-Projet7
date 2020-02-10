package com.openclassroom.go4lunch.di;

import com.openclassroom.go4lunch.utils.JsonPlaceHolderApi;

import java.net.URL;

import javax.inject.Singleton;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by de Meeûs Augustin on 2020-02-09
 **/
public class PlacesApiRequestSingleton {

    private static PlacesApiRequestSingleton instance = null;
    private static Retrofit retrofit;
    private final static String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    private static JsonPlaceHolderApi jsonPlaceHolderApi;

    private PlacesApiRequestSingleton(){

    }

    public static PlacesApiRequestSingleton getInstance() {
        if(instance == null){
            instance = new PlacesApiRequestSingleton();
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        }
        return instance;
    }

    public static JsonPlaceHolderApi getJsonPlaceHolderApi(){
        return jsonPlaceHolderApi;
    }
}
