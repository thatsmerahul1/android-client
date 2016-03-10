package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.UserProfile;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by jifeng on 23/06/15.
 */
public class CreateProfileRequest extends RetrofitSpiceRequest<CreateProfileResponse, EcareZoneApi> {
    @Expose
    public UserProfile userProfile;
    @Expose
    public String email;
    @Expose
    public String password;
    @Expose
    public String apiKey;
    @Expose
    public String deviceUnique;

    public CreateProfileRequest() {
        super(CreateProfileResponse.class, EcareZoneApi.class);

        this.email = LoginInfo.userName;
        this.password = LoginInfo.hashedPassword;
        this.apiKey = Constants.API_KEY;
        this.deviceUnique = Constants.deviceUnique;
    }

    @Override
    public CreateProfileResponse loadDataFromNetwork() throws Exception {
        return getService().createProfile(LoginInfo.userId, this);
    }
}
