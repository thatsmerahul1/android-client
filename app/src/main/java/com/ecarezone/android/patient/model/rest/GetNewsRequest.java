package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.service.EcareZoneApi;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by L&T Technology Services on 3/7/2016.
 */
public class GetNewsRequest extends RetrofitSpiceRequest<GetNewsResponse, EcareZoneApi> {

    public GetNewsRequest() {
        super(GetNewsResponse.class, EcareZoneApi.class);
    }

    @Override
    public GetNewsResponse loadDataFromNetwork() throws Exception {
        //TODO, once patient-clients has news info, remove passing static userId, and enable variable userId call.
        //return getService().getNews(LoginInfo.userId);
        return getService().getNews(Long.parseLong("26"));
    }
}