package com.ecarezone.android.patient.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.Appointment;
import com.ecarezone.android.patient.model.database.AppointmentDbApi;
import com.ecarezone.android.patient.model.rest.GetAllAppointmentResponse;
import com.ecarezone.android.patient.model.rest.ValidateAppointmentRequest;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * class helps in getting all the approved appointments and populate the database.
 * Specially needed when the user may login using multiple devices.
 */
public class FetchAppointmentService extends IntentService {

    private static final String ACTION_FETCH_APPOINTMENTS = "fetchAppointments";

    private static final String EXTRA_PARAM_DOC_ID = "docId";

    private SpiceManager spiceManager = new SpiceManager(RoboEcareSpiceServices.class);

    public SpiceManager getSpiceManager() {
        if (!spiceManager.isStarted()) {
            spiceManager.start(this);
        }
        return spiceManager;
    }

    public FetchAppointmentService() {
        super("FetchAppointmentService");
    }

    /**
     * Starts this service to fetch all appointments with the given doctor id. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchAppointment(Context context) {
        Intent intent = new Intent(context, FetchAppointmentService.class);
        intent.setAction(ACTION_FETCH_APPOINTMENTS);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_APPOINTMENTS.equals(action)) {
//                handlePendingAppointment();
                handleActionFetchAppointments();
            }
        }
    }

//    private void handlePendingAppointment() {
//
//        FetchPendingAppointmentRequest request =
//                new FetchPendingAppointmentRequest(LoginInfo.userId);
//        getSpiceManager().execute(request, new FetchAppointmentListRequestListener(true));
//    }

    /**
     * Handle action Fetch Appointments in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchAppointments() {

        FetchAllAppointmentRequest request =
                new FetchAllAppointmentRequest(LoginInfo.userId);
        getSpiceManager().execute(request, new FetchAppointmentListRequestListener());

    }

//    private static class FetchPendingAppointmentRequest extends RetrofitSpiceRequest<GetAllAppointmentResponse, EcareZoneApi> implements Serializable {
//
//        private long userId;
//
//        public FetchPendingAppointmentRequest(long userId) {
//            super(AppointmentResponse.class, EcareZoneApi.class);
//            this.userId = userId;
//        }
//
//        @Override
//        public AppointmentResponse loadDataFromNetwork() throws Exception {
//            return getService().getAllAppointments(userId);
//        }
//    }

    private static class FetchAllAppointmentRequest extends RetrofitSpiceRequest<GetAllAppointmentResponse, EcareZoneApi> implements Serializable {

        private long userId;

        public FetchAllAppointmentRequest(long userId) {
            super(GetAllAppointmentResponse.class, EcareZoneApi.class);
            this.userId = userId;
        }

        @Override
        public GetAllAppointmentResponse loadDataFromNetwork() throws Exception {
            return getService().getAllAppointments(userId);
        }
    }


    private class FetchAppointmentListRequestListener implements RequestListener<GetAllAppointmentResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
//          fail silently
            Log.e("FetchAppointmentService", spiceException.getMessage());
        }

        @Override
        public void onRequestSuccess(GetAllAppointmentResponse appointmentResponse) {

            if (appointmentResponse != null) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                ArrayList<Appointment> appointments = (ArrayList<Appointment>) appointmentResponse.data;

                if (appointments != null) {
                    AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
                    ListIterator<Appointment> iter = appointments.listIterator();
                    Appointment appointment = null;
                    while (iter.hasNext()) {
                        appointment = iter.next();
                        try {
                            appointment.setTimeStamp(String.valueOf(dateFormat.parse(appointment.getTimeStamp()).getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (appointmentDbApi.isAppointmentPresent(appointment.getAppointmentId())) {
                            appointmentDbApi.updateAppointment(appointment.getAppointmentId(), appointment);
                        } else {
                            appointmentDbApi.saveAppointment(appointment);
                        }
                    }
                    validateAppointment();
                }

            }
        }
    }

    /****************
     * VALIDATE APPOINTMENT
     ****************/

    private void validateAppointment() {

        AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
        List<Appointment> appointmentList1 = appointmentDbApi.getAllAppointments();
        List<Appointment> appointmentList = appointmentDbApi.getAllPendingAppointments();
        if (appointmentList != null) {
            for (Appointment appointmentIns : appointmentList) {
                try {
                    ValidateAppointmentRequest request =
                            new ValidateAppointmentRequest(appointmentIns.getAppointmentId(), LoginInfo.userName, LoginInfo.hashedPassword, Constants.API_KEY, Constants.deviceUnique);
                    getSpiceManager().execute(request, new ValidateTaskRequestListener(appointmentIns.getAppointmentId()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private class ValidateTaskRequestListener implements RequestListener<com.ecarezone.android.patient.model.rest.base.BaseResponse> {

        long appointmentId;

        public ValidateTaskRequestListener(long appointmentId) {
            this.appointmentId = appointmentId;
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

        @Override
        public void onRequestSuccess(BaseResponse baseResponse) {

            if (baseResponse != null) {
                if (baseResponse.toString() != null) {
                    AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
                    if (baseResponse.toString().contains("Appointment is accepted ")) {
//                        appointment has been accepted
                        appointmentDbApi.updateAppointmentStatus(appointmentId, true);
                    } else {
//                        appointment not accepted
                        appointmentDbApi.updateAppointmentStatus(appointmentId, false);
                    }
                }
            }
        }
    }
}
