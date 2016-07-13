package com.ecarezone.android.patient;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import java.util.HashMap;

/**
 * Created by CHAO WEI on 6/19/2015.
 */
public class PatientApplication extends Application {

    private HashMap<String, Boolean> nameValuePair = new HashMap<String, Boolean>();
    private int lastAvailabilityStatus;

    final String getCallerName() {
        return PatientApplication.class.getSimpleName();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }

    public void setStatusNameValuePair(HashMap<String, Boolean> nameValuePair) {
        this.nameValuePair = nameValuePair;
    }

    public HashMap<String, Boolean> getNameValuePair() {
        return nameValuePair;
    }

    public void setLastAvailabilityStatus(int lastAvailabilityStatus) {
        this.lastAvailabilityStatus = lastAvailabilityStatus;
    }

    public int getLastAvailabilityStatus() {
        return this.lastAvailabilityStatus;
    }

}
