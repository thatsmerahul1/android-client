package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecarezone.android.patient.ProfileDetailsActivity;
import com.ecarezone.android.patient.R;

/**
 * Created by CHAO WEI on 5/1/2015.
 */
public class UserProfileFragment extends EcareZoneBaseFragment implements View.OnClickListener {

    @Override
    protected String getCallerName() {
        return UserProfileFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_user_profile, container, false);
        view.findViewById(R.id.layout_profile_mine).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == null) return;

        final int viewId = v.getId();
        if(viewId == R.id.layout_profile_mine) {
            final Activity activity = getActivity();
            // open new activity
            if(activity != null) {
                activity.startActivity(new Intent(activity.getApplicationContext(), ProfileDetailsActivity.class));
            }
        }
    }
}
