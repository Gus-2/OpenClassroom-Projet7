package com.openclassroom.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Open implements Parcelable {

    @SerializedName("day")
    @Expose
    private Integer day;
    @SerializedName("time")
    @Expose
    private String time;

    protected Open(Parcel in) {
        if (in.readByte() == 0) {
            day = null;
        } else {
            day = in.readInt();
        }
        time = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (day == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(day);
        }
        dest.writeString(time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Open> CREATOR = new Creator<Open>() {
        @Override
        public Open createFromParcel(Parcel in) {
            return new Open(in);
        }

        @Override
        public Open[] newArray(int size) {
            return new Open[size];
        }
    };

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
