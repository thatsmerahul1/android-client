package com.ecarezone.android.patient;

import android.app.Application;

/**
 * Created by CHAO WEI on 6/19/2015.
 */
public class PatientApplication extends Application {

    final String getCallerName() {
        return  PatientApplication.class.getSimpleName();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
