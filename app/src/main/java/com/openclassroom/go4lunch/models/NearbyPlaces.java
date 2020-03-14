
package com.openclassroom.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class NearbyPlaces implements Parcelable {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = new ArrayList<>();

    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;

    @SerializedName("results")
    @Expose
    private List<Result> results = new ArrayList<>();

    @Expose
    private String status;

    public NearbyPlaces(Parcel in) {
        nextPageToken = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nextPageToken);
        dest.writeString(status);
    }

    public NearbyPlaces(List<Object> htmlAttributions, String nextPageToken, List<Result> results, String status) {
        this.htmlAttributions = htmlAttributions;
        this.nextPageToken = nextPageToken;
        this.results = results;
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NearbyPlaces> CREATOR = new Creator<NearbyPlaces>() {
        @Override
        public NearbyPlaces createFromParcel(Parcel in) {
            return new NearbyPlaces(in);
        }

        @Override
        public NearbyPlaces[] newArray(int size) {
            return new NearbyPlaces[size];
        }
    };

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
