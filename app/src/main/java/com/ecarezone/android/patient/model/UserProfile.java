package com.ecarezone.android.patient.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by jifeng.zhang on 27/06/15.
 */
public class UserProfile implements Serializable {
    @Expose
    @DatabaseField(canBeNull = false)
    public String userId;
    @Expose
    @DatabaseField
    public String profileName;
    @Expose
    @DatabaseField
    public String email;
    @Expose
    @DatabaseField
    public String height;
    @Expose
    @DatabaseField
    public String name;
    @Expose
    @DatabaseField
    public String address;
    @Expose
    @DatabaseField
    public String weight;
    @Expose
    @DatabaseField
    public String avatarUrl;
    @Expose
    @DatabaseField(canBeNull = false)
    public String profileId;
    @Expose
    @DatabaseField
    public String ethnicity;
    @Expose
    @DatabaseField
    public String gender;
    @Expose
    @DatabaseField
    public String birthdate;
    @DatabaseField
    public boolean isComplete;
    @DatabaseField
    public boolean isMyProfile;

    public UserProfile() {
    }

    public String toString() {
        return profileName + " " + email + " " + height + " " + name + " " + address + " " + weight + " " + avatarUrl + " " + profileId + " " + ethnicity + " " + gender + " " + birthdate + " " + isComplete;
    }
}
