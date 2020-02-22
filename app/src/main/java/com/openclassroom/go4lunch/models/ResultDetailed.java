package com.openclassroom.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultDetailed implements Parcelable {

    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHoursDetailed openingHours;
    @SerializedName("place_id")
    @Expose
    private String place_id;
    @SerializedName("international_phone_number")
    @Expose
    private String internationalPhoneNumber;
    @SerializedName("website")
    @Expose
    private String website;


    protected ResultDetailed(Parcel in) {
        formattedAddress = in.readString();
        openingHours = in.readParcelable(OpeningHoursDetailed.class.getClassLoader());
        place_id = in.readString();
        internationalPhoneNumber = in.readString();
        website = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(formattedAddress);
        dest.writeParcelable(openingHours, flags);
        dest.writeString(place_id);
        dest.writeString(internationalPhoneNumber);
        dest.writeString(website);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResultDetailed> CREATOR = new Creator<ResultDetailed>() {
        @Override
        public ResultDetailed createFromParcel(Parcel in) {
            return new ResultDetailed(in);
        }

        @Override
        public ResultDetailed[] newArray(int size) {
            return new ResultDetailed[size];
        }
    };

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public OpeningHoursDetailed getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHoursDetailed openingHours) {
        this.openingHours = openingHours;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}