package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.AppointmentResponse;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by 10603675 on 22-06-2016.
 */
public class BookAppointmentResponse implements Serializable {
    @Expose
    public Status status;
    @Expose
    public AppointmentResponse data;


    @Override
    public String toString() {
        return data.id
                + ":" + data.callType + ":" + data.dateTime + ":"
                + data.message + ":" + data.patientId + ":" + data.doctorId;
    }
}
