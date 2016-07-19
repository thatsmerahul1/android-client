package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by 20109804 on 6/15/2016.
 */
public class ChangeStatusRequest extends RetrofitSpiceRequest<String, EcareZoneApi> {

    @Expose
    public String email;
    @Expose
    public String password;
    @Expose
    public String name;
    @Expose
    public String role;
    @Expose
    public int status;
    @Expose
    public String deviceUnique;

    public ChangeStatusRequest(String email, String password, String name, String role, int status, String deviceUnique) {
        super(String.class, EcareZoneApi.class);

// {"email":"uapatient1@gmail.com", "password":"wkkdl/bt34SeumhQNMNlzQ==",
// "name":"name", "role": "1","status":"0","deviceUnique":"b5d4c425-a305-4363-8d6a-f3fb65635abf"}

        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.status = status;
        this.deviceUnique = deviceUnique;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        return getService().changeStatus(LoginInfo.userId, this);
    }
}