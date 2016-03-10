package com.ecarezone.android.patient.model.rest;

import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import retrofit.mime.TypedFile;

/**
 * Created by L&T Technology Services on 3/9/2016.
 */
public class UploadImageRequest extends RetrofitSpiceRequest<UploadImageResponse, EcareZoneApi> {

    @Expose
    private TypedFile file;

    public UploadImageRequest(TypedFile file) {
        super(UploadImageResponse.class, EcareZoneApi.class);
        this.file = file;
    }

    @Override
    public UploadImageResponse loadDataFromNetwork() throws Exception {
        return getService().upload(file, LoginInfo.userId);
    }
}
