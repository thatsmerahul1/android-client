package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by 10603675 on 22-06-2016.
 */
public class ValidateAppointmentRequest extends RetrofitSpiceRequest<BaseResponse, EcareZoneApi> implements Serializable {

    @Expose
    long appointmentId;
    @Expose
    String email;
    @Expose
    String password;
    @Expose
    String apiKey;
    @Expose
    String deviceUnique;

    public ValidateAppointmentRequest(long appointmentId, String email, String password, String apiKey, String deviceUnique) {
        super(BaseResponse.class, EcareZoneApi.class);
        this.appointmentId = appointmentId;
        this.email = email;
        this.apiKey = apiKey;
        this.deviceUnique = deviceUnique;
        this.password = password;
    }

    @Override
    public BaseResponse loadDataFromNetwork() throws Exception {
        return getService().validateAppointment(appointmentId, this);
    }
}
