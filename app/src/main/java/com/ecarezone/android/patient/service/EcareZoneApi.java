package com.ecarezone.android.patient.service;

import com.ecarezone.android.patient.model.rest.AddDoctorRequest;
import com.ecarezone.android.patient.model.rest.AddDoctorResponse;
import com.ecarezone.android.patient.model.rest.CreateProfileRequest;
import com.ecarezone.android.patient.model.rest.CreateProfileResposnse;
import com.ecarezone.android.patient.model.rest.GetDoctorRequest;
import com.ecarezone.android.patient.model.rest.GetDoctorResponse;
import com.ecarezone.android.patient.model.rest.GetDoctorsRequest;
import com.ecarezone.android.patient.model.rest.GetDoctorsResponse;
import com.ecarezone.android.patient.model.rest.LoginRequest;
import com.ecarezone.android.patient.model.rest.LoginResponse;
import com.ecarezone.android.patient.model.rest.Repo;
import com.ecarezone.android.patient.model.rest.SearchDoctorsRequest;
import com.ecarezone.android.patient.model.rest.SearchDoctorsResponse;
import com.ecarezone.android.patient.model.rest.SignupRequest;
import com.ecarezone.android.patient.model.rest.SignupResponse;
import com.ecarezone.android.patient.model.rest.UpdateProfileRequest;
import com.ecarezone.android.patient.model.rest.UpdateProfileResponse;
import com.ecarezone.android.patient.model.rest.base.BaseRequest;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public interface EcareZoneApi {
    @GET("/users/{user}/repos")
    List<Repo> listRepos(@Path("user") String user);

    @POST("/login")
    LoginResponse login(@Body LoginRequest request);

    @POST("/signup")
    SignupResponse signup(@Body SignupRequest request);

    @POST("/doctors/search")
    SearchDoctorsResponse searchDoctors(@Body SearchDoctorsRequest request);

    @POST("/doctors/{doctorId}")
    GetDoctorResponse getDoctor(@Path("doctorId") Long doctorId ,@Body BaseRequest request);

    @POST("/users/{userId}/doctors")
    GetDoctorsResponse getDoctors(@Path("userId") Long userId ,@Body BaseRequest request);

    @POST("/users/{userId}/doctors/{doctorId}")
    BaseResponse addDoctor(@Path("userId") Long userId, @Path("doctorId") Long doctorId , @Body BaseRequest request);

    @PUT("/users/{userId}/profiles/{profileId}")
    BaseResponse updateProfile(@Path("userId") Long userId, @Path("profileId") Long profileId, @Body UpdateProfileRequest request);

    @POST("/users/{userId}/profiles")
    CreateProfileResposnse createProfile(@Path("userId") Long userId, @Body CreateProfileRequest request);
}
