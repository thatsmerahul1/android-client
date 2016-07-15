package com.ecarezone.android.patient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.fragment.DoctorListFragment;
import com.ecarezone.android.patient.fragment.FirstTimeUserProfileFragment;
import com.ecarezone.android.patient.fragment.NewsCategoriesFragment;
import com.ecarezone.android.patient.fragment.PatientFragment;
import com.ecarezone.android.patient.fragment.SettingsFragment;
import com.ecarezone.android.patient.fragment.UserProfileFragment;
import com.ecarezone.android.patient.fragment.WelcomeFragment;
import com.ecarezone.android.patient.gcm.HeartBeatReceiver;
import com.ecarezone.android.patient.gcm.HeartbeatService;
import com.ecarezone.android.patient.model.Appointment;
import com.ecarezone.android.patient.model.database.AppointmentDbApi;
import com.ecarezone.android.patient.model.database.ProfileDbApi;
import com.ecarezone.android.patient.model.rest.GetAllAppointmentRequest;
import com.ecarezone.android.patient.model.rest.GetAllAppointmentResponse;
import com.ecarezone.android.patient.model.rest.ValidateAppointmentRequest;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.service.FetchAppointmentService;
import com.ecarezone.android.patient.utils.Util;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.urbanairship.UAirship;

import java.util.HashMap;

import java.util.Calendar;
import java.util.List;

/**
 * Created by CHAO WEI on 5/3/2015.
 */
public class MainActivity extends EcareZoneBaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout = null;
    private ActionBarDrawerToggle mDrawerToggle = null;
    private Toolbar mToolBar = null;
    private ActionBar mActionBar = null;
    private boolean isBackStackRequired;
    private boolean isWelcomeMainRequired;
    private PatientFragment mPatientFragment;
    int status = 1;

    public static final long DISCONNECT_TIMEOUT = 60000; // 1 min = 1 * 60 * 1000 ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        UAirship.takeOff(getApplication(), new UAirship.OnReadyCallback() {

            @Override
            public void onAirshipReady(UAirship uAirship) {
                uAirship.getPushManager().setUserNotificationsEnabled(true);
            }

        });

        if (mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.side_drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu();
                    syncState();
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                    syncState();
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerLayout.setFitsSystemWindows(true);
            mDrawerToggle.syncState();
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
            });
        }

        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.ic_action_menu);
        }
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(Constants.ECARE_ZONE);

        if(getIntent().getBooleanExtra("from_login_screen", false)){
            // when a user logs into a different device.
            // needed to fetch all his/her existing appointments and populate the DB
            FetchAppointmentService.startActionFetchAppointment(getApplicationContext());
        }

        /* queries the db and checks whether to show welcome screen or to show home screen.
           Check is based on whether the user has created a profile or not. */
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
                if (profileDbApi != null) {
                    boolean hasProfiles = profileDbApi.hasProfile(LoginInfo.userId.toString());
                    return hasProfiles;
                } else {
                    return false;
                }
            }

            @Override
            protected void onCancelled(Boolean aBoolean) {
                super.onCancelled(aBoolean);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    onNavigationChanged(R.layout.frag_patient_main, null);
                } else {
                    onNavigationChanged(R.layout.frag_welcome, null);
                }
                isWelcomeMainRequired = aBoolean;
                super.onPostExecute(aBoolean);
            }
        }.execute();
