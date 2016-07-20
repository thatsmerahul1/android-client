package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by 20109804 on 6/13/2016.
 */
public class GetDoctorRequest extends RetrofitSpiceRequest<GetDoctorResponse, EcareZoneApi> implements Serializable {
    int doctorId;


    public GetDoctorRequest(int doctorId) {
        super(GetDoctorResponse.class, EcareZoneApi.class);
        this.doctorId = doctorId;
    }  public GetDoctorRequest() {
        super(GetDoctorResponse.class, EcareZoneApi.class);
     }

    @Override
    public GetDoctorResponse loadDataFromNetwork() throws Exception {
        return getService().getRecDoctor(doctorId);
    }
}
