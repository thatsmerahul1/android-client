package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.AppointmentActivity;
import com.ecarezone.android.patient.CallActivity;
import com.ecarezone.android.patient.ChatActivity;
import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.NetworkCheck;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.VideoActivity;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.fragment.dialog.AddDoctorRequestDialog;
import com.ecarezone.android.patient.fragment.dialog.EcareZoneAlertDialog;
import com.ecarezone.android.patient.fragment.dialog.EditAppointmentDialog;
import com.ecarezone.android.patient.model.Appointment;
import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.database.AppointmentDbApi;
import com.ecarezone.android.patient.model.database.ChatDbApi;
import com.ecarezone.android.patient.model.rest.AddDoctorRequest;
import com.ecarezone.android.patient.model.rest.AddDoctorResponse;
import com.ecarezone.android.patient.model.rest.BookAppointmentRequest;
import com.ecarezone.android.patient.model.rest.DeleteAppointmentRequest;
import com.ecarezone.android.patient.model.rest.ValidateAppointmentRequest;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.utils.PermissionUtil;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.ecarezone.android.patient.utils.Util;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


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
    private TextView unreadChatCount;

    private Activity mActivity;
    private int viewId;
    private ProgressDialog progressDialog;
    private boolean showAddDoctorOption;
    private AppointmentDbApi appointmentDbApi;

    private Appointment currentAppointment;
    private EditAppointmentDialog editAppointmentDialog;

    private static int videoCall = 0;
    private static int voipCall = 1;

    public interface OnAppointmentOptionButtonClickListener {
        public static int BTN_TIME_TO_CALL = 0;
        public static int BTN_CHANGE_TIME = 1;
        public static int BTN_CANCEL = 2;
        public static int BTN_MAKE_AN_APPOINTMENT = 3;

        public void onButtonClicked(int whichButtonClicked, int callType);
    }

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

        appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
        doctorDetailData = getArguments();
        doctor = doctorDetailData.getParcelable(Constants.DOCTOR_DETAIL);
        showAddDoctorOption = doctorDetailData.getBoolean(DoctorListFragment.ADD_DOCTOR_DISABLE_CHECK, false);
        getAllComponent(view, doctor);
        IntentFilter intentFilter = new IntentFilter("send");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(message, intentFilter);
        updateChatCount();
        isAppointmentPresent();
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

        unreadChatCount = (TextView) view.findViewById(R.id.chat_count);

        doctorChat.setOnClickListener(this);
        doctorVideo.setOnClickListener(this);
        doctorVoice.setOnClickListener(this);
        addDoctorButton.setOnClickListener(this);
        buttonAppointment.setOnClickListener(this);

        if (!showAddDoctorOption) {
//            addDoctorButton.setVisibility(View.GONE);
            addDoctorButton.setEnabled(false);
        }

        if (doctor != null) {
            setDoctorPresenceIcon(doctor.status);
            if (doctor.status.equalsIgnoreCase("1")) {
                doctorStatusText.setText(R.string.doctor_available);
            } else {
                doctorStatusText.setText(R.string.doctor_busy);
            }
            doctorStatusText.setVisibility(View.VISIBLE);
            doctorNameView.setText("Dr. " + doctor.name);
            doctorSpecialist.setText(doctor.category);
        }
        doctorId = doctor.doctorId;
        doctorName = doctor.name;


        String imageUrl = doctor.avatarUrl;

        if (imageUrl != null && imageUrl.trim().length() > 8) {
            int dp = mActivity.getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);
            ;
            Picasso.with(mActivity)
                    .load(imageUrl).resize(dp, dp)
                    .centerCrop().placeholder(R.drawable.news_other)
                    .error(R.drawable.news_other)
                    .into(doctorProfileImg);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;
        FragmentManager fragmentManager;
        Bundle bundle;
        viewId = v.getId();
        if (NetworkCheck.isNetworkAvailable(mActivity)) {
            switch (viewId) {
                case R.id.btn_doctor_chat_id:
                    chatButtonClicked();
                    break;
                case R.id.btn_doctor_video_id:

                    editAppointmentDialog =
                            EditAppointmentDialog.newInstance(mOnAppointmentOptionClicked, videoCall);
                    fragmentManager = getActivity().getFragmentManager();

                    bundle = getAppointmentBundle(v);
                    editAppointmentDialog.setArguments(bundle);
                    editAppointmentDialog.show(fragmentManager, "EditAppointmentDialogFragment");
//                    callVideoButtonClicked();
                    break;
                case R.id.btn_doctor_voice_id:


                    bundle = getAppointmentBundle(v);
                    editAppointmentDialog =
                            EditAppointmentDialog.newInstance(mOnAppointmentOptionClicked, voipCall);
                    fragmentManager = getActivity().getFragmentManager();
                    editAppointmentDialog.setArguments(bundle);
                    editAppointmentDialog.show(fragmentManager, "EditAppointmentDialogFragment");
//                    if (v.getTag() != null && v.getTag().equals("make_appointment")) {
//                        createAppointment(false);
//                    } else if(v.getTag() != null && v.getTag().equals("editAppointment")){
//                        createAppointment(true);
//                    }
//                    else{
//
//                    }
//                    callButtonClicked(v);
                    break;
                case R.id.button_appointment:

                    bundle = new Bundle();
                    bundle.putBoolean("isAppointmentAvailable", false);
                    editAppointmentDialog =
                            EditAppointmentDialog.newInstance(mOnAppointmentOptionClicked, voipCall);
                    fragmentManager = getActivity().getFragmentManager();
                    editAppointmentDialog.setArguments(bundle);
                    editAppointmentDialog.show(fragmentManager, "EditAppointmentDialogFragment");

//                    createAppointment(false);
                    break;
                case R.id.add_doctor_button:
                    sendAddDoctorRequest();
                    break;
            }
        } else {
            Toast.makeText(mActivity, "Please check your internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private Bundle getAppointmentBundle(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("doctor_name", doctorName);
        Object obj = v.getTag();
        if(obj != null){
            if(obj instanceof String) {
                String tagValue = ((String) obj);
                if ((tagValue).equalsIgnoreCase("make_appointment")) {
                    bundle.putBoolean("isAppointmentAvailable", false);
                    bundle.putBoolean("isTimeToCall", false);
                }
                else if((tagValue).equalsIgnoreCase("timeToCall")){
                    bundle.putBoolean("isAppointmentAvailable", true);
                    bundle.putBoolean("isTimeToCall", true);
                    bundle.putString("callType", currentAppointment.getCallType());
                    bundle.putString("dateTime", currentAppointment.getTimeStamp());
                }
                else if((tagValue).equalsIgnoreCase("editAppointment")){
                    bundle.putBoolean("isAppointmentAvailable", true);
                    bundle.putBoolean("isTimeToCall", false);
                    bundle.putString("callType", currentAppointment.getCallType());
                    bundle.putString("dateTime", currentAppointment.getTimeStamp());
                }
            }
        }
        return  bundle;
    }

    private void callVideoButtonClicked() {
        if (PermissionUtil.isPermissionRequired() && PermissionUtil.getAllpermissionRequired(mActivity, PermissionUtil.SINCH_PERMISSIONS).length > 0) {
            PermissionUtil.setAllPermission(mActivity, PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, PermissionUtil.SINCH_PERMISSIONS);
        } else {
            Intent videoScreen = new Intent(mActivity, VideoActivity.class);
            videoScreen.putExtra(Constants.EXTRA_NAME, doctor.name);
            videoScreen.putExtra(Constants.EXTRA_EMAIL, doctor.emailId);
            startActivity(videoScreen);
        }
    }

    private void callButtonClicked() {
        if (PermissionUtil.isPermissionRequired() && PermissionUtil.getAllpermissionRequired(mActivity, PermissionUtil.SINCH_PERMISSIONS).length > 0) {
            PermissionUtil.setAllPermission(mActivity, PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, PermissionUtil.SINCH_PERMISSIONS);
        } else {
            Intent callScreen = new Intent(mActivity, CallActivity.class);
            callScreen.putExtra(Constants.EXTRA_NAME, doctor.name);
            callScreen.putExtra(Constants.EXTRA_EMAIL, doctor.emailId);
            startActivity(callScreen);
        }
    }

    private void chatButtonClicked() {
        Intent chatIntent = new Intent(mActivity.getApplicationContext(), ChatActivity.class);
        chatIntent.putExtra(Constants.EXTRA_NAME, doctor.name);
        chatIntent.putExtra(Constants.EXTRA_EMAIL, doctor.emailId);
        mActivity.startActivity(chatIntent);
        mActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    private void createAppointment(boolean isEdit) {
        Intent intent = new Intent(mActivity.getApplicationContext(), AppointmentActivity.class);
        intent.putExtra("doctorId", doctor.doctorId);
        if (isEdit) {
            intent.putExtra("currentAppointment", currentAppointment);
        }
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    private OnAppointmentOptionButtonClickListener mOnAppointmentOptionClicked =
            new OnAppointmentOptionButtonClickListener() {
                @Override
                public void onButtonClicked(int whichButtonClicked, int typeOfCall) {

                    if (whichButtonClicked == OnAppointmentOptionButtonClickListener.BTN_TIME_TO_CALL) {
                        if (typeOfCall == videoCall) {
                            callVideoButtonClicked();
                        } else {
                            callButtonClicked();
                        }
                    } else if (whichButtonClicked == OnAppointmentOptionButtonClickListener.BTN_MAKE_AN_APPOINTMENT) {
                        createAppointment(false);
                    } else if (whichButtonClicked == OnAppointmentOptionButtonClickListener.BTN_CHANGE_TIME) {
                        createAppointment(true);
                    } else if (whichButtonClicked == OnAppointmentOptionButtonClickListener.BTN_CANCEL) {
                        cancelAppointment();
                    }
                }
            };

    public void updateChatCount() {

        if (unreadChatCount != null && doctor != null) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            ChatDbApi chatDbApi = ChatDbApi.getInstance(getActivity());
            int unreadCount = chatDbApi.getUnReadChatCountByUserId(doctor.emailId);
            if (unreadCount > 0) {
                unreadChatCount.setText(String.valueOf(unreadCount));
                unreadChatCount.setVisibility(View.VISIBLE);
            } else {
                unreadChatCount.setText(String.valueOf(0));
                unreadChatCount.setVisibility(View.GONE);
            }
        }

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

    @Override
    public void onStart() {
        super.onStart();
        try {
            isAppointmentPresent();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void isAppointmentPresent() {
        Date currentDate = new Date();
        List<Appointment> appointmentsList = appointmentDbApi.getAppointmentHistory(doctor.doctorId, LoginInfo.userId, currentDate);
        if (appointmentsList != null) {

            if (appointmentsList.size() > 0) {
                currentAppointment = appointmentsList.get(0);
                long bool = 0;
                try {
                    bool = currentAppointment.getAppointmentId();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (bool > 0) {

                    long convDateTime = Util.getTimeInLongFormat(currentAppointment.getTimeStamp());

                    if (convDateTime >= currentDate.getTime()) {
                        if (appointmentsList.get(0).getCallType().equalsIgnoreCase("voip")) {
                            doctorVideo.setCompoundDrawablesWithIntrinsicBounds(null,
                                    getResources().getDrawable(R.drawable.button_video_call_with_green_notification), null, null);
                            doctorVideo.setTag("timeToCall");
                        } else {
                            doctorVoice.setCompoundDrawablesWithIntrinsicBounds(null,
                                    getResources().getDrawable(R.drawable.button_voip_normal_with_green_notification), null, null);
                            doctorVoice.setTag("timeToCall");
                        }
                    } else {
                        if (appointmentsList.get(0).getCallType().equalsIgnoreCase("voip")) {
                            doctorVideo.setCompoundDrawablesWithIntrinsicBounds(null,
                                    getResources().getDrawable(R.drawable.button_video_call_with_notification), null, null);
                            doctorVideo.setTag("editAppointment");
                        } else {
                            doctorVoice.setCompoundDrawablesWithIntrinsicBounds(null,
                                    getResources().getDrawable(R.drawable.button_voip_normal_with_notification), null, null);
                            doctorVoice.setTag("editAppointment");
                        }
                    }
                }
            } else {
                doctorVoice.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.drawable.button_voip_normal), null, null);//.setBackgroundResource(R.drawable.button_voip_normal);
                doctorVoice.setTag("make_appointment");
                doctorVideo.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.drawable.button_video_call_normal), null, null);
                doctorVideo.setTag("make_appointment");
            }
        }
        else {
            doctorVoice.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.button_voip_normal), null, null);//.setBackgroundResource(R.drawable.button_voip_normal);
            doctorVoice.setTag("make_appointment");
            doctorVideo.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.button_video_call_normal), null, null);
            doctorVideo.setTag("make_appointment");
        }
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
                AddDoctorRequestDialog addDoctorRequestDialog = new AddDoctorRequestDialog();
                Bundle bndl = new Bundle();
                bndl.putString("doctor_name", doctorName);
                addDoctorRequestDialog.setArguments(bndl);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                addDoctorRequestDialog.show(fragmentManager, "AddDoctorRequestSuccessFragment");
            } else {
                Log.d(TAG, "AddDoctorResponse Status " + addDoctorResponse.status.message);
            }
        }
    }

    /*Incoming chat message receiver*/
    BroadcastReceiver message = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("send")) {
                updateChatCount();
            }
        }
    };

    /******************
     * VALIDATE APPOINTMENT
     *******************/
    private void validateAppointment() {

        List<Appointment> appointmentsList1 = appointmentDbApi.getAppointments(String.valueOf(doctor.doctorId), false);
        for (Appointment appointmentIns : appointmentsList1) {

            try {
                ValidateAppointmentRequest request =
                        new ValidateAppointmentRequest(appointmentIns.getAppointmentId(), LoginInfo.userName, LoginInfo.hashedPassword, Constants.API_KEY, Constants.deviceUnique);
                getSpiceManager().execute(request, new ValidateTaskRequestListener(appointmentIns.getAppointmentId()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class ValidateTaskRequestListener implements RequestListener<BaseResponse> {

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

    /*********CANCEL APPOINTMENT***********/
     /*
        Cancel Appointment
     */
    private void cancelAppointment() {
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), "Adding Doctor......");
        long appointmentId = currentAppointment.getAppointmentId();
        String appointmentTime = currentAppointment.getTimeStamp();
        String callType = currentAppointment.getCallType();//voice/video

        DeleteAppointmentRequest request =
                new DeleteAppointmentRequest(LoginInfo.userName, LoginInfo.hashedPassword,
                        Constants.API_KEY, Constants.deviceUnique,
                        appointmentTime,
                        callType, appointmentId);
        getSpiceManager().execute(request, new DeleteAppointmentTaskRequestListener());

    }

    public final class DeleteAppointmentTaskRequestListener implements RequestListener<BaseResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestSuccess(BaseResponse baseResponse) {

            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            if(baseResponse.status.code == 200 &&
                    baseResponse.status.message.startsWith("Appointment deleted successfully")){
                appointmentDbApi.deleteAppointment(currentAppointment);
                EcareZoneAlertDialog.showAlertDialog(getActivity(), getString(R.string.alert),
                       getString(R.string.appointment_deleted), getString(R.string.welcome_button_ok));
                try {
                    isAppointmentPresent();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };
}