//        disconnectHandler.post(disconnectCallback);


        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        Constants.deviceUnique = sharedPreferences.getString(Constants.UA_CHANNEL_NUMBER, Constants.deviceUnique);

        initStatus();
        setStatusAlarm();
        Util.setAppointmentAlarm(this);
    }

    private void initStatus() {
        PatientApplication doctorApplication = (PatientApplication) getApplicationContext();
        doctorApplication.setLastAvailabilityStatus(1);
        HashMap<String, Boolean> statusMap = doctorApplication.getNameValuePair();
        statusMap.put(Constants.STATUS_CHANGE, true);
        doctorApplication.setStatusNameValuePair(statusMap);

        Intent intent = new Intent(this, HeartbeatService.class);
        intent.putExtra(Constants.UPDATE_STATUS, true);
        startService(intent);
    }

    private void setStatusAlarm() {

        try {
            Intent intent = new Intent(this, HeartBeatReceiver.class);
            intent.putExtra(Constants.SEND_HEART_BEAT, true);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                    Constants.UPDATE_STATUS_REQ_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mAlarmManager.setInexactRepeating(AlarmManager.RTC,
                    SystemClock.elapsedRealtime(),
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES / 3, pendingIntent);
            Log.i("HeartbeatService", "Heartbeat alarm started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    public void toggleDrawer(boolean open) {
        if (mDrawerLayout != null) {
            if (open) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            } else {
                mDrawerLayout.closeDrawers();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((mDrawerToggle != null) && (mDrawerToggle.onOptionsItemSelected(item))) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawers();
                return;
            }
        }
        if (isBackStackRequired) {
            onNavigationChanged(isWelcomeMainRequired ? R.layout.frag_patient_main : R.layout.frag_welcome, null);
            isBackStackRequired = false;
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected String getCallerName() {
        return MainActivity.class.getSimpleName();
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        Log.d(TAG, "onNavigationChanged " + fragmentLayoutResId);
        if (fragmentLayoutResId < 0) return;
        if (fragmentLayoutResId == R.layout.frag_patient_main) {
            if(mPatientFragment == null) {
                mPatientFragment = PatientFragment.getNewInstance();
            }
            changeFragment(R.id.screen_container, mPatientFragment,
                    getString(R.string.main_side_menu_home), args, false);
            isBackStackRequired = false;
            isWelcomeMainRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_welcome) {
            changeFragment(R.id.screen_container, new WelcomeFragment(),
                    WelcomeFragment.class.getSimpleName(), args, false);
            isBackStackRequired = false;
        } else if (fragmentLayoutResId == R.layout.frag_news_categories) {
            changeFragment(R.id.screen_container, new NewsCategoriesFragment(),
                    getString(R.string.main_side_menu_news), args, false);
            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_doctor_list) {
            changeFragment(R.id.screen_container, new DoctorListFragment(),
                    getString(R.string.main_side_menu_doctors), args, false);
            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_settings) {
            changeFragment(R.id.screen_container, new SettingsFragment(),
                    getString(R.string.main_side_menu_settings), args, false);
            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.list_view) {
            changeFragment(R.id.screen_container, new UserProfileFragment(),
                    UserProfileFragment.class.getSimpleName(), args, false);
            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_first_time_profile) {
            changeFragment(R.id.screen_container, new FirstTimeUserProfileFragment(),
                    FirstTimeUserProfileFragment.class.getSimpleName(), args, false);
            isBackStackRequired = true;
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onServiceConnected() {

    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /********************
     * FETCH ALL APPOINTMENTS
     **********************/
    private void getAllAppointments() {

        GetAllAppointmentRequest request = new GetAllAppointmentRequest(LoginInfo.userId);
        getSpiceManager().execute(request, new GetAllAppointmentRequestListener());

    }

    private class GetAllAppointmentRequestListener implements RequestListener<GetAllAppointmentResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

        @Override
        public void onRequestSuccess(GetAllAppointmentResponse baseResponse) {

            if (baseResponse != null) {
                if (baseResponse.status != null) {
                    if (baseResponse.status.code == 200 &&
                            baseResponse.status.message.equalsIgnoreCase("Retrieval of Appointments Done")) {
                        int appointmentSize = baseResponse.data.size();
                        AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
                        for (Appointment appointment : baseResponse.data) {
//                            appointmentDbApi.updateOrInsertAppointment(appointment);
                            appointmentDbApi.saveAppointment(appointment);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.changeStatus(true, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.changeStatus(false, this);
    }

}
