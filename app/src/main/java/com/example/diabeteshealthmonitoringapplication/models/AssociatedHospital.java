package com.example.diabeteshealthmonitoringapplication.models;

public class AssociatedHospital {
    private String uid,name,hospital;

    public AssociatedHospital() {
    }

    public AssociatedHospital(String uid, String name, String hospital) {
        this.uid = uid;
        this.name = name;
        this.hospital = hospital;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
}
