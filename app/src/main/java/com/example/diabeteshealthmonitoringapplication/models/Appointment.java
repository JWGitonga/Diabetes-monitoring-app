package com.example.diabeteshealthmonitoringapplication.models;

public class Appointment {
    private String uid,date,time,hospital;

    /***
     *  @NoArgsConstructor - No arguments constructor
     */
    public Appointment() {

    }

    /**
     * @AllArgsConstructor
     * @param uid Firebase Assigned id
     * @param date Date the appointment was booked
     * @param time Time the appointment was booked
     * @param hospital Hospital in which the appointment was booked
     */
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
