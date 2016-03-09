package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.UserProfile;
import com.ecarezone.android.patient.model.rest.base.BaseRequest;

/**
 * Created by jifeng on 23/06/15.
 */
public class UpdateProfileRequest extends BaseRequest{
    //base64
    public String avatar;
    public UserProfile userProfile;

    public UpdateProfileRequest(String email, String password, String apiKey, String deviceUnique) {
        super(email, password, apiKey, deviceUnique);
    }
}
