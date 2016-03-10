package com.ecarezone.android.patient.service;

import com.ecarezone.android.patient.config.Constants;
import com.google.gson.GsonBuilder;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;


/**
 * Created by L&T Technology Services on 3/2/2016.
 */
public class RoboEcareSpiceServices extends RetrofitGsonSpiceService {

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(EcareZoneApi.class);
    }

    @Override
    protected String getServerUrl() {
        return Constants.API_END_POINT;
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Content-Type", "application/json");
                    }
                })
                .setLog(new AndroidLog(Constants.ECARE_ZONE))
                .setConverter(new GsonConverter(new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation().create()))
                .setEndpoint(getServerUrl());

        return builder;
    }


}

