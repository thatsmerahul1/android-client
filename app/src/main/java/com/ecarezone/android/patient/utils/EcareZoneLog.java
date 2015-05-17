package com.ecarezone.android.patient.utils;

import android.util.Log;

/**
 * Created by CHAO WEI on 5/17/2015.
 */
public class EcareZoneLog {

    private EcareZoneLog() {
    }

    public static void e(final String callerName, Throwable t) {
        Log.e(callerName, "Erorr", t);
    }
}
