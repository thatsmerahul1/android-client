package com.ecarezone.android.patient.model.rest;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public class LoginRequest {
    String email;
    String password;
    Integer role;
    String apiKey;
    String deviceUnique;

    public LoginRequest(String email, String password, Integer role, String apiKey, String deviceUnique) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.apiKey = apiKey;
        this.deviceUnique = deviceUnique;
    }
}
