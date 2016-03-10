package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.rest.Status;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public class LoginResponse implements Serializable {
    @Expose
    public Status status;
    @Expose
    public Data data;
}
