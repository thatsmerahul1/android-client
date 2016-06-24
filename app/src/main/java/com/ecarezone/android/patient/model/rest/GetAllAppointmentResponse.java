package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.Appointment;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 10603675 on 23-06-2016.
 */
public class GetAllAppointmentResponse implements Serializable {

    @Expose
    public Status status;
    @Expose
    public List<Appointment> data;

}
