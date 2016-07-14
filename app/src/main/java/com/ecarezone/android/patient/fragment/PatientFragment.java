package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.DoctorBioActivity;
import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.NetworkCheck;
import com.ecarezone.android.patient.ProfileDetailsActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.adapter.OnButtonClickedListener;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.fragment.dialog.EcareZoneAlertDialog;
import com.ecarezone.android.patient.model.Appointment;
import com.ecarezone.android.patient.model.AppointmentAcceptRequest;
import com.ecarezone.android.patient.model.AppointmentResponse;
import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.UserProfile;
import com.ecarezone.android.patient.model.database.AppointmentDbApi;
import com.ecarezone.android.patient.model.database.ChatDbApi;
import com.ecarezone.android.patient.model.database.DoctorProfileDbApi;
import com.ecarezone.android.patient.model.database.ProfileDbApi;
import com.ecarezone.android.patient.model.rest.DeleteAppointmentRequest;
import com.ecarezone.android.patient.model.rest.GetDoctorRequest;
import com.ecarezone.android.patient.model.rest.GetDoctorResponse;
import com.ecarezone.android.patient.model.rest.PendingAppointmentRequest;
import com.ecarezone.android.patient.model.rest.PendingAppointmentResponse;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.ecarezone.android.patient.utils.SinchUtil;
import com.ecarezone.android.patient.utils.Util;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import ch.boye.httpclientandroidlib.client.ClientProtocolException;

/**
 * Created by CHAO WEI on 5/12/2015.
 */
public class PatientFragment extends EcareZoneBaseFragment implements View.OnClickListener, SinchUtil.onChatHistoryChangeListner {

    private String TAG = PatientFragment.class.getSimpleName();

    private LinearLayout mProfileFinishReminderLayout = null;
    private RelativeLayout mMessageCounterLayout = null;
    private TextView mHomeMessageIndicator;
    private boolean requestInPrgress = false;
    private LinearLayout mPendingAppointmentLayout;
    private LinearLayout mCurrentAppointmentLayout;
    private SimpleDateFormat sdf;
    private ProgressDialog progressDialog;
    private long currentAppointmentId;

    /*Empty fragment*/
    public PatientFragment() {
    }

    public static PatientFragment getNewInstance() {
        PatientFragment fragment = new PatientFragment();
        return fragment;
    }

    @Override
    protected String getCallerName() {
        return PatientFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_patient_main, container, false);
        LinearLayout newsFeedLayout = (LinearLayout) view.findViewById(R.id.newsFeedLayout);
        newsFeedLayout.setOnClickListener(this);

        LinearLayout chatAlertLayout = (LinearLayout) view.findViewById(R.id.chatAlertLayout);
        chatAlertLayout.setOnClickListener(this);

        LinearLayout recommendedDoctorLayout = (LinearLayout) view.findViewById(R.id.recommendedDoctorLayout);
        //TODO hide or show this recommended doctor layout based on data.
        //recommendedDoctorLayout.setVisibility(View.GONE);
        Button viewDoctorProfileButton = (Button) view.findViewById(R.id.viewDoctorProfile);
        viewDoctorProfileButton.setOnClickListener(this);

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        mProfileFinishReminderLayout = (LinearLayout) view.findViewById(R.id.profileFinishReminderLayout);
//        new ProfileFinishedAsyncTask().execute();

        mPendingAppointmentLayout = (LinearLayout) view.findViewById(R.id.pendingAppointmentLayout);
        mCurrentAppointmentLayout = (LinearLayout) view.findViewById(R.id.currentAppointmentLayout);

        mMessageCounterLayout = (RelativeLayout) view.findViewById(R.id.new_message_counter_layout);
        mHomeMessageIndicator = (TextView) view.findViewById(R.id.text_view_home_message_indicator);

