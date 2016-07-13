package com.ecarezone.android.patient.model;

import com.google.gson.annotations.Expose;

/**
 * Created by 10603675 on 22-06-2016.
 */
public class AppointmentResponse {

    @Expose
    public int id;
    @Expose
    public String dateTime;
    @Expose
    public String callType;
    @Expose
    public String patientId;
    @Expose
    public String doctorId;
    @Expose
    public String message;

}
