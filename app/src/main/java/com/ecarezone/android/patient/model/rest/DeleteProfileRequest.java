package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by L&T Technology Services on 2/22/2016.
 */
public class DeleteProfileRequest extends RetrofitSpiceRequest<BaseResponse, EcareZoneApi> implements Serializable {
    @Expose
    String email;
    @Expose
    String password;
    @Expose
    String apiKey;
    @Expose
    String deviceUnique;

    long profileId;

    public DeleteProfileRequest(long profileId) {
        super(BaseResponse.class, EcareZoneApi.class);

        this.email = LoginInfo.userName;
        this.password = LoginInfo.hashedPassword;
        this.apiKey = Constants.API_KEY;
        this.deviceUnique = Constants.deviceUnique;

        this.profileId = profileId;
    }

    @Override
    public BaseResponse loadDataFromNetwork() throws Exception {
        return getService().deleteProfile(LoginInfo.userId, profileId, this);
    }
}
