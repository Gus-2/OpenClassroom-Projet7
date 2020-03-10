package com.openclassroom.go4lunch.models;



import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by de Meeûs Augustin on 10/03/2020
 **/
public class Message {
    private String message;
    private String name;
    private String imageUrl;
    private Date id;

    public Message(String message, String name, String imageUrl) {
        this.message = message;
        this.name = name;
        this.imageUrl = imageUrl;
        Calendar calendar = Calendar.getInstance();
        id =  calendar.getTime();
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getId() {
        return id;
    }

    public void setId(Date id) {
        this.id = id;
    }
}
