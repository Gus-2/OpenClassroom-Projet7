package com.openclassroom.go4lunch.utils;

import com.openclassroom.go4lunch.di.DetailsPlacesApiRequestSingleton;
import com.openclassroom.go4lunch.di.NearbyPlacesApiRequestSingleton;
import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.NearbyPlaces;
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

    public static NearbyPlaces nearbyPlaces;

    public static Observable<NearbyPlaces> streamFetchNearbyRestaurant(String location, String key){
        return NearbyPlacesApiRequestSingleton.getInstanceNearbyPlaces().getJsonPlaceHolderApi()
                .getExample(location, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    private static Observable<DetailsPlaces> streamFetchDetailRestaurant(String placeId, String key){
        return DetailsPlacesApiRequestSingleton.getInstanceDetailsPlaces().getJsonPlaceHolderApi()
                .getPlaceDetails(placeId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<List<DetailsPlaces>> getNearbyRestaurantThenFetchTheirDetails(String location, String key){
        return streamFetchNearbyRestaurant(location, key)
                .map(RetrofitStreams::setExampleAndReturnResult)
                .flatMapIterable(results -> results)
                .flatMap(result -> streamFetchDetailRestaurant(result.getPlaceId(), key ))
                .toList()
                .toObservable();
    }

    private static List<Result> setExampleAndReturnResult(NearbyPlaces nearbyPlaces){
        RetrofitStreams.nearbyPlaces = nearbyPlaces;
        return nearbyPlaces.getResults();
    }



}
