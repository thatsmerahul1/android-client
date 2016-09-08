package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.CallActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.database.DoctorProfileDbApi;
import com.ecarezone.android.patient.service.SinchService;
import com.ecarezone.android.patient.utils.SinchUtil;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by L&T Technology Services.
 */
public class CallFragment extends EcareZoneBaseFragment implements View.OnClickListener, CallListener {

    private Activity mActivity;
    private Bundle incomingCallArguments;
    private TextView topPanel, progressPanel, inComingVideoCallRemoteUser;
    private LinearLayout bootomPanel, incomingCallPanel;
    private ImageView incomingUserProfilePic;
    private ImageView incomingUserProfilePic2;
    private ImageView doctorAvatar;
    private FrameLayout callMainBackground;
    private Button endcallButton, answerButton, declineButton;
    private String TAG = CallFragment.class.getName();
    private String mCallId;

    @Override
    protected String getCallerName() {
        return CallFragment.class.getSimpleName();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        incomingCallArguments = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_call, container, false);
        getAllComponent(view);
        return view;
    }

    private void getAllComponent(View view) {
        topPanel = (TextView) view.findViewById(R.id.calltopPanel);
        bootomPanel = (LinearLayout) view.findViewById(R.id.callbottomPanel);
        doctorAvatar = (ImageView) view.findViewById(R.id.doctor_avatar);
        progressPanel = (TextView) view.findViewById(R.id.callprogressPanel);
        endcallButton = (Button) view.findViewById(R.id.end_call);
        callMainBackground = (FrameLayout) view.findViewById(R.id.callMainBackground);

        ((CallActivity) getActivity()).hideActionbar();

        if (incomingCallArguments.getString(SinchService.CALL_ID) != null) {
            mCallId = incomingCallArguments.getString(SinchService.CALL_ID);
            incomingCallPanel = (LinearLayout) view.findViewById(R.id.incomingCallPanel);
            inComingVideoCallRemoteUser = (TextView) view.findViewById(R.id.remoteUser);
            answerButton = (Button) view.findViewById(R.id.answerButton);
            declineButton = (Button) view.findViewById(R.id.declineButton);
            incomingUserProfilePic = (ImageView)view.findViewById(R.id.doctor_avatar);
            incomingUserProfilePic2 = (ImageView)view.findViewById(R.id.incomingUserProfilePic);
            incomingCallPanel.setVisibility(View.VISIBLE);
            inComingVideoCallRemoteUser.setText(incomingCallArguments.getString(SinchService.INCOMING_CALL_USER));

            answerButton.setOnClickListener(this);
            declineButton.setOnClickListener(this);

        }
        //to get user name and image
        DoctorProfileDbApi profileDbApi = DoctorProfileDbApi.getInstance(mActivity);
        String email = incomingCallArguments.getString("email");
        Doctor tempProfiles;
        int dp = mActivity.getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);
        if(email != null){
           tempProfiles = profileDbApi.getProfile(email);
            if(tempProfiles != null) {
                String imageUrl = tempProfiles.avatarUrl;
                if (imageUrl != null && imageUrl.trim().length() > 8) {
                    Picasso.with(mActivity)
                            .load(imageUrl).resize(dp, dp)
                            .centerCrop().placeholder(R.drawable.news_other)
                            .error(R.drawable.news_other)
                            .into(doctorAvatar);

                }
            }
        } else {
            tempProfiles = profileDbApi.getProfile(incomingCallArguments.getString("INCOMING_CALL_USER"));
            if (tempProfiles != null) {
                inComingVideoCallRemoteUser.setText(tempProfiles.name);
                String imageUrl = tempProfiles.avatarUrl;
                if (imageUrl != null && imageUrl.trim().length() > 8) {
                    Picasso.with(mActivity)
                            .load(imageUrl).resize(dp, dp)
                            .centerCrop().placeholder(R.drawable.news_other)
                            .error(R.drawable.news_other)
                            .into(incomingUserProfilePic);
                    Picasso.with(mActivity)
                            .load(imageUrl).resize(dp, dp)
                            .centerCrop().placeholder(R.drawable.news_other)
                            .error(R.drawable.news_other)
                            .into(incomingUserProfilePic2);
                }
            }

        }
        if (tempProfiles != null) {
            String imageUrl = tempProfiles.avatarUrl;
            if (imageUrl != null && imageUrl.trim().length() > 8) {
                Picasso.with(mActivity)
                        .load(imageUrl).resize(dp, dp)
                        .centerCrop().placeholder(R.drawable.news_other)
                        .error(R.drawable.news_other)
                        .into(doctorAvatar);
                topPanel.setText(tempProfiles.name);
            }
        }
        endcallButton.setOnClickListener(this);

        topPanel.setVisibility(View.GONE);
        bootomPanel.setVisibility(View.GONE);
        doctorAvatar.setVisibility(View.GONE);
    }

    @Override
    public void onCallProgressing(Call call) {
        Log.d(TAG, "Call progressing");
        SinchUtil.getSinchAudioPlayer().playProgressTone();
        mCallId = call.getCallId();
    }

    @Override
    public void onCallEstablished(Call call) {
        Log.d(TAG, "Call established");
        SinchUtil.getSinchAudioPlayer().stopProgressTone();
        mActivity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        mCallId = call.getCallId();
        addOutGoingCallScreen();
    }

    @Override
    public void onCallEnded(Call call) {
        CallEndCause cause = call.getDetails().getEndCause();
        Log.d(TAG, "Call ended. Reason: " + cause.toString());
        SinchUtil.getSinchAudioPlayer().stopProgressTone();
        mActivity.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        String endMsg = "Call ended: " + call.getDetails().toString();
        Toast.makeText(mActivity, "Call ended "/*endMsg*/, Toast.LENGTH_LONG).show();
        mCallId = call.getCallId();
        endCall();
    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> list) {
        Log.i(TAG, "Send push notification when it is required");
    }

    /*Adding GUI for outgoing call*/
    private void addOutGoingCallScreen() {
        topPanel.setVisibility(View.VISIBLE);
        bootomPanel.setVisibility(View.VISIBLE);
        doctorAvatar.setVisibility(View.VISIBLE);
        progressPanel.setVisibility(View.GONE);
        callMainBackground.setBackgroundColor(Color.WHITE);
    }

    /*End the ongoing call*/
    private void endCall() {
        SinchUtil.getSinchAudioPlayer().stopProgressTone();
        Call call = SinchUtil.getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        mActivity.finish();
        SinchUtil.getSinchAudioPlayer().stopRingtone();
    }

    @Override
    public void onClick(View view) {
        if (view == null) return;

        final int viewId = view.getId();
        switch (viewId) {
            case R.id.end_call:
                endCall();
                break;
            case R.id.answerButton:
                answerClicked();
                break;
            case R.id.declineButton:
                declineClicked();
                break;
            default:
                Log.i(getCallerName(), "not clicked any valid view");
        }
    }

    /*Incoming Call answered*/
    private void answerClicked() {
        SinchUtil.getSinchAudioPlayer().stopRingtone();
        Call call = SinchUtil.getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();
            incomingCallPanel.setVisibility(View.GONE);
        } else {
            mActivity.finish();
        }
    }

    /*Incoming Call decline*/
    private void declineClicked() {
        SinchUtil.getSinchAudioPlayer().stopRingtone();
        Call call = SinchUtil.getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        mActivity.finish();
    }
}
