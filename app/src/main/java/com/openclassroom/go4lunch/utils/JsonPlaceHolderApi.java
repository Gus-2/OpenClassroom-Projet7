package com.openclassroom.go4lunch.utils;

import com.openclassroom.go4lunch.models.Example;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by de Meeûs Augustin on 2020-02-08
 **/
public interface JsonPlaceHolderApi {

    @GET("json?radius=1500&type=restaurant")
    Call<Example> getExample(
            @Query("location") String location,
            @Query("key") String key
    );
}
