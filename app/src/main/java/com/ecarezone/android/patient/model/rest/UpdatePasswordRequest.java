package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by 20075979 on 3/14/2016.
 */
public class UpdatePasswordRequest extends RetrofitSpiceRequest<BaseResponse, EcareZoneApi> {
    @Expose
    public String currentPassword;
    @Expose
    public String newPassword;
    @Expose
    public String email;
    @Expose
    public String role;
    public UpdatePasswordRequest(String currentPassword, String newPassword,String email,String role) {
        super(BaseResponse.class,EcareZoneApi.class);
        this.currentPassword=currentPassword;
        this.newPassword=newPassword;
        this.email=email;
        this.role=role;
    }

    @Override
    public BaseResponse loadDataFromNetwork() throws Exception {
        return getService().updatePassword(this);
    }
}
