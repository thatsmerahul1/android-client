package com.ecarezone.android.patient.service;

import com.ecarezone.android.patient.config.Constants;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;

/**
 * Created by jifeng.zhang on 20/06/15.
 */
public class EcareZoneWebService {
    private static RestAdapter restAdapter = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setLog(new AndroidLog(Constants.ECARE_ZONE))
            .setEndpoint(Constants.API_END_POINT).build();
    public static EcareZoneApi api = restAdapter.create(EcareZoneApi.class);
}
