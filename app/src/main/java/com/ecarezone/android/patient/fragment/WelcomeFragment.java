package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.R;

/**
 * Created by CHAO WEI on 5/1/2015.
 */
public class WelcomeFragment extends EcareZoneBaseFragment implements View.OnClickListener {

    @Override
    protected String getCallerName() {
        return WelcomeFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_welcome, container, false);
        view.findViewById(R.id.button_welcome_not_now).setOnClickListener(this);
        view.findViewById(R.id.button_welcome_ok).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        final int viewId = v.getId();
        if (viewId == R.id.button_welcome_not_now) {
            // open side menu
            Activity act = getActivity();
            if ((act != null) && (act instanceof MainActivity)) {
                ((MainActivity) act).toggleDrawer(true);
            }
        } else if (viewId == R.id.button_welcome_ok) {
            // to go to profile page
            invokeNavigationChanged(R.layout.list_view, null);
        }
    }
}
