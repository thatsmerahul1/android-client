package com.ecarezone.android.patient.model.rest.base;

import com.ecarezone.android.patient.model.rest.Status;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by jifeng.zhang on 27/06/15.
 */
public class BaseResponse implements Serializable{
    @Expose
    public Status status;

    @Override
    public String toString() {
        return status.code+" "+status.message;
    }
}
