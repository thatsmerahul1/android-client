package com.ecarezone.android.patient.fragment.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.fragment.DoctorFragment;

/**
 * Created by 10603675 on 23-06-2016.
 */
public class EditAppointmentDialog extends DialogFragment implements View.OnClickListener {
    private String doctorName;
    private String callType;
    private String dateTime;
    private boolean isAppointmentAvailable;
    private boolean isTimeToCall;
    private TextView textViewInfo;
    private TextView textViewTime;
    private static DoctorFragment.OnAppointmentOptionButtonClickListener mOptionButtonClickListener;
    private static int typeOfCall;

    public EditAppointmentDialog() {
    }

    /**
     * Create a new instance of EditAppointmentDialog, providing "num"
     * as an argument.
     */
    public static EditAppointmentDialog newInstance(
            DoctorFragment.OnAppointmentOptionButtonClickListener optionButtonClickListener, int callType) {

        mOptionButtonClickListener = optionButtonClickListener;
        typeOfCall = callType;
        EditAppointmentDialog fragment = new EditAppointmentDialog();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(350, 350);

        isAppointmentAvailable = getArguments().getBoolean("isAppointmentAvailable", false);
        isTimeToCall = getArguments().getBoolean("isTimeToCall", false);
        doctorName = getArguments().getString("doctor_name");
        callType = getArguments().getString("callType");
        dateTime = getArguments().getString("dateTime");

        final View view = inflater.inflate(R.layout.edit_appointment_dialog_layout, container, false);
        textViewInfo = (TextView) view.findViewById(R.id.textViewInfo);

        textViewTime = (TextView) view.findViewById(R.id.textViewTime);
        textViewTime.setText(
                String.format(getResources().getString(R.string.doctor_add_request_success), doctorName));

        Button btnTimeToCall = (Button) view.findViewById(R.id.buttonTimeToCall);
        btnTimeToCall.setOnClickListener(this);

        Button btnChangeTime = (Button) view.findViewById(R.id.buttonChangeTime);
        btnChangeTime.setOnClickListener(this);

        Button btnCancel = (Button) view.findViewById(R.id.buttonCancel);
        btnCancel.setOnClickListener(this);

        if (!isAppointmentAvailable) {

            textViewTime.setVisibility(View.INVISIBLE);
            btnTimeToCall.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);

            String infoText = getString(R.string.sorry)+", Dr."+doctorName+"\n"+getString(R.string.is_not_available_right_now);
            textViewInfo.setText(infoText);

            btnChangeTime.setText(R.string.doctor_appointment);
            btnChangeTime.setOnClickListener(this);
        } else {

            String infoText = getString(R.string.your)+ " "+
                    callType.toLowerCase()+ " "+getString(R.string.appointment_with)+ " Dr."+doctorName+
                    " "+getString(R.string.has_been_booked);
            textViewInfo.setText(infoText);

            textViewTime.setText(dateTime);

            if (isTimeToCall) {
                btnTimeToCall.setEnabled(true);
                btnCancel.setEnabled(true);

                btnChangeTime.setEnabled(false);
                btnChangeTime.setBackgroundResource(R.drawable.circle_gray_complete);

                btnCancel.setEnabled(false);
                btnCancel.setBackgroundResource(R.drawable.circle_gray_complete);

                btnTimeToCall.setOnClickListener(this);
            } else {

                btnTimeToCall.setEnabled(false);
                btnTimeToCall.setBackgroundResource(R.drawable.circle_gray_complete);

                btnCancel.setEnabled(true);

                btnChangeTime.setEnabled(true);
                btnCancel.setEnabled(true);
            }
        }

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonTimeToCall:
                mOptionButtonClickListener.onButtonClicked(
                        DoctorFragment.OnAppointmentOptionButtonClickListener.BTN_TIME_TO_CALL, typeOfCall);
                break;

            case R.id.buttonChangeTime:
                if (!isAppointmentAvailable) {
                    mOptionButtonClickListener.onButtonClicked(
                            DoctorFragment.OnAppointmentOptionButtonClickListener.BTN_MAKE_AN_APPOINTMENT, typeOfCall);
                } else {
                    mOptionButtonClickListener.onButtonClicked(
                            DoctorFragment.OnAppointmentOptionButtonClickListener.BTN_CHANGE_TIME, typeOfCall);
                }
                break;

            case R.id.buttonCancel:
                mOptionButtonClickListener.onButtonClicked(
                        DoctorFragment.OnAppointmentOptionButtonClickListener.BTN_CANCEL, typeOfCall);
                break;

        }
        dismiss();

    }
}
