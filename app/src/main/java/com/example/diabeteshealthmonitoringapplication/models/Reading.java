package com.example.diabeteshealthmonitoringapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class Reading implements Parcelable {
    private String from,reading,time,date;
    @Nullable
    private String suggestion;

    /**
     * @NoArgsConstructor
     */
    public Reading() {

    }

    /**
     * All Args Constructor
     * @param from FromUId
     * @param reading Reading of that day
     * @param date Date of that day
     * @param suggestion Patient comments
     */
    public Reading(String from, String reading, String date,String time ,@Nullable String suggestion) {
        this.from = from;
        this.reading = reading;
        this.date = date;
        this.time = time;
        this.suggestion = suggestion;
    }

    protected Reading(Parcel in) {
        from = in.readString();
        reading = in.readString();
        date = in.readString();
        suggestion = in.readString();
    }

    public static final Creator<Reading> CREATOR = new Creator<Reading>() {
        @Override
        public Reading createFromParcel(Parcel in) {
            return new Reading(in);
        }

        @Override
        public Reading[] newArray(int size) {
            return new Reading[size];
        }
    };

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Nullable
    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(@Nullable String suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(from);
        dest.writeString(reading);
        dest.writeString(date);
        dest.writeString(suggestion);
    }
}
