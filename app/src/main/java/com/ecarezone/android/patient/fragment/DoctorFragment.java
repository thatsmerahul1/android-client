package com.ecarezone.android.patient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.ecarezone.android.patient.R;


/**
 * Created by CHAO WEI on 6/1/2015.
 */
public class DoctorFragment extends EcareZoneBaseFragment implements View.OnClickListener {
    @Override
    protected String getCallerName() {
        return DoctorFragment.class.getSimpleName();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_doctor, container, false);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == null) return;
/*
        final int viewId = v.getId();
        if(viewId == R.id.image_view_chat) {
            final Activity activity = getActivity();
            // open new activity
            if(activity != null) {
                activity.startActivity(new Intent(activity.getApplicationContext(), ChatActivity.class));
            }
        }
        */
    }
}
