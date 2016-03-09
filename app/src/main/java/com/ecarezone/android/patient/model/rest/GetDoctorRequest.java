package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.rest.base.BaseRequest;

/**
 * Created by jifeng on 22/06/15.
 */
public class GetDoctorRequest extends BaseRequest{
    public GetDoctorRequest(String email, String password, String apiKey, String deviceUnique) {
        super(email, password, apiKey, deviceUnique);
    }
}
