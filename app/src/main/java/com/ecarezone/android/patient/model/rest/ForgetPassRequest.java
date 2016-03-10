package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by L&T Technology Services  on 2/19/2016.
 */
public class ForgetPassRequest extends RetrofitSpiceRequest<LoginResponse, EcareZoneApi> implements Serializable {
    @Expose
    String email;
    @Expose
    Integer role;

    public ForgetPassRequest(String email, Integer role) {
        super(LoginResponse.class, EcareZoneApi.class);
        this.email = email;
        this.role = role;

    }

    @Override
    public LoginResponse loadDataFromNetwork() throws Exception {
        return getService().forgetPassword(this);
    }
}
