package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by jifeng on 23/06/15.
 */
public class CreateProfileResponse extends BaseResponse implements Serializable {
    @Expose
    public String profileName;
    @Expose
    public String email;
    @Expose
    public String height;
    @Expose
    public String name;
    @Expose
    public String address;
    @Expose
    public String weight;
    @Expose
    public String avatarUrl;
    @Expose
    public String profileId;
    @Expose
    public String ethnicity;
    @Expose
    public String gender;
    @Expose
    public String birthdate;

    @Override
    public String toString() {
        return profileName + ":" + email + ":" + height + ":" + name + ":" + address + ":" + weight + ":" + avatarUrl + ":" + profileId + ":" + ethnicity + ":" + gender + ":" + birthdate;
    }
}