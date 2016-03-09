package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.RegistrationActivity;


public class SplashScreenFragment extends EcareZoneBaseFragment {

    public static SplashScreenFragment newInstance() {
        return  new SplashScreenFragment();
    }

    @Override
    protected String getCallerName() {
        return SplashScreenFragment.class.getSimpleName();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_splashscreen, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        performSplashTask();

    }


    private void performSplashTask () {
        final Activity activity = getActivity();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(activity != null) {
                    // we use SharedPreferences to show a indicator for checking
                    // if the user has logged in
                    SharedPreferences perPreferences = activity.getSharedPreferences("eCareZone", Activity.MODE_PRIVATE);
                    boolean is_login = perPreferences.getBoolean("is_login", false);

                    if (is_login) { // the current user is still in login status

                        Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        //activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
                        activity.startActivity(new Intent(activity.getApplicationContext(), RegistrationActivity.class));
                    }

                    activity.finish();

                }
            }
        }, 1500L);

    }
}
