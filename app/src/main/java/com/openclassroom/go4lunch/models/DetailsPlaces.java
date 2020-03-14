package com.openclassroom.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetailsPlaces implements Parcelable {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("result")
    @Expose
    private ResultDetailed result;
    @SerializedName("status")
    @Expose
    private String status;

    public DetailsPlaces(List<Object> htmlAttributions, ResultDetailed result, String status) {
        this.htmlAttributions = htmlAttributions;
        this.result = result;
        this.status = status;
    }

    protected DetailsPlaces(Parcel in) {
        result = in.readParcelable(Result.class.getClassLoader());
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(result, flags);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DetailsPlaces> CREATOR = new Creator<DetailsPlaces>() {
        @Override
        public DetailsPlaces createFromParcel(Parcel in) {
            return new DetailsPlaces(in);
        }

        @Override
        public DetailsPlaces[] newArray(int size) {
            return new DetailsPlaces[size];
        }
    };

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public ResultDetailed getResult() {
        return result;
    }

    public void setResult(ResultDetailed result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
