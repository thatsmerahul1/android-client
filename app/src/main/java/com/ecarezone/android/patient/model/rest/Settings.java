package com.ecarezone.android.patient.model.rest;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by L&T Technology Services  on 2/19/2016.
 */
public class Settings implements Serializable {
    @Expose
    public String email;
    @Expose
    public String country;
    @Expose
    public String language;
}
