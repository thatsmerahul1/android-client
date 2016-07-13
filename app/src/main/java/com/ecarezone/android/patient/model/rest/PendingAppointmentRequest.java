package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by Umesh on 07-07-2016.
 */
public class PendingAppointmentRequest extends RetrofitSpiceRequest<PendingAppointmentResponse, EcareZoneApi> implements Serializable {

    @Expose
    private long userId;

    public PendingAppointmentRequest(long userId) {
        super(PendingAppointmentResponse.class, EcareZoneApi.class);
        this.userId = userId;
    }


    @Override
    public PendingAppointmentResponse loadDataFromNetwork() throws Exception {
        return getService().getPendingAppointmentList(userId);
    }
}
