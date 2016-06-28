package com.ecarezone.android.patient.service;

import com.ecarezone.android.patient.model.AppointmentResponse;
import com.ecarezone.android.patient.model.rest.AddDoctorRequest;
import com.ecarezone.android.patient.model.rest.AddDoctorResponse;
import com.ecarezone.android.patient.model.rest.BookAppointmentRequest;
import com.ecarezone.android.patient.model.rest.BookAppointmentResponse;
import com.ecarezone.android.patient.model.rest.ChangeStatusRequest;
import com.ecarezone.android.patient.model.rest.CreateProfileRequest;
import com.ecarezone.android.patient.model.rest.CreateProfileResponse;
import com.ecarezone.android.patient.model.rest.DeleteAppointmentRequest;
import com.ecarezone.android.patient.model.rest.DeleteProfileRequest;
import com.ecarezone.android.patient.model.rest.ForgetPassRequest;
import com.ecarezone.android.patient.model.rest.GetAllAppointmentResponse;
import com.ecarezone.android.patient.model.rest.GetDoctorResponse;
import com.ecarezone.android.patient.model.rest.GetNewsResponse;
import com.ecarezone.android.patient.model.rest.LoginRequest;
import com.ecarezone.android.patient.model.rest.LoginResponse;
import com.ecarezone.android.patient.model.rest.Repo;
import com.ecarezone.android.patient.model.rest.SearchDoctorsRequest;
import com.ecarezone.android.patient.model.rest.SearchDoctorsResponse;
import com.ecarezone.android.patient.model.rest.SettingsRequest;
import com.ecarezone.android.patient.model.rest.SignupRequest;
import com.ecarezone.android.patient.model.rest.UpdatePasswordRequest;
import com.ecarezone.android.patient.model.rest.UpdateProfileRequest;
import com.ecarezone.android.patient.model.rest.UploadImageResponse;
import com.ecarezone.android.patient.model.rest.ValidateAppointmentRequest;
import com.ecarezone.android.patient.model.rest.base.BaseRequest;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.RestMethod;
import retrofit.mime.TypedFile;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public interface EcareZoneApi {
    @GET("/users/{user}/repos")
    List<Repo> listRepos(@Path("user") String user);

    @POST("/login")
    LoginResponse login(@Body LoginRequest request);

    @POST("/logout")
    LoginResponse logout(@Body LoginRequest request);

    @POST("/forgot-password")
    LoginResponse forgetPassword(@Body ForgetPassRequest request);

    @POST("/changePassword")
    BaseResponse updatePassword(@Body UpdatePasswordRequest request);

    @POST("/signup")
    LoginResponse signup(@Body SignupRequest request);

    @PUT("/users/{userid}/settings")
    LoginResponse settingsUpdate(@Path("userid") Long userId, @Body SettingsRequest request);

    @POST("/doctors/search")
    SearchDoctorsResponse searchDoctors(@Body SearchDoctorsRequest request);

    @GET("/doctors/{doctorid}")
    GetDoctorResponse getRecDoctor(@Path("doctorid") int doctorid);

    @GET("/users/{userId}/doctors")
    SearchDoctorsResponse getDoctors(@Path("userId") Long userId);

    @POST("/users/{userId}/doctors/{doctorId}")
    AddDoctorResponse addDoctor(@Path("userId") Long userId, @Path("doctorId") Long doctorId, @Body AddDoctorRequest request);

    @PUT("/users/{userId}/profiles/{profileId}")
    CreateProfileResponse updateProfile(@Path("userId") Long userId, @Path("profileId") Long profileId, @Body UpdateProfileRequest request);

    @POST("/users/{userId}/profiles")
    CreateProfileResponse createProfile(@Path("userId") Long userId, @Body CreateProfileRequest request);

    @DELETE("/users/{userId}/profiles/{profileId}")
    BaseResponse deleteProfile(@Path("userId") Long userId, @Path("profileId") Long profileId, @Body DeleteProfileRequest request);

    @GET("/users/{userId}/news")
    GetNewsResponse getNews(@Path("userId") Long userId);

    @Multipart
    @POST("/users/{userId}/profilespic")
    UploadImageResponse upload(@Part("profilepic") TypedFile file,
                               @Path("userId") Long userId);

    @GET("/users/recommendedDoctors")
    SearchDoctorsResponse getRecommendedDoctors();

    @POST("/bookappointment/users/{userId}/doctors/{doctorId}")
    BookAppointmentResponse bookAppointment(@Path("userId") Long userId,
                                 @Path("doctorId") Long doctorId, @Body BookAppointmentRequest request);

    @POST("/validateappointment/users/{appointmentId}")
    BaseResponse validateAppointment(@Path("appointmentId") long appointmentId, @Body ValidateAppointmentRequest request);

    @GET("/appointments/users/{userId}")
    GetAllAppointmentResponse getAllAppointments(@Path("userId") long userId);

    @DELETE("/deleteappointment/users/{appointmentId}")
    BaseResponse deleteAppointment(@Path("appointmentId") long appointmentId, @Body DeleteAppointmentRequest request);

    @POST("/changeStatus")
    BaseResponse changeStatus(@Body ChangeStatusRequest request);
}

/**
 * Retrofit DELETE request doesn't allow body in its request,
 * created this custom DELETE interface to accept body content.
 * TODO Need to delete this once the server side modifications are done.
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
@RestMethod(value = "DELETE", hasBody = true)
@interface DELETE {
    String value();
}
