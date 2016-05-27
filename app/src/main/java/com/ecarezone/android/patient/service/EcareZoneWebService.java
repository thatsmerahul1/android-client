package com.ecarezone.android.patient.service;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.utils.ImageUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;

/**
 * Created by jifeng.zhang on 20/06/15.
 */
public class EcareZoneWebService {
    private static RestAdapter restAdapter = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setLog(new AndroidLog(Constants.ECARE_ZONE))
            .setErrorHandler(new RetrofitErrorHandler())
            .setEndpoint(Constants.API_END_POINT).build();
    public static EcareZoneApi api = restAdapter.create(EcareZoneApi.class);


    public static class RetrofitErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError cause) {

            if (cause.isNetworkError()) {
                if(cause.getMessage().contains("authentication")){
                    //401 errors
                    return  new Exception("Invalid credentials. Please verify login info.");
                }else if (cause.getCause() instanceof SocketTimeoutException) {
                    //Socket Timeout
                    return new SocketTimeoutException("Connection Timeout. " +
                            "Please verify your internet connection.");
                } else {
                    //No Connection
                    return new ConnectException("No Connection. " +
                            "Please verify your internet connection.");
                }
            } else {

                return cause;
            }
        }

    }
}
