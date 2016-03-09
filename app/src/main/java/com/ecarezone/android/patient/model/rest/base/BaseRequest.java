package com.ecarezone.android.patient.model.rest.base;

/**
 * Created by jifeng.zhang on 27/06/15.
 */
public class BaseRequest {
    String email;
    String password;
    String apiKey;
    String deviceUnique;

    public BaseRequest(String email, String password, String apiKey, String deviceUnique) {
        this.email = email;
        this.password = password;
        this.apiKey = apiKey;
        this.deviceUnique = deviceUnique;
    }
}
