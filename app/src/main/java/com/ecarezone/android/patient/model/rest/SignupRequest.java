package com.ecarezone.android.patient.model.rest;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public class SignupRequest {
    String email;
    String password;
    Integer role;
    String country;
    String language;
    String latitude;
    String longitude;
    String apiKey;
    String deviceUnique;


    public SignupRequest(String email, String password, Integer role, String country, String language, String latitude, String longitude, String apiKey, String deviceUnique) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.country = country;
        this.language = language;
        this.latitude = latitude;
        this.longitude = longitude;
        this.apiKey = apiKey;
        this.deviceUnique = deviceUnique;
    }
}
