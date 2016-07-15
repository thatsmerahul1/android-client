package com.ecarezone.android.patient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.ecarezone.android.patient.config.Constants;
import com.urbanairship.UAirship;

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

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        if(sharedPreferences.getString(Constants.UA_CHANNEL_NUMBER, null) == null) {

            UAirship.takeOff(this, new UAirship.OnReadyCallback() {

                @Override
                public void onAirshipReady(UAirship uAirship) {
                    uAirship.getPushManager().setUserNotificationsEnabled(true);
                }

            });
        }
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