        populateAppointmentsFromDatabase();
        return view;
    }

    private void populateAppointmentsFromDatabase() {

        AppointmentDbApi appointmentDbi = AppointmentDbApi.getInstance(getApplicationContext());
        List<Appointment> appointmentList = appointmentDbi.getAllPendingAppointments();
        List<AppointmentResponse> appointmentResponse = Util.getAppointmentResponseList(appointmentList);
        if (appointmentResponse != null) {
            populatePendingAppointmentList(appointmentResponse);
        }
    }

    BroadcastReceiver message = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUnreadMessageCount(ChatDbApi.getInstance(context).getUnReadChatCount());
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        updateUnreadMessageCount(ChatDbApi.getInstance(getContext()).getUnReadChatCount());
        updateAppointmentReminders();
        if (!requestInPrgress) {
            fetchPendingAppointments();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Constants.ECARE_ZONE);
        new ProfileFinishedAsyncTask().execute();
        SinchUtil.setChatHistoryChangeListner(this);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(message,
                new IntentFilter("send"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newsFeedLayout:
                ((MainActivity) getActivity()).onNavigationChanged(R.layout.frag_news_categories, null);
                break;
            case R.id.chatAlertLayout:
                ((MainActivity) getActivity()).onNavigationChanged(R.layout.frag_doctor_list, null);
                break;
            case R.id.viewDoctorProfile:
                // TODO call the doctor profile activity.
//                ((MainActivity) getActivity()).onNavigationChanged(R.layout.frag_doctor_list, null);
                if (NetworkCheck.isNetworkAvailable(getActivity())) {
                    GetDoctorRequest request = new GetDoctorRequest();
                    getSpiceManager().execute(request, new RecommondedDoctor());
                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_finish_profile_ok:
                ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
                UserProfile profile = profileDbApi.getMyProfile();
                if (profile != null) {
                    String profileId = profile.profileId;
                    startActivityForResult(new Intent(getApplicationContext(), ProfileDetailsActivity.class)
                            .putExtra(ProfileDetailsActivity.IS_NEW_PROFILE, false)
                            .putExtra(ProfileDetailsActivity.PROFILE_ID, profileId), UserProfileFragment.VIEW_PROFILE_REQUEST_CODE);
                }
                break;
        }
    }


    public final class RecommondedDoctor implements RequestListener<GetDoctorResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
//            progressDialog.dismiss();

        }

        @Override
        public void onRequestSuccess(final GetDoctorResponse response) {

            if (response.status != null) {
                if (response.status.code == 200) {
                    final Activity activity = getActivity();

                    Intent showDoctorIntent = new Intent(activity.getApplicationContext(), DoctorBioActivity.class);
                    Doctor doctor = response.data;
                    Bundle data = new Bundle();
                    data.putParcelable(Constants.DOCTOR_DETAIL, doctor);

                    showDoctorIntent.putExtra(Constants.DOCTOR_BIO_DETAIL, data);
                    activity.startActivity(showDoctorIntent);
                }
            }
        }
    }

    @Override
    public void onChange(int noOfUnreadMessage) {
//        updateUnreadMessageCount(noOfUnreadMessage);
    }

    private void updateUnreadMessageCount(int noOfUnreadMessage) {
        if (noOfUnreadMessage != NumberUtils.INTEGER_ZERO) {
            mHomeMessageIndicator.setVisibility(View.VISIBLE);
            mHomeMessageIndicator.setText(String.valueOf(noOfUnreadMessage));
        } else {
            mHomeMessageIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(message);
    }

    class ProfileFinishedAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
            return profileDbApi.isMyProfileComplete();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                // profile is finished. so remove this layout
                mProfileFinishReminderLayout.setVisibility(View.GONE);
            } else {
                mProfileFinishReminderLayout.setVisibility(View.VISIBLE);
                Button button_finish_profile_ok = (Button) mProfileFinishReminderLayout.findViewById(R.id.button_finish_profile_ok);
                button_finish_profile_ok.setOnClickListener(PatientFragment.this);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UserProfileFragment.VIEW_PROFILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Successfully updated the profile");
            } else {
                Log.d(TAG, "No profile changes done:");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStop() {
        super.onStop();
        SinchUtil.removeChatHistoryChangeListner();
    }

    /******
     * APPOINTMENT REMINDERS
     ******/

    private void updateAppointmentReminders() {
        AppointmentDbApi appointmentDbi = AppointmentDbApi.getInstance(getApplicationContext());
        DoctorProfileDbApi doctorProfileDbApi = DoctorProfileDbApi.getInstance(getApplicationContext());

        List<Appointment> appointmentList =
                appointmentDbi.getAppointmentsByPatientId(String.valueOf(LoginInfo.userId), true);

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCurrentAppointmentLayout.removeAllViews();
        boolean isCurrentAppointmentAvailable = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMM. dd. hh:mm a");
        for (Appointment appointment : appointmentList) {

            long appointmentTime = Util.getTimeInLongFormat(appointment.getTimeStamp());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(appointmentTime);
            calendar.add(Calendar.HOUR_OF_DAY, -12);
            calendar.add(Calendar.MINUTE, -30);
            long last12Hrs = calendar.getTimeInMillis();

            long currentTime = System.currentTimeMillis();

            if (currentTime > (last12Hrs) &&
                    currentTime < (appointmentTime + 30 * 60 * 1000)) { // appointment time + 30 minutes
//              show these appointments in the screen

                View view = inflater.inflate(R.layout.current_appointment_layout, null);

                TextView textViewAppointmentType = (TextView) view.findViewById(R.id.appointment_type);
                TextView textViewDoctorName = (TextView) view.findViewById(R.id.doctor_name);
                TextView textViewAppointmentTime = (TextView) view.findViewById(R.id.appointment_time);
                ImageView appointmentTypeIcon = (ImageView) view.findViewById(R.id.appointment_type_icon);

                Doctor doctor = doctorProfileDbApi.getProfileById(appointment.getDoctorId());

                if (doctor != null) {
                    textViewDoctorName.setText(getString(R.string.dr) + doctor.name + ": ");
                }
                try {
                    long dateTime = Long.parseLong(appointment.getTimeStamp());
                    textViewAppointmentTime.setText(Util.getTimeInStringFormat(dateTime, sdf));
                } catch (NumberFormatException nfe) {
                    textViewAppointmentTime.setText(appointment.getTimeStamp());
                }


                textViewAppointmentType.setText(appointment.getCallType() + " " + getString(R.string.call_appointment));

                if (appointment.getCallType().equalsIgnoreCase("video")) {
                    appointmentTypeIcon.setImageResource(R.drawable.icon_video);
                } else {
                    appointmentTypeIcon.setImageResource(R.drawable.icon_call);
                }

                isCurrentAppointmentAvailable = true;
                mCurrentAppointmentLayout.addView(view);
            }
        }
        if (isCurrentAppointmentAvailable) {
            mCurrentAppointmentLayout.setVisibility(View.VISIBLE);
        } else {
            mCurrentAppointmentLayout.setVisibility(View.GONE);
        }


    }

    /********************
     * FETCH PENDING APPOINTMENTS
     ****************/
    private void fetchPendingAppointments() {

        requestInPrgress = true;
        PendingAppointmentRequest request =
                new PendingAppointmentRequest(LoginInfo.userId);
        getSpiceManager().execute(request, new FetchAppointmentsTaskRequestListener());


    }

    private class FetchAppointmentsTaskRequestListener implements RequestListener<PendingAppointmentResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            requestInPrgress = false;
        }

        @Override
        public void onRequestSuccess(PendingAppointmentResponse response) {
            requestInPrgress = false;
            if (response != null) {
                if (response.status.code == 200) {
                    AppointmentDbApi appointmentDbi = AppointmentDbApi.getInstance(getApplicationContext());
                    ListIterator<AppointmentResponse> appointmentIterator = response.data.listIterator();

                    while (appointmentIterator.hasNext()) {

                        AppointmentResponse appointmentResponse = appointmentIterator.next();
                        if (appointmentResponse.patientId != null) {

                            if (!appointmentDbi.isAppointmentPresent(appointmentResponse.id)) {

                                Appointment appointment = new Appointment();
                                appointment.setConfirmed(false);
                                appointment.setTimeStamp(appointmentResponse.dateTime);
                                appointment.setAppointmentId(appointmentResponse.id);
                                appointment.setCallType(appointmentResponse.callType);
                                appointment.setpatientId(appointmentResponse.patientId);
                                appointment.setDoctorId(appointmentResponse.doctorId);

                                appointmentDbi.saveAppointment(appointment);
                            }
                        }
                    }
                    populatePendingAppointmentList(response.data);

                }
            }
        }
    }

    /**
     * @param data
     */
    private void populatePendingAppointmentList(final List<AppointmentResponse> data) {

        if (mPendingAppointmentLayout != null) {

            DoctorProfileDbApi doctorProfileDbApi = DoctorProfileDbApi.getInstance(getApplicationContext());
            mPendingAppointmentLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ListIterator<AppointmentResponse> appointmentIterator = data.listIterator();
            int position = 0;

            if (data.size() > 0) {
                mPendingAppointmentLayout.setVisibility(View.VISIBLE);
            }

            while (appointmentIterator.hasNext()) {

                AppointmentResponse appointmentResponse = appointmentIterator.next();

                if (appointmentResponse.patientId == null) {
                    continue;
                }

                View view = inflater.inflate(R.layout.appointment_pending_list_item, null);

                TextView appointmentPendingTime = (TextView) view.findViewById(R.id.appointment_time);
                TextView appointmentTypeOfCall = (TextView) view.findViewById(R.id.type_of_call);
                TextView appointmentPatientName = (TextView) view.findViewById(R.id.patient_name);
                ImageView pendingDoctorAvatar = (ImageView) view.findViewById(R.id.appointment_doctor_avatar);

                Button accept = (Button) view.findViewById(R.id.patient_appointment_request_accept);
                accept.setTag(position);
                Button reject = (Button) view.findViewById(R.id.patient_appointment_request_reject);
                reject.setTag(position);

                //TODO:
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptAppointment(data.get((Integer) v.getTag()));
                    }
                });

                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rejectAppointment(data.get((Integer) v.getTag()));
