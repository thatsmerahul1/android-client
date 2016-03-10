package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.VideoActivity;
import com.ecarezone.android.patient.service.SinchService;
import com.ecarezone.android.patient.utils.SinchUtil;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

/**
 * Created by L&T Technology Services.
 */
public class VideoFragment extends EcareZoneBaseFragment implements View.OnClickListener, VideoCallListener {

    private boolean mVideoViewsAdded;
    private String TAG = VideoFragment.class.getSimpleName();
    private String mCallId;
    private RelativeLayout localVideoView;
    private LinearLayout incomingCallPanel;
    private RelativeLayout remoteVideoView;
    private FrameLayout topPanel, bootomPanel;
    private RelativeLayout cameraPanel;
    private TextView progressPanel, inComingVideoCallRemoteUser;
    private Activity mActivity;
    private Button hangupButton, answerButton, declineButton;
    private Bundle inComingvideoArguments;

    @Override
    protected String getCallerName() {
        return VideoFragment.class.getSimpleName();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        inComingvideoArguments = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_video, container, false);
        getAllComponent(view);
        return view;
    }

    private void getAllComponent(View view) {
        localVideoView = (RelativeLayout) view.findViewById(R.id.localVideo);
        remoteVideoView = (RelativeLayout) view.findViewById(R.id.remoteVideo);
        ((VideoActivity) getActivity()).hideActionbar();
        topPanel = (FrameLayout) view.findViewById(R.id.topPanel);
        bootomPanel = (FrameLayout) view.findViewById(R.id.bottomPanel);
        cameraPanel = (RelativeLayout) view.findViewById(R.id.cameraPanel);
        progressPanel = (TextView) view.findViewById(R.id.progressPanel);
        hangupButton = (Button) view.findViewById(R.id.hangupButton);

        topPanel.setVisibility(View.GONE);
        bootomPanel.setVisibility(View.GONE);
        cameraPanel.setVisibility(View.GONE);

        if (inComingvideoArguments != null) {
            mCallId = inComingvideoArguments.getString(SinchService.CALL_ID);

            incomingCallPanel = (LinearLayout) view.findViewById(R.id.incomingCallPanel);
            inComingVideoCallRemoteUser = (TextView) view.findViewById(R.id.remoteUser);
            answerButton = (Button) view.findViewById(R.id.answerButton);
            declineButton = (Button) view.findViewById(R.id.declineButton);
            incomingCallPanel.setVisibility(View.VISIBLE);
            inComingVideoCallRemoteUser.setText(inComingvideoArguments.getString(SinchService.INCOMING_CALL_USER));

            answerButton.setOnClickListener(this);
            declineButton.setOnClickListener(this);

        }
        hangupButton.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        if (v == null) return;

        final int viewId = v.getId();
        switch (viewId) {
            case R.id.hangupButton:
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

    @Override
    public void onCallEnded(Call call) {
        CallEndCause cause = call.getDetails().getEndCause();
        Log.d(TAG, "Call ended. Reason: " + cause.toString());
        SinchUtil.getSinchAudioPlayer().stopProgressTone();
        mActivity.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        String endMsg = "Call ended: " + call.getDetails().toString();
        Toast.makeText(mActivity, endMsg, Toast.LENGTH_LONG).show();
        mCallId = call.getCallId();

        endCall();
    }

    @Override
    public void onCallEstablished(Call call) {
        Log.d(TAG, "Call established");
        SinchUtil.getSinchAudioPlayer().stopProgressTone();
        mActivity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        AudioController audioController = SinchUtil.getSinchServiceInterface().getAudioController();
        audioController.enableSpeaker();
        Log.d(TAG, "Call offered video: " + call.getDetails().isVideoOffered());
        mCallId = call.getCallId();
    }

    @Override
    public void onCallProgressing(Call call) {
        Log.d(TAG, "Call progressing");
        SinchUtil.getSinchAudioPlayer().playProgressTone();
        mCallId = call.getCallId();
    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        // Send a push through your push provider here, e.g. GCM
    }

    @Override
    public void onVideoTrackAdded(Call call) {
        Log.d(TAG, "Video track added");
        mCallId = call.getCallId();
        addVideoViews();
    }

    private void addVideoViews() {
        if (mVideoViewsAdded || SinchUtil.getSinchServiceInterface() == null) {
            return; //early
        }
        topPanel.setVisibility(View.VISIBLE);
        bootomPanel.setVisibility(View.VISIBLE);
        cameraPanel.setVisibility(View.VISIBLE);
        progressPanel.setVisibility(View.GONE);
        final VideoController vc = SinchUtil.getSinchServiceInterface().getVideoController();
        if (vc != null) {
            localVideoView.addView(vc.getLocalView());
            localVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vc.toggleCaptureDevicePosition();
                }
            });
            remoteVideoView.addView(vc.getRemoteView());
            mVideoViewsAdded = true;
        }
    }

    private void removeVideoViews() {
        if (SinchUtil.getSinchServiceInterface() == null) {
            return; // early
        }
        VideoController vc = SinchUtil.getSinchServiceInterface().getVideoController();
        if (vc != null) {
            remoteVideoView.removeView(vc.getRemoteView());
            localVideoView.removeView(vc.getLocalView());
            mVideoViewsAdded = false;
        }
    }

    private void updateUI() {
        if (SinchUtil.getSinchServiceInterface() == null) {
            return; // early
        }

        Call call = SinchUtil.getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            if (call.getState() == CallState.ESTABLISHED) {
                addVideoViews();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        removeVideoViews();
    }

    private void endCall() {
        SinchUtil.getSinchAudioPlayer().stopProgressTone();
        SinchUtil.getSinchAudioPlayer().stopRingtone();
        Call call = SinchUtil.getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        mActivity.finish();
    }

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

    private void declineClicked() {
        SinchUtil.getSinchAudioPlayer().stopRingtone();
        Call call = SinchUtil.getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        mActivity.finish();
    }
}
