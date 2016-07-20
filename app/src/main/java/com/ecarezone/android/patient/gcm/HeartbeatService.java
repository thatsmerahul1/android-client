package com.ecarezone.android.patient.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.ecarezone.android.patient.PatientApplication;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.UserProfile;
import com.ecarezone.android.patient.model.database.ProfileDbApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.ByteArrayEntity;
import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;

/**
 * Created by Umesh on 27-06-2016.
 */
public class HeartbeatService extends IntentService {

    PatientApplication patientApplication;
    int status;
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
            patientApplication = (PatientApplication) getApplicationContext();


            if (patientApplication.getNameValuePair().containsKey(Constants.STATUS_CHANGE)) {
                if (!patientApplication.getNameValuePair().get(Constants.STATUS_CHANGE)) {
                    status = Constants.IDLE;
                } else {
                    status = Constants.ONLINE;
                }

                if (patientApplication.getLastAvailabilityStatus() != status) {
                    ChangeStatusRequest changeStatusService = new ChangeStatusRequest(status);
                    changeStatusService.startHttpRequest();
                }
            } else {
                patientApplication.getNameValuePair().put(Constants.STATUS_CHANGE, false);
                status = Constants.ONLINE;
            }


            Log.i("HeartbeatService", "status updated");
        }
    }


    private class ChangeStatusRequest {

// {"email":"uapatient1@gmail.com", "password":"wkkdl/bt34SeumhQNMNlzQ==",
// "name":"name", "role": "1","status":"0","deviceUnique":"b5d4c425-a305-4363-8d6a-f3fb65635abf"}

        private String requestBody;

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
            requestBody = jsonObj.toString();
        }

        public void startHttpRequest() {

            String response = null;
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("http://188.166.55.204:8080/ECZ/notification/pushstatus/" + LoginInfo.userId);
            // Set content type
            httpPost.setHeader("Content-Type", "application/json");

            if(requestBody != null) {
                //Post Data
                HttpEntity entity = new ByteArrayEntity(requestBody.getBytes());
                httpPost.setEntity(entity);
            }

            //making POST request.
            try {
                HttpResponse responseHttp = httpClient.execute(httpPost);
                if (responseHttp.getStatusLine().getStatusCode() == 200) {
                    StringBuilder sb = new StringBuilder();

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(
                                    responseHttp.getEntity().getContent()));
                    String resp = null;
                    while ((resp = reader.readLine()) != null) {
                        sb.append(resp);
                    }
                    response = sb.toString();
                }
                // write response to log
                Log.d("Http Post Response:", responseHttp.toString());
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }

            if (response != null && response.equalsIgnoreCase("Notification Sent")) {
                Log.i("HeartbeatService", response);
                patientApplication.setLastAvailabilityStatus(status);

            } else {
                Log.i("HeartbeatService", "Notification Not Sent: " + response);
            }
        }
    }
}