//                        DeleteAppointment deleteAppointment = new DeleteAppointment(
//                                data.get((Integer) v.getTag()).id, mButtonClickListener);
//                        deleteAppointment.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                });

                Doctor doctor = null;
                if (appointmentResponse != null) {
                    doctor = doctorProfileDbApi.getProfileById(appointmentResponse.doctorId);
                }

                if (doctor != null) {

                    appointmentPatientName.setText("Patient: " + doctor.name);
                    appointmentPendingTime.setText(
                            sdf.format(new Date(Util.getTimeInLongFormat(appointmentResponse.dateTime.trim()))));
                    appointmentTypeOfCall.setText(WordUtils.capitalize(appointmentResponse.callType) + " call");
                    pendingDoctorAvatar.setImageResource(R.drawable.request_icon);
                    if (doctor.avatarUrl != null) {
                        Picasso.with(getActivity())
                                .load(doctor.avatarUrl)
                                .config(Bitmap.Config.RGB_565).fit()
                                .centerCrop()
                                .placeholder(R.drawable.request_icon)
                                .into(pendingDoctorAvatar);
                    }
                }

                position++;
                mPendingAppointmentLayout.addView(view);
            }
        } else {
            mPendingAppointmentLayout.setVisibility(View.GONE);
        }

    }

    private void acceptAppointment(AppointmentResponse appointment) {

        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), "Adding Doctor......");
        progressDialog.setMessage(getString(R.string.processing));
        progressDialog.show();
        currentAppointmentId = appointment.id;
        AppointmentAcceptRequest request =
                new AppointmentAcceptRequest(appointment.id);
        getSpiceManager().execute(request, new AcceptAppointmentTaskRequestListener());

    }

    public final class AcceptAppointmentTaskRequestListener implements RequestListener<BaseResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestSuccess(BaseResponse baseResponse) {

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
            if (baseResponse.status.code == 200 &&
                    baseResponse.status.message.startsWith("Appointment accepted")) {
                appointmentDbApi.deleteAppointment(currentAppointmentId);
                EcareZoneAlertDialog.showAlertDialog(getActivity(), getString(R.string.alert),
                        getString(R.string.appointment_accepted), getString(R.string.welcome_button_ok));
                try {
                    populateAppointmentsFromDatabase();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    ;

    private void rejectAppointment(AppointmentResponse appointment) {

        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), "Adding Doctor......");
        progressDialog.setMessage(getString(R.string.processing));
        progressDialog.show();
        currentAppointmentId = appointment.id;
        DeleteAppointmentRequest request =
                new DeleteAppointmentRequest(LoginInfo.userName, LoginInfo.hashedPassword,
                        Constants.API_KEY, Constants.deviceUnique,
                        appointment.dateTime,
                        appointment.callType, appointment.id);
        getSpiceManager().execute(request, new DeleteAppointmentTaskRequestListener());

    }

    public final class DeleteAppointmentTaskRequestListener implements RequestListener<BaseResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestSuccess(BaseResponse baseResponse) {

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
            if (baseResponse.status.code == 200 &&
                    baseResponse.status.message.startsWith("Appointment deleted successfully")) {
                appointmentDbApi.deleteAppointment(currentAppointmentId);
                EcareZoneAlertDialog.showAlertDialog(getActivity(), getString(R.string.alert),
                        getString(R.string.appointment_deleted), getString(R.string.welcome_button_ok));
                try {
                    populateAppointmentsFromDatabase();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    ;

    /*********
     * REJECT APPOINTMENT NETWORK COMMUNICATION
     ***********/
     /*
        Reject Appointment
        Retrofit is unable to send body parameter with method type delete
         This is a workaround for this retrofit limitation
     */

    private class DeleteAppointment extends AsyncTask<String, String, String> {

        private int appointmentId;
        private String body;
        private OnButtonClickedListener mButtonClickListener;

        public DeleteAppointment(int appointmentId, OnButtonClickedListener mButtonClickListener) {
            this.appointmentId = appointmentId;
            this.mButtonClickListener = mButtonClickListener;
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("apiKey", Constants.API_KEY);
                jsonObj.put("password", LoginInfo.hashedPassword);
                jsonObj.put("deviceUnique", Constants.deviceUnique);
                jsonObj.put("email", LoginInfo.userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            body = jsonObj.toString();
        }

        @Override
        protected String doInBackground(String... param) {

            String response = null;
            String line = "";
            URL url;
            HttpURLConnection urlConnection = null;
            BufferedReader rd;

            try {
                url = new URL("http://188.166.55.204:9000/deleteappointment/" + appointmentId);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(out);

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
            return response;
        }

        private void writeStream(OutputStream stream)
                throws IOException {

            OutputStream out = new BufferedOutputStream(stream);

            if (body != null) {
                out.write(URLEncoder.encode(body, "UTF-8")
                        .getBytes());
            }
            out.flush();
        }


        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (mButtonClickListener != null) {
                if (s != null && s.equalsIgnoreCase("Appointment deleted successfully")) {
                    mButtonClickListener.onButtonClickedListener(-1, true);
                } else {
                    mButtonClickListener.onButtonClickedListener(-1, false);
                }
            }
        }
    }

    private OnButtonClickedListener mButtonClickListener = new OnButtonClickedListener() {
        @Override
        public void onButtonClickedListener(int position, boolean isSuccessful) {
            if (isSuccessful) {
                Toast.makeText(getActivity(), getString(R.string.appointment_deleted),
                        Toast.LENGTH_LONG).show();
            }
        }
    };
}