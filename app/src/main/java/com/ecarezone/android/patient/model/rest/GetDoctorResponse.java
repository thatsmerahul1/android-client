package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.Doctor;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by jifeng on 22/06/15.
 */
public class GetDoctorResponse implements Serializable {
    @Expose
    public Status status;
    @Expose
    public Doctor data;

}
