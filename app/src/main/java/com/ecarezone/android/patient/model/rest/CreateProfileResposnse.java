package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.rest.base.BaseResponse;

/**
 * Created by jifeng on 23/06/15.
 */
public class CreateProfileResposnse extends BaseResponse{
    public Data data;

    class Data{
        public String profileId;
    }
}
