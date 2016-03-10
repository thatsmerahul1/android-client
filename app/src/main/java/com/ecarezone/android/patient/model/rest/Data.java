package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.UserProfile;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by L&T Technology Services  on 2/19/2016.
 */
public class Data implements Serializable {
    @Expose
    public Long userId;
    @Expose
    public Integer recommandedDoctorId;
    @Expose
    public Settings settings;
    @Expose
    public UserProfile[] userProfiles;
    @Expose
    public int role;
    @Expose
    public int status;
}