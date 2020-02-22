package com.openclassroom.go4lunch.utils;

import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.Example;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by de Meeûs Augustin on 2020-02-08
 **/
public interface JsonPlaceHolderApi {

    @GET("json?radius=1500&type=restaurant")
    Observable<Example> getExample(
            @Query("location") String location,
            @Query("key") String key
    );

    @GET("json?fields=website,international_phone_number,formatted_address,opening_hours,place_id")
    Observable<DetailsPlaces> getPlaceDetails(
        @Query("place_id") String placeId,
        @Query("key") String key
    );
}
