package com.openclassroom.go4lunch;

import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.Geometry;
import com.openclassroom.go4lunch.models.Location;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.models.Result;
import com.openclassroom.go4lunch.models.ResultDetailed;

import java.util.Arrays;
import java.util.List;

/**
 * Created by de Meeûs Augustin on 14/03/2020
 **/
class Datas
{
    public static String ID1 = "ChIJrTLr-GyuEmsRBfy61i59si0";
    public static String ID2 = "ChIJrTLr-Gyuzepfjjzpoejffzp";
    public static String ID3 = "ChIJrTLr-Gyfezfzeefzefjffzp";

    public static ResultDetailed resultsDetailed1 = new ResultDetailed(null, null, ID1, null, null);
    public static ResultDetailed resultsDetailed2 = new ResultDetailed(null, null, ID2, null, null);
    public static ResultDetailed resultsDetailed3 = new ResultDetailed(null, null, ID3, null, null);

    static List<DetailsPlaces> detailsPlaces = Arrays.asList(
            new DetailsPlaces(null, resultsDetailed1 , null),
            new DetailsPlaces(null, resultsDetailed2 , null),
            new DetailsPlaces(null, resultsDetailed3 , null)
    );


    private static List<Result> results = Arrays.asList(
            new Result(new Geometry(new Location(40.741895, -73.989308), null), null, null, null, null,null, ID1, null, null, null, null, null, null, null, null),
            new Result(new Geometry(new Location(30.241895, -34.989308), null), null, null, null, null,null, ID2, null, null, null, null, null, null, null, null),
            new Result(new Geometry(new Location(12.241895, -8.989308), null), null, null, null, null,null, ID3, null, null, null, null, null, null, null, null)
    );

    static NearbyPlaces nearbyPlaces = new NearbyPlaces(null, null, results, null);
}
