package com.ecarezone.android.patient.model;

/**
 * Created by jifeng.zhang on 20/06/15.
 */
public class Doctor {
    public String email;
    public String name;
    public String doctorDescription;
    public String status;
    public String doctorCategory;
    public String doctorGender;
    public String doctorCountry;
    public String doctorLanguage;

    public Doctor(String email, String name, String doctorDescription, String status, String doctorCategory, String doctorGender, String doctorCountry, String doctorLanguage) {
        this.email = email;
        this.name = name;
        this.doctorDescription = doctorDescription;
        this.status = status;
        this.doctorCategory = doctorCategory;
        this.doctorGender = doctorGender;
        this.doctorCountry = doctorCountry;
        this.doctorLanguage = doctorLanguage;
    }
}
