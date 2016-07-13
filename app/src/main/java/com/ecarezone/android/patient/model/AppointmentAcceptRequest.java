package com.ecarezone.android.patient.model;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by Umesh on 15-06-2016.
 */
public class AppointmentAcceptRequest extends RetrofitSpiceRequest<BaseResponse, EcareZoneApi> implements Serializable {

    @Expose
    private String email;
    @Expose
    private String password;
    @Expose
    private String apiKey;
    @Expose
    private String deviceUnique;
    @Expose
    private String dateTime;
    @Expose
    private String callType;

    private long appointmentId;

    public AppointmentAcceptRequest(long appointmentId) {
        super(BaseResponse.class, EcareZoneApi.class);
        this.appointmentId = appointmentId;

        this.email = LoginInfo.userName;
        this.password = LoginInfo.hashedPassword;
        this.apiKey = Constants.API_KEY;
        this.deviceUnique = Constants.deviceUnique;

    }

    @Override
    public BaseResponse loadDataFromNetwork() throws Exception {

        return getService().acceptAppointmentRequest(appointmentId, this);

    }
}