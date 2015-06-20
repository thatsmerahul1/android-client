package com.ecarezone.android.patient;

import android.app.Application;
import android.util.Log;

import com.ecarezone.android.patient.service.WebService;

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

        WebService.getInstance(getApplicationContext()).configQuickblox();
    }
}
