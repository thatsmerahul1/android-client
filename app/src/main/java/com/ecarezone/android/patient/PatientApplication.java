package com.ecarezone.android.patient;

import android.app.Application;

import java.util.HashMap;

/**
 * Created by CHAO WEI on 6/19/2015.
 */
public class PatientApplication extends Application {
    public static HashMap<String,Boolean> nameValuePair = new HashMap<String, Boolean>();
    public static int lastAvailablityStaus;
    final String getCallerName() {
        return  PatientApplication.class.getSimpleName();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    public static HashMap<String, Boolean> getNameValuePair() {
        return nameValuePair;
    }

}
