package com.ecarezone.android.patient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.R;

/**
 * Created by CHAO WEI on 5/12/2015.
 */
public class PatientFragment extends EcareZoneBaseFragment implements View.OnClickListener {
    @Override
    protected String getCallerName() {
        return PatientFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_patient_main, container, false);
        ((TextView) view.findViewById(R.id.text_view_home_message_value)).setText(String.format(getString(R.string.home_new_messages), getString(R.string.doctor_name1)));

        LinearLayout newsFeedLayout = (LinearLayout) view.findViewById(R.id.newsFeedLayout);
        newsFeedLayout.setOnClickListener(this);

        LinearLayout chatAlertLayout = (LinearLayout) view.findViewById(R.id.chatAlertLayout);
        chatAlertLayout.setOnClickListener(this);

        LinearLayout recommendedDoctorLayout = (LinearLayout) view.findViewById(R.id.recommendedDoctorLayout);
        //TODO hide or show this recommended doctor layout based on data.
        //recommendedDoctorLayout.setVisibility(View.GONE);
        Button viewDoctorProfileButton= (Button) view.findViewById(R.id.viewDoctorProfile);
        viewDoctorProfileButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newsFeedLayout:
                ((MainActivity) getActivity()).onNavigationChanged(R.layout.frag_news_categories, null);
                break;
            case R.id.chatAlertLayout:
                Toast.makeText(getActivity(), "No chat available",Toast.LENGTH_LONG).show();
                break;
            case R.id.viewDoctorProfile:
                // TODO call the doctor profile activity.
                break;
        }
    }
}
