package com.ecarezone.android.patient.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by jifeng.zhang on 20/06/15.
 */
public class Doctor implements Parcelable, Serializable {
    @Expose
    @DatabaseField
    public Long doctorId;
    @Expose
    @DatabaseField
    public String emailId;
    @Expose
    @DatabaseField
    public String name;
    @Expose
    @DatabaseField
    public String doctorDescription;
    @Expose
    @DatabaseField
    public String status;
    @Expose
    @DatabaseField
    public String doctorCategory;
    @Expose
    public String category;
    @Expose
    public String doctorGender;
    @Expose
    public String doctorCountry;
    @Expose
    public String doctorLanguage;
    @Expose
    @DatabaseField
    public String avatarUrl;
    @Expose
    @DatabaseField
    public boolean requestPending;

    public Doctor(){

    }
    public Doctor(Long doctorId, String emailId, String avatarUrl,  String name, String doctorDescription, String status, String doctorCategory, String doctorGender, String doctorCountry, String doctorLanguage) {
        this.doctorId = doctorId;
        this.emailId = emailId;
        this.name = name;
        this.doctorDescription = doctorDescription;
        this.status = status;
        this.doctorCategory = doctorCategory;
        this.doctorGender = doctorGender;
        this.doctorCountry = doctorCountry;
        this.doctorLanguage = doctorLanguage;
        this.avatarUrl = avatarUrl;
    }

    public Doctor(Parcel in) {
        this.doctorId = in.readLong();
        this.emailId = in.readString();
        this.name = in.readString();
        this.doctorDescription = in.readString();
        this.status = in.readString();
        this.doctorCategory = in.readString();
        this.category = in.readString();
        this.doctorGender = in.readString();
        this.doctorCountry = in.readString();
        this.doctorLanguage = in.readString();
        this.avatarUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(doctorId);
        dest.writeString(emailId);
        dest.writeString(name);
        dest.writeString(doctorDescription);
        dest.writeString(status);
        dest.writeString(doctorCategory);
        dest.writeString(category);
        dest.writeString(doctorGender);
        dest.writeString(doctorCountry);
        dest.writeString(doctorLanguage);
        dest.writeString(avatarUrl);
    }

    public static Parcelable.Creator<Doctor> CREATOR = new Parcelable.Creator<Doctor>() {

        @Override
        public Doctor createFromParcel(Parcel source) {
            return new Doctor(source);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };
}
