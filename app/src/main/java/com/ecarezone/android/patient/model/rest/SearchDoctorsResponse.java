package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.Doctor;

import java.util.List;

/**
 * Created by jifeng.zhang on 20/06/15.
 */
public class SearchDoctorsResponse {
    public Status status;
    public List<Doctor> data;
}