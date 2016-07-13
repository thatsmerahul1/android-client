package com.ecarezone.android.patient.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.ecarezone.android.patient.PatientApplication;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.rest.ChangeStatusRequest;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.service.RoboEcareSpiceServices;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by Umesh on 27-06-2016.
 */
public class HeartbeatService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public HeartbeatService() {
        super("HeartbeatService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /**
         * sent a heart beat to the GCM to keep the TCP connection alive
         */
        if (intent.getBooleanExtra(Constants.SEND_HEART_BEAT, false)) {
            sendBroadcast(new Intent(
                    "com.google.android.intent.action.GTALK_HEARTBEAT"));
            sendBroadcast(new Intent(
                    "com.google.android.intent.action.MCS_HEARTBEAT"));
            Log.i("HeartbeatService", "Heartbeat sent to GCM");
        }
        if (intent.getBooleanExtra(Constants.UPDATE_STATUS, false)) {
            PatientApplication patientApplication = (PatientApplication) getApplicationContext();

            int status;
            if (!patientApplication.getNameValuePair().get(Constants.STATUS_CHANGE)) {
                status = 2;
            } else {
                status = 1;
            }
            if (patientApplication.getLastAvailabilityStatus() != status) {
                ChangeStatusRequest request = new ChangeStatusRequest(status, LoginInfo.hashedPassword,
                        LoginInfo.userName, "0");
                getSpiceManager().execute(request, new ChangeStatusRequestListener());
            }
            patientApplication.setLastAvailabilityStatus(status);


            Log.i("HeartbeatService", "status updated");
        }
    }

    public final class ChangeStatusRequestListener implements RequestListener<BaseResponse> {

        private String TAG = "ChangeStatusRequestListener";

        @Override
        public void onRequestFailure(SpiceException spiceException) {
//            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(final BaseResponse baseResponse) {
            Log.d(TAG, "statuschange " + "changed");

//            DoctorApplication.lastAvailablityStaus = status ;
        }
    }

    private SpiceManager spiceManager = new SpiceManager(RoboEcareSpiceServices.class);

    public SpiceManager getSpiceManager() {
        if (!spiceManager.isStarted()) {
            spiceManager.start(this);
        }
        return spiceManager;
    }

}
