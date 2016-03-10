package com.ecarezone.android.patient.model.rest;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by jifeng on 22/06/15.
 */
public class AddDoctorResponse implements Serializable {
    @Expose
    public Status status;
}
