package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.rest.base.BaseRequest;

/**
 * Created by jifeng on 23/06/15.
 */
public class GetDoctorsRequest extends BaseRequest{

    public GetDoctorsRequest(String email, String password, String apiKey, String deviceUnique) {
        super(email, password, apiKey, deviceUnique);
    }
}
