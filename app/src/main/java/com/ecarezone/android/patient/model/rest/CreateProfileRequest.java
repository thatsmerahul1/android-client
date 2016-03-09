package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.UserProfile;
import com.ecarezone.android.patient.model.rest.base.BaseRequest;

/**
 * Created by jifeng on 23/06/15.
 */
public class CreateProfileRequest extends BaseRequest {
    public UserProfile userProfile;

    public CreateProfileRequest(String email, String password, String apiKey, String deviceUnique) {
        super(email, password, apiKey, deviceUnique);
    }
}
