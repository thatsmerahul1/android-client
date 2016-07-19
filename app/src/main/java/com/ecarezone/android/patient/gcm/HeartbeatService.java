package com.ecarezone.android.patient.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.ecarezone.android.patient.PatientApplication;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.UserProfile;
import com.ecarezone.android.patient.model.database.ProfileDbApi;
import com.ecarezone.android.patient.service.RoboEcareSpiceServices;
import com.ecarezone.android.patient.utils.Util;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import ch.boye.httpclientandroidlib.client.ClientProtocolException;

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
            if(patientApplication.getNameValuePair().containsKey(Constants.STATUS_CHANGE)) {
                if (!patientApplication.getNameValuePair().get(Constants.STATUS_CHANGE)) {
                    status = Constants.IDLE;
                } else {
                    status = Constants.ONLINE;
                }
                ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
                int profileId = profileDbApi.getProfileIdUsingEmail(LoginInfo.userName);
                UserProfile userProfile = profileDbApi.getProfile(LoginInfo.userId.toString(), String.valueOf(profileId));

                if (patientApplication.getLastAvailabilityStatus() != status) {
//                    ChangeStatusRequest request = new ChangeStatusRequest(LoginInfo.userName,
//                            LoginInfo.hashedPassword, userProfile.name, Constants.USER_ROLE,
//                            status, Constants.deviceUnique);
//                    getSpiceManager().execute(request, new ChangeStatusRequestListener());
                    ChangeStatusRequest changeStatusService = new ChangeStatusRequest(status);
                }
            }
            else{
                patientApplication.getNameValuePair().put(Constants.STATUS_CHANGE, false);
                status = Constants.ONLINE;
            }
            patientApplication.setLastAvailabilityStatus(status);


            Log.i("HeartbeatService", "status updated");
        }
    }


    private class ChangeStatusRequest {

        private int appointmentId;

// {"email":"uapatient1@gmail.com", "password":"wkkdl/bt34SeumhQNMNlzQ==",
// "name":"name", "role": "1","status":"0","deviceUnique":"b5d4c425-a305-4363-8d6a-f3fb65635abf"}

        public ChangeStatusRequest(int status) {

            ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
            int profileId = profileDbApi.getProfileIdUsingEmail(LoginInfo.userName);
            UserProfile userProfile = profileDbApi.getProfile(LoginInfo.userId.toString(), String.valueOf(profileId));

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("email", LoginInfo.userName);
                jsonObj.put("password", LoginInfo.hashedPassword);
                jsonObj.put("name", userProfile.profileName);
                jsonObj.put("role", Constants.USER_ROLE);
                jsonObj.put("status", status);
                jsonObj.put("deviceUnique", Constants.deviceUnique);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            startHttpRequest(jsonObj.toString());
        }

        protected void startHttpRequest(String body) {

            String response = null;
            String line = "";
            URL url;
            HttpURLConnection urlConnection = null;
            BufferedReader rd;

            try {
                url = new URL("http://188.166.55.204:8080/ECZ/notification/pushstatus/" + LoginInfo.userId);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(out, body);

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                response = Util.readDataFromInputStream(in);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            if (response != null && response.equalsIgnoreCase("Notification Sent")) {
                Log.i("HeartbeatService", response);
            } else {
                Log.i("HeartbeatService", "Notification Not Sent: "+response);
            }
        }

        private void writeStream(OutputStream stream, String body)
                throws IOException {

            OutputStream out = new BufferedOutputStream(stream);

            if (body != null) {
                out.write(URLEncoder.encode(body, "UTF-8")
                        .getBytes());
            }
            out.flush();
        }
    }

    public final class ChangeStatusRequestListener implements RequestListener<String> {

        private String TAG = "ChangeStatusRequestListener";

        @Override
        public void onRequestFailure(SpiceException spiceException) {
//            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(final String baseResponse) {
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
