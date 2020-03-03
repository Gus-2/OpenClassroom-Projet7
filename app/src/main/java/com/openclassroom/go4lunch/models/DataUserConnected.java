package com.openclassroom.go4lunch.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by de Meeûs Augustin on 2020-03-01
 **/
public class DataUserConnected implements Serializable {

    private String userId;
    private String userFirstNameAndLastname;
    private String choosenRestaurantForTheDay;
    private Date dateOfTheChoosenRestaurant;
    private List<String> likedRestaurants;

    public DataUserConnected(String userId, String userFirstNameAndLastname, String choosenRestaurantForTheDay, Date dateOfTheChoosenRestaurant, List<String> likedRestaurants) {
        this.userId = userId;
        this.userFirstNameAndLastname = userFirstNameAndLastname;
        this.choosenRestaurantForTheDay = choosenRestaurantForTheDay;
        this.dateOfTheChoosenRestaurant = dateOfTheChoosenRestaurant;
        this.likedRestaurants = likedRestaurants;
    }

    public DataUserConnected(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChoosenRestaurantForTheDay() {
        return choosenRestaurantForTheDay;
    }

    public void setChoosenRestaurantForTheDay(String choosenRestaurantForTheDay) {
        this.choosenRestaurantForTheDay = choosenRestaurantForTheDay;
    }

    public Date getDateOfTheChoosenRestaurant() {
        return dateOfTheChoosenRestaurant;
    }

    public void setDateOfTheChoosenRestaurant(Date dateOfTheChoosenRestaurant) {
        this.dateOfTheChoosenRestaurant = dateOfTheChoosenRestaurant;
    }

    public List<String> getLikedRestaurants() {
        return likedRestaurants;
    }

    public void setLikedRestaurants(List<String> likedRestaurants) {
        this.likedRestaurants = likedRestaurants;
    }


    public String getUserFirstNameAndLastname() {
        return userFirstNameAndLastname;
    }

    public void setUserFirstNameAndLastname(String userFirstNameAndLastname) {
        this.userFirstNameAndLastname = userFirstNameAndLastname;
    }
}
