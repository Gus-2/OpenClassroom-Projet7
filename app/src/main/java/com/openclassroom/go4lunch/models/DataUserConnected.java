package com.openclassroom.go4lunch.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by de Meeûs Augustin on 2020-03-01
 **/
public class DataUserConnected implements Parcelable {

    private String userId;
    private String userFirstNameAndLastname;
    private String choosenRestaurantForTheDay;
    private String nameOfTheChoosenRestaurant;
    private String addressOfTheChoosenRestaurant;
    private String photoUrl;
    private Date dateOfTheChoosenRestaurant;
    private List<String> likedRestaurants;

    public DataUserConnected(String userId, String userFirstNameAndLastname, String choosenRestaurantForTheDay, String nameOfTheChoosenRestaurant, String addressOfTheChoosenRestaurant, String photoUrl, Date dateOfTheChoosenRestaurant, List<String> likedRestaurants) {
        this.userId = userId;
        this.userFirstNameAndLastname = userFirstNameAndLastname;
        this.choosenRestaurantForTheDay = choosenRestaurantForTheDay;
        this.nameOfTheChoosenRestaurant = nameOfTheChoosenRestaurant;
        this.addressOfTheChoosenRestaurant = addressOfTheChoosenRestaurant;
        this.photoUrl = photoUrl;
        this.dateOfTheChoosenRestaurant = dateOfTheChoosenRestaurant;
        this.likedRestaurants = likedRestaurants;
    }

    public DataUserConnected(){

    }


    protected DataUserConnected(Parcel in) {
        userId = in.readString();
        userFirstNameAndLastname = in.readString();
        choosenRestaurantForTheDay = in.readString();
        nameOfTheChoosenRestaurant = in.readString();
        addressOfTheChoosenRestaurant = in.readString();
        photoUrl = in.readString();
        likedRestaurants = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userFirstNameAndLastname);
        dest.writeString(choosenRestaurantForTheDay);
        dest.writeString(nameOfTheChoosenRestaurant);
        dest.writeString(addressOfTheChoosenRestaurant);
        dest.writeString(photoUrl);
        dest.writeStringList(likedRestaurants);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataUserConnected> CREATOR = new Creator<DataUserConnected>() {
        @Override
        public DataUserConnected createFromParcel(Parcel in) {
            return new DataUserConnected(in);
        }

        @Override
        public DataUserConnected[] newArray(int size) {
            return new DataUserConnected[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFirstNameAndLastname() {
        return userFirstNameAndLastname;
    }

    public void setUserFirstNameAndLastname(String userFirstNameAndLastname) {
        this.userFirstNameAndLastname = userFirstNameAndLastname;
    }

    public String getChoosenRestaurantForTheDay() {
        return choosenRestaurantForTheDay;
    }

    public void setChoosenRestaurantForTheDay(String choosenRestaurantForTheDay) {
        this.choosenRestaurantForTheDay = choosenRestaurantForTheDay;
    }

    public String getNameOfTheChoosenRestaurant() {
        return nameOfTheChoosenRestaurant;
    }

    public void setNameOfTheChoosenRestaurant(String nameOfTheChoosenRestaurant) {
        this.nameOfTheChoosenRestaurant = nameOfTheChoosenRestaurant;
    }

    public String getAddressOfTheChoosenRestaurant() {
        return addressOfTheChoosenRestaurant;
    }

    public void setAddressOfTheChoosenRestaurant(String addressOfTheChoosenRestaurant) {
        this.addressOfTheChoosenRestaurant = addressOfTheChoosenRestaurant;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
}
