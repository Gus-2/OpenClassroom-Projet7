package com.openclassroom.go4lunch.utils;

import android.util.Log;

import com.openclassroom.go4lunch.di.PlacesApiRequestSingleton;
import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.Example;
import com.openclassroom.go4lunch.models.Result;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by de Meeûs Augustin on 2020-02-13
 **/
public class RetrofitStreams {

    public static Example nearbyPlaces;

    public static Observable<Example> streamFetchNearbyRestaurant(String url, String location, String key){
        JsonPlaceHolderApi jsonPlaceHolderApi = PlacesApiRequestSingleton.getInstance(url).getJsonPlaceHolderApi();
        return jsonPlaceHolderApi.getExample(location, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<DetailsPlaces> streamFetchDetailRestaurant(String url, String placeId, String key){
        try {
            TimeUnit.MICROSECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JsonPlaceHolderApi jsonPlaceHolderApi = PlacesApiRequestSingleton.getInstance(url).getJsonPlaceHolderApi();
        return jsonPlaceHolderApi.getPlaceDetails(placeId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<List<DetailsPlaces>> getNearbyRestaurantThenFetchTheirDetails(String urlNearbyRestaurant,
            String urlDetailsRestaurant, String location, String key){

        return streamFetchNearbyRestaurant(urlNearbyRestaurant, location, key)
                .map(example -> RetrofitStreams.setExampleAndReturnResult(example))
                .flatMapIterable(results -> results)
                .flatMap(result -> streamFetchDetailRestaurant(urlDetailsRestaurant, result.getPlaceId(), key ))
                .toList()
                .toObservable();
    }

    private static List<Result> setExampleAndReturnResult(Example example){
        nearbyPlaces = example;
        return example.getResults();
    }



}
