package com.ecarezone.android.patient.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by L&T Technology Services on 2/26/2016.
 */
public class News implements Parcelable, Serializable {
    @Expose
    public String newsTitle;
    @Expose
    public String newsAbstract;
    @Expose
    public String newsLink;

    protected News(Parcel in) {
        newsTitle = in.readString();
        newsAbstract = in.readString();
        newsLink = in.readString();
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(newsTitle);
        dest.writeString(newsAbstract);
        dest.writeString(newsLink);
    }
}