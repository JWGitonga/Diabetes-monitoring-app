package com.example.diabeteshealthmonitoringapplication.activities;

public class Appointment {
    private String uid,date,time,hospital;

    public Appointment(String uid, String date, String time, String hospital) {
        this.uid = uid;
        this.date = date;
        this.time = time;
        this.hospital = hospital;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
}
