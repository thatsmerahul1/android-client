package com.ecarezone.android.patient.fragment.dialog;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ecarezone.android.patient.R;

/**
 * Created by L&T Technology Services on 04-03-2016.
 */

public class AddDoctorRequestDialog extends DialogFragment {

    private String doctorName;
    private TextView addDoctorSuccessTextView;
    private Button btn;

    public AddDoctorRequestDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(350, 350);

        doctorName = getArguments().getString("doctor_name");

        final View view = inflater.inflate(R.layout.add_doctor_successful_dialog, container, false);
        addDoctorSuccessTextView = (TextView) view.findViewById(R.id.add_doctor_success_text);
        addDoctorSuccessTextView.setText(String.format(getResources().getString(R.string.doctor_add_request_success), doctorName));
        btn = (Button)view.findViewById(R.id.ok_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
             }
        });
        return view;
    }
}
