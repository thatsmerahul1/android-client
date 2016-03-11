package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecarezone.android.patient.ProfileDetailsActivity;
import com.ecarezone.android.patient.R;

/**
 * Created by CHAO WEI on 5/31/2015.
 */
public class FirstTimeUserProfileFragment extends EcareZoneBaseFragment implements View.OnClickListener {
    public static int CREATE_PROFILE_REQUEST_CODE = 1000;

    @Override
    protected String getCallerName() {
        return FirstTimeUserProfileFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_first_time_profile, container, false);
        view.findViewById(R.id.layout_profile_add_mine).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        final int viewId = v.getId();
        if (viewId == R.id.layout_profile_add_mine) {
            final Activity activity = getActivity();
            if (activity != null) {
                // adding IS_NEW_PROFILE to let ProfileDetailsActivity to create new profile.
                startActivityForResult(new Intent(activity.getApplicationContext(), ProfileDetailsActivity.class)
                        .putExtra(ProfileDetailsActivity.IS_NEW_PROFILE, true), CREATE_PROFILE_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREATE_PROFILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // profile created. remove the first time profile fragment & show the profile list fragment
                //getActivity().getSupportFragmentManager().popBackStack();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(this);
                transaction.replace(R.id.screen_container, new UserProfileFragment());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                transaction.addToBackStack(UserProfileFragment.class.getSimpleName());
                transaction.commit();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
