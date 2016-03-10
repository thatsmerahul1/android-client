package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by L&T Technology Services on 3/1/2016.
 */
public class UploadImageResponse extends BaseResponse implements Serializable {
    @Expose
    public Data data;

    public class Data implements Serializable {
        public String avatarUrl;
    }
}
