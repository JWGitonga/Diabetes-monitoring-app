package com.example.diabeteshealthmonitoringapplication;

import androidx.annotation.Nullable;

public class Reading {
    private String from,reading,date;
    @Nullable
    private String suggestion;

    public Reading(String from, String reading, String date, @Nullable String suggestion) {
        this.from = from;
        this.reading = reading;
        this.date = date;
        this.suggestion = suggestion;
    }

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
}
