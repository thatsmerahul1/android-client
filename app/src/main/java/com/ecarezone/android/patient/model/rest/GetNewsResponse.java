package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.NewsCategory;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by L&T Technology Services on 2/20/2016.
 */
public class GetNewsResponse implements Serializable {
    @Expose
    public Status status;
    @Expose
    public NewsCategory[] data;
}