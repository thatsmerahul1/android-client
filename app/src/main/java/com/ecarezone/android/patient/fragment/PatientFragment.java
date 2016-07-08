package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.DoctorActivity;
import com.ecarezone.android.patient.DoctorBioActivity;
import com.ecarezone.android.patient.EcareZoneBaseActivity;
import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.NetworkCheck;
import com.ecarezone.android.patient.ProfileDetailsActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.UserProfile;
import com.ecarezone.android.patient.model.database.ChatDbApi;
import com.ecarezone.android.patient.model.database.ProfileDbApi;
import com.ecarezone.android.patient.model.rest.Data;
import com.ecarezone.android.patient.model.rest.GetDoctorRequest;
import com.ecarezone.android.patient.model.rest.GetDoctorResponse;
import com.ecarezone.android.patient.utils.SinchUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;

/**
 * Created by CHAO WEI on 5/12/2015.
 */
public class PatientFragment extends EcareZoneBaseFragment implements View.OnClickListener, SinchUtil.onChatHistoryChangeListner {

    private String TAG = PatientFragment.class.getSimpleName();

    private LinearLayout mProfileFinishReminderLayout = null;
    private RelativeLayout mMessageCounterLayout = null;
    private TextView mHomeMessageIndicator;

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

        mProfileFinishReminderLayout = (LinearLayout) view.findViewById(R.id.profileFinishReminderLayout);
        new ProfileFinishedAsyncTask().execute();

        mMessageCounterLayout = (RelativeLayout)view.findViewById(R.id.new_message_counter_layout);
        mHomeMessageIndicator = (TextView)view.findViewById(R.id.text_view_home_message_indicator);
        return view;
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
                if(NetworkCheck.isNetworkAvailable(getActivity())) {
                    GetDoctorRequest request = new GetDoctorRequest();
                    getSpiceManager().execute(request, new RecommondedDoctor());
                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_finish_profile_ok:
                ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
                UserProfile profile = profileDbApi.getMyProfile();
                if(profile != null) {
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

//            Log.d(TAG, "Status Code " + response.status.code);
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

    private void updateUnreadMessageCount(int noOfUnreadMessage){
        if (noOfUnreadMessage != NumberUtils.INTEGER_ZERO) {
            mHomeMessageIndicator.setVisibility(View.VISIBLE);
            mHomeMessageIndicator.setText(String.valueOf(noOfUnreadMessage));
        }else{
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
}