package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by jifeng.zhang on 20/06/15.
 */
public class SearchDoctorsRequest extends RetrofitSpiceRequest<SearchDoctorsResponse, EcareZoneApi> implements Serializable {
    @Expose
    String keyword;
    @Expose
    String email;
    @Expose
    String password;
    @Expose
    String apiKey;
    @Expose
    String deviceUnique;
    @Expose
    Long userId;

    public SearchDoctorsRequest(Long userId, String email, String password, String apiKey, String deviceUnique, String keyword) {
        super(SearchDoctorsResponse.class, EcareZoneApi.class);
        this.keyword = keyword;
        this.userId = userId;
        this.email = email;
        this.apiKey = apiKey;
        this.deviceUnique = deviceUnique;
        this.password = password;
    }

    @Override
    public SearchDoctorsResponse loadDataFromNetwork() throws Exception {

        //This file is common for searchDoctor , recommended Doctor and mycareteam doctors
        if (keyword == null) {
            if (userId == null) {
                return getService().getRecommendedDoctors();
            } else {
                return getService().getDoctors(userId);
            }
        } else {
            return getService().searchDoctors(this);
        }
    }

}
