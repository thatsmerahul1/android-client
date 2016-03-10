package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public class SignupRequest extends RetrofitSpiceRequest<LoginResponse, EcareZoneApi> implements Serializable {
    @Expose
    String email;
    @Expose
    String password;
    @Expose
    Integer role;
    @Expose
    String country;
    @Expose
    String language;
    @Expose
    String latitude;
    @Expose
    String longitude;
    @Expose
    String apiKey;
    @Expose
    String deviceUnique;


    public SignupRequest(String email, String password, Integer role, String country, String language, String latitude, String longitude, String apiKey, String deviceUnique) {
        super(LoginResponse.class, EcareZoneApi.class);
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

    @Override
    public LoginResponse loadDataFromNetwork() throws Exception {
        return getService().signup(this);
    }
}
