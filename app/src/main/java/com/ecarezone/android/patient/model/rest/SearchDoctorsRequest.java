package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.model.rest.base.BaseRequest;

/**
 * Created by jifeng.zhang on 20/06/15.
 */
public class SearchDoctorsRequest extends BaseRequest{
    String keyword;


    public SearchDoctorsRequest(String email, String password, String apiKey, String deviceUnique) {
        super(email, password, apiKey, deviceUnique);
    }

    public SearchDoctorsRequest(String email, String password, String apiKey, String deviceUnique, String keyword) {
        super(email, password, apiKey, deviceUnique);
        this.keyword = keyword;
    }
}
