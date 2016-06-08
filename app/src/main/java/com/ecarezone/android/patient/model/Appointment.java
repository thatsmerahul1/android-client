package com.ecarezone.android.patient.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 10603675 on 25-05-2016.
 */
public class Appointment implements Serializable {

    @Expose
    @DatabaseField(canBeNull = false)
    private String appointmentId;

    @Expose
    @DatabaseField(canBeNull = true)
    private String callType;

    @Expose
    @DatabaseField(canBeNull = true)
    private String doctorId;

    @Expose
    @DatabaseField(canBeNull = true)
    private String userId;

    @Expose
    @DatabaseField(canBeNull = true, dataType = DataType.DATE_STRING,
            format = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    @Expose
    @DatabaseField(canBeNull = true, dataType = DataType.BOOLEAN)
    private boolean isConfirmed;


    private boolean isAppointmentPresent;


    public Appointment() {

    }

    public boolean isAppointmentPresent() {
        return isAppointmentPresent;
    }

    public void setAppointmentPresent(boolean isAppointmentPresent) {
        this.isAppointmentPresent = isAppointmentPresent;
    }

    public Date getTimeStamp() {
        return dateTime;
    }

    public void setTimeStamp(Date timeStamp) {
        this.dateTime = timeStamp;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getUserIdId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String isConfirmed() {
        return isConfirmed();
    }

    public void setConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

}
