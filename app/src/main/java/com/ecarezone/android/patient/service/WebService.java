package com.ecarezone.android.patient.service;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONObject;


/**
 * Created by CHAO WEI on 5/17/2015.
 */
public class WebService {

    public String getCallerName() {
        return  WebService.class.getSimpleName();
    }

    private static final String UTF_8 =  "UTF-8";

    /**
     * WebService singleton instance
     */
    private static WebService sInstance;
    private Context mContext = null;
    private Resources mResources = null;

    /**
     * Get the {@link WebService} instance.
     *
     * @param context
     *            Application context.
     *
     * @return {@link WebService} instance.
     */
    public static WebService getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WebService(context);
        }
        return sInstance;
    }

    private WebService(Context context) {
        mContext = context;
        mResources = context.getResources();
    }


    public JSONObject register() {

        return  null;
    }

    public JSONObject login() {

        return null;
    }

    public JSONObject lgout() {

        return  null;
    }

}
