package com.ecarezone.android.patient;

import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.WindowManager;

import com.ecarezone.android.patient.fragment.CallFragment;
import com.ecarezone.android.patient.fragment.VideoFragment;
import com.ecarezone.android.patient.service.SinchService;
import com.ecarezone.android.patient.utils.PermissionUtil;
import com.ecarezone.android.patient.utils.SinchUtil;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L&T Technology Services.
 */
public class CallActivity extends EcareZoneBaseActivity {
    private String TAG = CallActivity.class.getName();
    private ActionBar actionbar;
    String userName = "ecareuser@mail.com";
    private String mCallId;
    private CallFragment callFragment;
    Map<String, Integer> perms = new HashMap<>();

    @Override
    protected String getCallerName() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        callFragment = new CallFragment();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_call);
        Bundle data = getIntent().getExtras();
        onNavigationChanged(R.layout.frag_call, ((data == null) ? null : data));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionbar = getSupportActionBar();
        actionbar.setTitle("");
        addSupportOnBackStackChangedListener(this);
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        SinchUtil.setSinchAudioPlayer(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (mCallId == null) {
            if (PermissionUtil.isPermissionRequired()
                    && PermissionUtil.getAllpermissionRequired(this, PermissionUtil.SINCH_PERMISSIONS).length > 0) {
                PermissionUtil.setAllPermission(this, PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                        , PermissionUtil.SINCH_PERMISSIONS);
            } else {
                establishOutgoingCall(userName);
            }
        } else {
            if (PermissionUtil.isPermissionRequired()
                    && PermissionUtil.getAllpermissionRequired(this, PermissionUtil.SINCH_PERMISSIONS).length > 0) {
                PermissionUtil.setAllPermission(this, PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                        , PermissionUtil.SINCH_PERMISSIONS);
            } else {
                establishIncomingCall(mCallId);
            }
        }
    }

    public void hideActionbar() {
        actionbar.hide();
    }

    private void establishOutgoingCall(String userName) {

        if (userName.isEmpty()) {
            return;
        }
        try {
            Call call = SinchUtil.getSinchServiceInterface().callUser(userName);
            if (call == null) {
                return;
            }
            mCallId = call.getCallId();
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }
    }

    private void establishIncomingCall(String callId) {
        SinchUtil.getSinchAudioPlayer().playRingtone();
    }

    @Override
    public void onServiceConnected() {
        Call call = SinchUtil.getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(callFragment);
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.frag_call) {
            changeFragment(R.id.screen_container, callFragment,
                    VideoFragment.class.getSimpleName(), args);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;
        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                for (int count = 0; count < perms.size(); count++) {
                    if (perms.get(permissions[count]).equals(PackageManager.PERMISSION_GRANTED)) {
                        // All Permissions Granted
                        permissionGranted = true;
                    } else {
                        permissionGranted = false;
                        break;
                    }
                }

                if (permissionGranted) {
                    if (mCallId == null) {
                        establishOutgoingCall(userName);
                    } else {
                        establishIncomingCall(mCallId);
                    }
                } else {
                    SinchUtil.getSinchAudioPlayer().stopRingtone();
                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SinchUtil.getSinchAudioPlayer().stopProgressTone();
        SinchUtil.getSinchAudioPlayer().stopRingtone();
    }
}
