package com.ecarezone.android.patient.model.rest;


import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public class LoginRequest extends RetrofitSpiceRequest<LoginResponse, EcareZoneApi> implements Serializable {
    @Expose
    String email;
    @Expose
    String password;
    @Expose
    Integer role;
    @Expose
    String apiKey;
    @Expose
    String deviceUnique;
    @Expose
    Double latitude;
    @Expose
    Double longitude;

    public LoginRequest(String email, String password, Integer role, String apiKey, String deviceUnique
            , Double latitude, Double longitude) {
        super(LoginResponse.class, EcareZoneApi.class);
        this.email = email;
        this.password = password;
        this.role = role;
        this.apiKey = apiKey;
        this.deviceUnique = deviceUnique;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public LoginResponse loadDataFromNetwork() throws Exception {
        //Current request is using for both login and logout based on condition
        if (password == null) {
            return getService().logout(this);
        }
        return getService().login(this);
    }
}
