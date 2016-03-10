package com.ecarezone.android.patient.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by L&T Technology services on 2/19/2016.
 */
public class User {
    @DatabaseField
    public String userId;
    @DatabaseField
    public String email;
    @DatabaseField
    public String password;
    @DatabaseField
    public String country;
    @DatabaseField
    public String language;
    @DatabaseField
    public String role;
}