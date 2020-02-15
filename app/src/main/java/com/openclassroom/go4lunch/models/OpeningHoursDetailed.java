package com.openclassroom.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpeningHoursDetailed implements Parcelable {

    @SerializedName("open_now")
    @Expose
    private Boolean openNow;
    @SerializedName("periods")
    @Expose
    private List<Period> periods = null;
    @SerializedName("weekday_text")
    @Expose
    private List<String> weekdayText = null;

    protected OpeningHoursDetailed(Parcel in) {
        byte tmpOpenNow = in.readByte();
        openNow = tmpOpenNow == 0 ? null : tmpOpenNow == 1;
        weekdayText = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (openNow == null ? 0 : openNow ? 1 : 2));
        dest.writeStringList(weekdayText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OpeningHoursDetailed> CREATOR = new Creator<OpeningHoursDetailed>() {
        @Override
        public OpeningHoursDetailed createFromParcel(Parcel in) {
            return new OpeningHoursDetailed(in);
        }

        @Override
        public OpeningHoursDetailed[] newArray(int size) {
            return new OpeningHoursDetailed[size];
        }
    };

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    public List<String> getWeekdayText() {
        return weekdayText;
    }

    public void setWeekdayText(List<String> weekdayText) {
        this.weekdayText = weekdayText;
    }

}