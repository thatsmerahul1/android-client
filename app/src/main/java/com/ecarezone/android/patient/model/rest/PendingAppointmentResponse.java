package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.AppointmentResponse;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Umesh on 07-07-2016.
 */
public class PendingAppointmentResponse implements Serializable {
    @Expose
    public Status status;
    @Expose
    public List<AppointmentResponse> data;

}
