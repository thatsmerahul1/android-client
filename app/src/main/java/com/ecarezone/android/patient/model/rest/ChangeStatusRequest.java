package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by 20109804 on 6/15/2016.
 */
public class ChangeStatusRequest extends RetrofitSpiceRequest<BaseResponse, EcareZoneApi> {
    @Expose
    public int status;
    @Expose
    public String password;
    @Expose
    public String email;
    @Expose
    public String role;

    public ChangeStatusRequest(int status, String password, String email, String role) {
        super(BaseResponse.class, EcareZoneApi.class);
        this.status = status;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @Override
    public BaseResponse loadDataFromNetwork() throws Exception {
        return getService().changeStatus(this);
    }
}