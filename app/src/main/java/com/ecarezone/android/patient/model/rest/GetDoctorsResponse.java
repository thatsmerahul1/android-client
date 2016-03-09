package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;

import java.util.List;

/**
 * Created by jifeng on 23/06/15.
 */
public class GetDoctorsResponse extends BaseResponse{
    public List<Doctor> data;
}
