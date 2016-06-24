package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.AppointmentResponse;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by 10603675 on 23-06-2016.
 */
public class GetAllAppointmentRequest extends RetrofitSpiceRequest<GetAllAppointmentResponse, EcareZoneApi> implements Serializable {


    private long userId;
    public GetAllAppointmentRequest(long userId) {
        super(GetAllAppointmentResponse.class, EcareZoneApi.class);
        this.userId = userId;
    }

    @Override
    public GetAllAppointmentResponse loadDataFromNetwork() throws Exception {
        return getService().getAllAppointments(userId);
    }
}
