package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by jifeng on 22/06/15.
 */
public class AddDoctorRequest extends RetrofitSpiceRequest<AddDoctorResponse, EcareZoneApi> implements Serializable {

    @Expose
    String email;
    @Expose
    String password;
    @Expose
    String apiKey;
    @Expose
    String deviceUnique;
    @Expose
    Long doctorId;
    @Expose
    String doctorName;

    public AddDoctorRequest(Long doctorId, String doctorName, String email, String password, String apiKey, String deviceUnique) {
        super(AddDoctorResponse.class, EcareZoneApi.class);
        this.email = email;
        this.apiKey = apiKey;
        this.deviceUnique = deviceUnique;
        this.password = password;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
    }

    @Override
    public AddDoctorResponse loadDataFromNetwork() throws Exception {
        return getService().addDoctor(LoginInfo.userId, doctorId, this);
    }
}
