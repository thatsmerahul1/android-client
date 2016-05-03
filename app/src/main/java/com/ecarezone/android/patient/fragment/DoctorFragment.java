package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecarezone.android.patient.AppointmentActivity;
import com.ecarezone.android.patient.CallActivity;
import com.ecarezone.android.patient.ChatActivity;
import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.VideoActivity;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.fragment.dialog.AddDoctorRequestDialog;
import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.rest.AddDoctorRequest;
import com.ecarezone.android.patient.model.rest.AddDoctorResponse;
import com.ecarezone.android.patient.utils.PermissionUtil;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;


/**
 * Created by CHAO WEI on 6/1/2015.
 */
public class DoctorFragment extends EcareZoneBaseFragment implements View.OnClickListener {

    private static final String TAG = DoctorFragment.class.getSimpleName();
    private static final Integer HTTP_STATUS_OK = 200;
    private ImageView doctorStatusIcon;
    private TextView doctorStatusText;
    private TextView doctorNameView;
    private TextView doctorSpecialist;
    private ImageView doctorProfileImg;
    private Button doctorChat;
    private Button doctorVideo;
    private Button doctorVoice;
    private Button addDoctorButton;
    private Button buttonAppointment;
    private Bundle doctorDetailData;
    private Long doctorId;
    private String doctorName;
    private Doctor doctor;

    private Activity mActivity;
    private int viewId;
    private ProgressDialog progressDialog;
    private boolean showAddDoctorOption;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    protected String getCallerName() {
        return DoctorFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_doctor, container, false);

        doctorDetailData = getArguments();
        doctor = doctorDetailData.getParcelable(Constants.DOCTOR_DETAIL);
        showAddDoctorOption = doctorDetailData.getBoolean(DoctorListFragment.ADD_DOCTOR_DISABLE_CHECK, false);
        getAllComponent(view, doctor);
        return view;
    }

    private void getAllComponent(View view, Doctor doctor) {
        doctorStatusIcon = (ImageView) view.findViewById(R.id.doctor_status_icon);
        doctorStatusText = (TextView) view.findViewById(R.id.doctor_status_text);
        doctorNameView = (TextView) view.findViewById(R.id.doctor_name_id);
        doctorSpecialist = (TextView) view.findViewById(R.id.doctor_specialist_id);
        doctorProfileImg = (ImageView) view.findViewById(R.id.doctor_profile_pic_id);
        doctorChat = (Button) view.findViewById(R.id.btn_doctor_chat_id);
        doctorVideo = (Button) view.findViewById(R.id.btn_doctor_video_id);
        doctorVoice = (Button) view.findViewById(R.id.btn_doctor_voice_id);
        addDoctorButton = (Button) view.findViewById(R.id.add_doctor_button);
        buttonAppointment = (Button) view.findViewById(R.id.button_appointment);

        doctorChat.setOnClickListener(this);
        doctorVideo.setOnClickListener(this);
        doctorVoice.setOnClickListener(this);
        addDoctorButton.setOnClickListener(this);
        buttonAppointment.setOnClickListener(this);

        if (!showAddDoctorOption) {
            addDoctorButton.setVisibility(View.GONE);
        }

        if (doctor != null) {
            setDoctorPresenceIcon(doctor.status);
            if(doctor.status.equalsIgnoreCase("1")) {
                doctorStatusText.setText(R.string.doctor_available);
            }
            else{
                doctorStatusText.setText(R.string.doctor_busy);
            }
            doctorStatusText.setVisibility(View.VISIBLE);
            doctorNameView.setText("Dr. " + doctor.name);
            doctorSpecialist.setText(doctor.doctorCategory);
        }
        doctorId = doctor.doctorId;
        doctorName = doctor.name;
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        viewId = v.getId();
        switch (viewId) {
            case R.id.btn_doctor_chat_id:
                chatButtonClicked();
                break;
            case R.id.btn_doctor_video_id:
                callVideoButtonClicked();
                break;
            case R.id.btn_doctor_voice_id:
                callButtonClicked();
                break;
            case R.id.button_appointment:
                createAppointment();
                break;
            case R.id.add_doctor_button:
                sendAddDoctorRequest();
                break;
        }


    }

    private void callVideoButtonClicked() {
        if (PermissionUtil.isPermissionRequired() && PermissionUtil.getAllpermissionRequired(mActivity, PermissionUtil.SINCH_PERMISSIONS).length > 0) {
            PermissionUtil.setAllPermission(mActivity, PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, PermissionUtil.SINCH_PERMISSIONS);
        } else {
            Intent videoScreen = new Intent(mActivity, VideoActivity.class);
            videoScreen.putExtra(Constants.EXTRA_NAME, doctor.name);
            videoScreen.putExtra(Constants.EXTRA_EMAIL, doctor.email);
            startActivity(videoScreen);
        }
    }

    private void callButtonClicked() {
        if (PermissionUtil.isPermissionRequired() && PermissionUtil.getAllpermissionRequired(mActivity, PermissionUtil.SINCH_PERMISSIONS).length > 0) {
            PermissionUtil.setAllPermission(mActivity, PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, PermissionUtil.SINCH_PERMISSIONS);
        } else {
            Intent callScreen = new Intent(mActivity, CallActivity.class);
            callScreen.putExtra(Constants.EXTRA_NAME, doctor.name);
            callScreen.putExtra(Constants.EXTRA_EMAIL, doctor.email);
            startActivity(callScreen);
        }
    }

    private void chatButtonClicked() {
        Intent chatIntent = new Intent(mActivity.getApplicationContext(), ChatActivity.class);
        chatIntent.putExtra(Constants.EXTRA_NAME, doctor.name);
        chatIntent.putExtra(Constants.EXTRA_EMAIL, doctor.email);
        mActivity.startActivity(chatIntent);
        mActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    private void createAppointment() {
        mActivity.startActivity(new Intent(mActivity.getApplicationContext(), AppointmentActivity.class));
        mActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    /*
        Doctor Status status updating
     */
    private void setDoctorPresenceIcon(String status) {
        if (status.equalsIgnoreCase(Constants.AVAILABLE) || status.equalsIgnoreCase("1")) {
            doctorStatusIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_green));
            doctorVideo.setEnabled(true);
            doctorVoice.setEnabled(true);
        } else if (status.equalsIgnoreCase(Constants.BUSY) || status.equalsIgnoreCase("0")) {
            doctorStatusIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_red));
            doctorVideo.setEnabled(false);
            doctorVoice.setEnabled(false);
        }
    }

    /*
        Sending Add docotor request
     */
    private void sendAddDoctorRequest() {
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), "Adding Doctor......");
        AddDoctorRequest request =
                new AddDoctorRequest(doctorId, doctorName, LoginInfo.userName, LoginInfo.hashedPassword, Constants.API_KEY, Constants.deviceUnique);
        getSpiceManager().execute(request, new AddDoctorTaskRequestListener());
    }

    /*
            Add Doctor request response
     */
    public final class AddDoctorTaskRequestListener implements RequestListener<AddDoctorResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(AddDoctorResponse addDoctorResponse) {
            Log.d(TAG, "AddDoctorResponse Status " + addDoctorResponse.status.code);
            progressDialog.dismiss();
            if (addDoctorResponse.status.code.equals(HTTP_STATUS_OK)) {
                AddDoctorRequestDialog addDoctorRequestDialog = new AddDoctorRequestDialog(doctorName);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                addDoctorRequestDialog.show(fragmentManager, "AddDoctorRequestSuccessFragment");
            } else {
                Log.d(TAG, "AddDoctorResponse Status " + addDoctorResponse.status.message);
            }
        }
    }
}
