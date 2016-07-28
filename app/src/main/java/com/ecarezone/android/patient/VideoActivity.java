package com.ecarezone.android.patient;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.fragment.VideoFragment;
import com.ecarezone.android.patient.service.SinchService;
import com.ecarezone.android.patient.utils.PermissionUtil;
import com.ecarezone.android.patient.utils.SinchUtil;
import com.sinch.android.rtc.calling.Call;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L&T Technology Services.
 */
public class VideoActivity extends EcareZoneBaseActivity {
    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;
    private VideoFragment videoFragment;
    private String mCallId;
    private String TAG = VideoActivity.class.getName();
    private ActionBar actionbar;
    String userName = "ecareuser@mail.com";
    Map<String, Integer> perms = new HashMap<>();

    @Override
    protected String getCallerName() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        videoFragment = new VideoFragment();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video);
        Bundle data = getIntent().getExtras();
        onNavigationChanged(R.layout.frag_video, ((data == null) ? null : data));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionbar = getSupportActionBar();
        actionbar.setTitle("");
        addSupportOnBackStackChangedListener(this);
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        Log.i(TAG,"mCallID::"+mCallId);
        SinchUtil.setSinchAudioPlayer(this);
        if (mCallId == null) {
            if (PermissionUtil.isPermissionRequired()
                    && PermissionUtil.getAllpermissionRequired(this, PermissionUtil.SINCH_PERMISSIONS).length > 0) {
                PermissionUtil.setAllPermission(this, PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                        , PermissionUtil.SINCH_PERMISSIONS);
            } else {
                userName = getIntent().getStringExtra(Constants.EXTRA_EMAIL);
                establishOutGoingVideoCall(userName);
            }
        } else {
            if (PermissionUtil.isPermissionRequired()
                    && PermissionUtil.getAllpermissionRequired(this, PermissionUtil.SINCH_PERMISSIONS).length > 0) {
                PermissionUtil.setAllPermission(this,
                        PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, PermissionUtil.SINCH_PERMISSIONS);
            } else {
                establishIncomingVideoCall(mCallId);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideActionbar() {
        actionbar.hide();
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.frag_video) {
            changeFragment(R.id.screen_container, videoFragment,
                    VideoFragment.class.getSimpleName(), args);
        }
    }

    /*Sinch service connected call back from base class*/
    @Override
    public void onServiceConnected() {

        Log.i(getCallerName(), "mCallId::" + mCallId);
        Call call = SinchUtil.getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(videoFragment);
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }

    /*Method to establish an outgoing call*/
    private void establishOutGoingVideoCall(String userName) {
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }
        Call call = SinchUtil.getSinchServiceInterface().callUserVideo(userName);
        String callId = call.getCallId();
        mCallId = call.getCallId();
        Log.i(getCallerName(), " establishOutGoingVideoCall mCallId::" + mCallId);
    }

    /*Method to establish an incoming call*/
    private void establishIncomingVideoCall(String callId) {
        SinchUtil.getSinchAudioPlayer().playRingtone();
    }

    /*Permission callback for checking all required permissions are granted or not*/
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
                        establishOutGoingVideoCall(userName);
                    } else {
                        establishIncomingVideoCall(mCallId);
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
    protected void onStop() {
        super.onStop();
        SinchUtil.getSinchAudioPlayer().stopProgressTone();
        SinchUtil.getSinchAudioPlayer().stopRingtone();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
