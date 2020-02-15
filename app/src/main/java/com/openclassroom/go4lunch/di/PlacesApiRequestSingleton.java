package com.openclassroom.go4lunch.di;

import com.openclassroom.go4lunch.utils.JsonPlaceHolderApi;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by de Meeûs Augustin on 2020-02-09
 **/
public class PlacesApiRequestSingleton {

    private static PlacesApiRequestSingleton instance = null;
    private static Retrofit retrofit;
    private static JsonPlaceHolderApi jsonPlaceHolderApi;

    private PlacesApiRequestSingleton(){

    }

    public static PlacesApiRequestSingleton getInstance(String URL) {
        if(instance == null){
            instance = new PlacesApiRequestSingleton();
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();


            jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        }
        return instance;
    }

    public static JsonPlaceHolderApi getJsonPlaceHolderApi(){
        return jsonPlaceHolderApi;
    }




}
