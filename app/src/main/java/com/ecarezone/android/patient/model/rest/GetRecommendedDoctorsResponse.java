package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;

import java.util.List;

/**
 * Created by L&T Technology Services on 01-03-2016.
 */
public class GetRecommendedDoctorsResponse extends BaseResponse {

    public List<Doctor> data;
}
