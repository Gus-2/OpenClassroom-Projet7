package com.openclassroom.go4lunch.di;

import com.openclassroom.go4lunch.utils.JsonPlaceHolderApi;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by de Meeûs Augustin on 2020-02-17
 **/
public class DetailsPlacesApiRequestSingleton {

    private static DetailsPlacesApiRequestSingleton instance = null;
    private static JsonPlaceHolderApi jsonPlaceHolderApi;

    private DetailsPlacesApiRequestSingleton(){

    }

    public static DetailsPlacesApiRequestSingleton getInstanceDetailsPlaces() {
        if(instance == null){
            instance = new DetailsPlacesApiRequestSingleton();
            Retrofit retrofitDetailsPlaces = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/api/place/details/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();


            jsonPlaceHolderApi = retrofitDetailsPlaces.create(JsonPlaceHolderApi.class);
        }

        return instance;
    }

    public  JsonPlaceHolderApi getJsonPlaceHolderApi(){
        return jsonPlaceHolderApi;
    }



}
