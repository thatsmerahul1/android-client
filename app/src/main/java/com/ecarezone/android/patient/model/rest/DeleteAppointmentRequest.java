package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by 10603675 on 24-06-2016.
 */
public class DeleteAppointmentRequest extends RetrofitSpiceRequest<BaseResponse, EcareZoneApi> implements Serializable {

    @Expose
    String email;
    @Expose
    String password;
    @Expose
    String apiKey;
    @Expose
    String deviceUnique;
//    @Expose
//    String dateTime;
//    @Expose
//    String callType;
    long appointmentId;

    public DeleteAppointmentRequest(String email, String password, String apiKey,
                                  String deviceUnique, String dateTime,
                                  String callType, long appointmentId) {
        super(BaseResponse.class, EcareZoneApi.class);
        this.email = email;
        this.apiKey = apiKey;
        this.deviceUnique = deviceUnique;
        this.password = password;
//        this.dateTime = dateTime;
//        this.callType = callType;
        this.appointmentId = appointmentId;
    }

    @Override
    public BaseResponse loadDataFromNetwork() throws Exception {
        return getService().deleteAppointment(appointmentId, this);
    }
}
