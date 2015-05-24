package com.ecarezone.android.patient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecarezone.android.patient.R;

/**
 * Created by CHAO WEI on 5/1/2015.
 */
public class SettingsFragment extends EcareZoneBaseFragment {
    @Override
    protected String getCallerName() {
        return SettingsFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_settings, container, false);
        return view;
    }
}
