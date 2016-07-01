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
public class Appointment implements Serializable, Comparable<Appointment> {

    @Expose
    @DatabaseField(canBeNull = false)
    private long id;

    @Expose
    @DatabaseField(canBeNull = true)
    private String callType;

    @Expose
    @DatabaseField(canBeNull = true)
    private String doctorId;

    @Expose
    @DatabaseField(canBeNull = true)
    private String patientId;

    @Expose
    @DatabaseField(canBeNull = true)
    private String dateTime;

    @Expose
    @DatabaseField(canBeNull = true)
    private int isConfirmed;


    private boolean isAppointmentPresent;
    private long dateTimeInLong = 0;

    public Appointment() {

    }

    public boolean isAppointmentPresent() {
        return isAppointmentPresent;
    }

    public void setAppointmentPresent(boolean isAppointmentPresent) {
        this.isAppointmentPresent = isAppointmentPresent;
    }

    public String getTimeStamp() {
        return dateTime;
    }

    public void setTimeStamp(String timeStamp) {
        this.dateTime = timeStamp;
    }

    public long getAppointmentId() {
        return id;
    }

    public void setAppointmentId(long appointmentId) {
        this.id = appointmentId;
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

    public String getpatientId() {
        return patientId;
    }

    public void setpatientId(String patientId) {
        this.patientId = patientId;
    }

    public int isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(int isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public long getDateTimeInLong() {
        return dateTimeInLong;
    }

    public void setDateTimeInLong(long dateTimeInLong) {
        this.dateTimeInLong = dateTimeInLong;
    }

    @Override
    public int compareTo(Appointment another) {
        if (dateTimeInLong < another.dateTimeInLong) {
            return -1;
        } else if (dateTimeInLong == another.dateTimeInLong) {
            return 0;
        } else {
            return 1;
        }

    }
}
