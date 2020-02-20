package com.openclassroom.go4lunch.di;

import com.openclassroom.go4lunch.utils.JsonPlaceHolderApi;
import com.readystatesoftware.chuck.ChuckInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by de Meeûs Augustin on 2020-02-09
 **/
public class NearbyPlacesApiRequestSingleton {

    private static NearbyPlacesApiRequestSingleton instance = null;
    private static Retrofit retrofitNearbyPlaces;
    private static JsonPlaceHolderApi jsonPlaceHolderApi;

    private NearbyPlacesApiRequestSingleton(){

    }

    public static NearbyPlacesApiRequestSingleton getInstanceNearbyPlaces() {
        if(instance == null){
            //OkHttpClient httpClient = new OkHttpClient().newBuilder().addInterceptor(new ChuckInterceptor());
            instance = new NearbyPlacesApiRequestSingleton();
            retrofitNearbyPlaces = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();


            jsonPlaceHolderApi = retrofitNearbyPlaces.create(JsonPlaceHolderApi.class);
        }

        return instance;
    }

    public static JsonPlaceHolderApi getJsonPlaceHolderApi(){
        return jsonPlaceHolderApi;
    }




}
