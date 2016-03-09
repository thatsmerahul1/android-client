package com.ecarezone.android.patient.service;

import retrofit.RestAdapter;

/**
 * Created by jifeng.zhang on 20/06/15.
 */
public class EcareZoneWebService {

    private static RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint("http://188.166.55.204:9000").build();
    public static EcareZoneApi api = restAdapter.create(EcareZoneApi.class);
}
