package com.ecarezone.android.patient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecarezone.android.patient.R;

/**
 * Created by CHAO WEI on 5/12/2015.
 */
public class PatientFragment extends EcareZoneBaseFragment {
    @Override
    protected String getCallerName() {
        return PatientFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_patient_main, container, false);
        ((TextView) view.findViewById(R.id.text_view_home_message_value)).setText(String.format(getString(R.string.home_new_messages), getString(R.string.doctor_name1)));
        return view;
    }

}
