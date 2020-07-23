package com.openclassroom.go4lunch;

import com.openclassroom.go4lunch.models.Location;
import com.openclassroom.go4lunch.utils.Checks;

import org.junit.Test;
import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by de Meeûs Augustin on 12/03/2020
 **/
public class ChecksTest {



    @Test
    public void testCheckIfGoodDate(){
        assertFalse(Checks.checkIfGoodDate(null));

        Calendar calendarNow;
        calendarNow = Calendar.getInstance();
        assertTrue(Checks.checkIfGoodDate(calendarNow.getTime()));

        calendarNow.set(1999, 6, 21);
        assertFalse(Checks.checkIfGoodDate(calendarNow.getTime()));

        calendarNow.set(2020, 3, 11);
        assertFalse(Checks.checkIfGoodDate(calendarNow.getTime()));
    }


    @Test
    public void testIsContainedInto(){
        assertTrue(Checks.isContainedInto(Datas.nearbyPlaces, new Location(40.741895, -73.989308)));
        assertFalse(Checks.isContainedInto(Datas.nearbyPlaces, new Location(40.740895, -23.989308)));
        assertFalse(Checks.isContainedInto(Datas.nearbyPlaces, new Location(0.0, 0.0)));
    }
}
