package com.openclassroom.go4lunch;

import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by de Meeûs Augustin on 14/03/2020
 **/
public class RestaurantDetailFormatTest {

    @Test
    public void testParceAddress(){
        assertEquals(RestaurantDetailFormat.parseAddress("Rue des Combattants 77, 1310 La Hulpe"), "77 rue des combattants" );
        assertEquals(RestaurantDetailFormat.parseAddress("Rue des alvéoles 12, 1310 La Hulpe"), "12 rue des alvéoles" );
        assertNotEquals(RestaurantDetailFormat.parseAddress("Rue des alvéoles 12, 1310 La Hulpe"), "epzcfjizepfo" );
    }

    @Test
    public void testGestDetailPlacesFromPlaceID(){
        assertEquals(Datas.detailsPlaces.get(0), RestaurantDetailFormat.getDetailPlacesFromPlaceID(Datas.detailsPlaces, Datas.detailsPlaces.get(0).getResult().getPlaceId()));
        assertNotEquals(Datas.detailsPlaces.get(2), RestaurantDetailFormat.getDetailPlacesFromPlaceID(Datas.detailsPlaces, Datas.detailsPlaces.get(1).getResult().getPlaceId()));
        assertNotEquals(new DetailsPlaces(null, null, null), RestaurantDetailFormat.getDetailPlacesFromPlaceID(Datas.detailsPlaces, Datas.detailsPlaces.get(2).getResult().getPlaceId()));
    }

    @Test
    public void testGetPositionFromPlaceID(){
        assertEquals(0, RestaurantDetailFormat.getPositionFromPlaceID(Datas.nearbyPlaces, Datas.ID1));
        assertEquals(-1, RestaurantDetailFormat.getPositionFromPlaceID(Datas.nearbyPlaces, "Cizefoizefzeoifhzeofhozfhzeo"));
        assertEquals(1, RestaurantDetailFormat.getPositionFromPlaceID(Datas.nearbyPlaces, Datas.ID2));
    }


}
