package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ecarezone.android.patient.AppointmentActivity;
import com.ecarezone.android.patient.R;

/**
 * Created by L&T Technology Services.
 */
public class AppointmentFragment extends EcareZoneBaseFragment implements View.OnClickListener {
    private Activity mActivity;
    private RadioButton radioVideo, radioVoip;
    private Button btnAppointment;

    @Override
    protected String getCallerName() {
        return AppointmentFragment.class.getName().toString();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_appointment, container, false);
        getAllComponent(view);

        ((AppointmentActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getText(R.string.doctor_appointment_header));
        return view;
    }

    private void getAllComponent(View view) {
        radioVideo = (RadioButton) view.findViewById(R.id.radioVideo);
        radioVoip = (RadioButton) view.findViewById(R.id.radioVoip);
        btnAppointment = (Button) view.findViewById(R.id.button_appointment);

        radioVideo.setOnClickListener(this);
        radioVoip.setOnClickListener(this);
        btnAppointment.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radioVideo:
                if (radioVideo.isChecked())
                    break;
            case R.id.radioVoip:
                if (radioVoip.isChecked())
                    break;
            case R.id.button_appointment:
                Toast.makeText(getActivity(), "button appointment", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
