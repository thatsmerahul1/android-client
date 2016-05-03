package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ecarezone.android.patient.AppointmentActivity;
import com.ecarezone.android.patient.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Created by L&T Technology Services.
 */
public class AppointmentFragment extends EcareZoneBaseFragment implements View.OnClickListener {
    private Activity mActivity;
    private RadioButton radioVideo, radioVoip;
    private Button btnAppointment;

    private TextView txtAppointmentTime;
    private TextView txtAppointmentDay;
    private TextView txtAppointmentMonth;

    private TextView txtErrorMsg;

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

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    private void getAllComponent(View view) {
        radioVideo = (RadioButton) view.findViewById(R.id.radioVideo);
        radioVoip = (RadioButton) view.findViewById(R.id.radioVoip);
        btnAppointment = (Button) view.findViewById(R.id.button_appointment);

        radioVideo.setOnClickListener(this);
        radioVoip.setOnClickListener(this);
        btnAppointment.setOnClickListener(this);

        txtAppointmentDay = (TextView) view.findViewById(R.id.appointment_day);
        txtAppointmentDay.setOnClickListener(this);

        txtAppointmentMonth = (TextView) view.findViewById(R.id.appointment_month);
        txtAppointmentMonth.setOnClickListener(this);

        txtAppointmentTime = (TextView) view.findViewById(R.id.appointment_time);
        txtAppointmentTime.setOnClickListener(this);

        txtErrorMsg = (TextView)view.findViewById(R.id.txtErrorMsg);

        Calendar mcurrentDate = Calendar.getInstance();
        final int day = mcurrentDate.get(Calendar.DATE);
        final int month = mcurrentDate.get(Calendar.MONTH);
        final int year = mcurrentDate.get(Calendar.YEAR);

        String dayStr = String.valueOf(day);
        dayStr += "(Today)";
        txtAppointmentDay.setText(dayStr);

        String monthString = new DateFormatSymbols().getMonths()[month];
        txtAppointmentMonth.setText(monthString);

//        String ampmStr = mcurrentDate.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
        String timeStr = mcurrentDate.get(Calendar.HOUR)+":"+ mcurrentDate.get(Calendar.MINUTE);
        txtAppointmentTime.setText(timeStr);
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
//                Toast.makeText(getActivity(), "button appointment", Toast.LENGTH_SHORT).show();
                break;
            case R.id.appointment_time:
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txtAppointmentTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

                break;
            case R.id.appointment_day:
                showDateTimePicker();
                break;
            case R.id.appointment_month:
                showDateTimePicker();
                break;

        }
    }

    private void showDateTimePicker(){
        Calendar mcurrentDate = Calendar.getInstance();
        final int day = mcurrentDate.get(Calendar.DATE);
        final int month = mcurrentDate.get(Calendar.MONTH);
        final int year = mcurrentDate.get(Calendar.YEAR);
        DatePickerDialog dPicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearSel, int monthOfYear, int dayOfMonth) {

                if(dayOfMonth < day){
                    txtErrorMsg.setText(getString(R.string.invalid_date));
                    return;
                }
                else{
                    txtErrorMsg.setText("");
                }

                String dayStr = String.valueOf(dayOfMonth);
                if(day == dayOfMonth && monthOfYear == month && year == yearSel){
                    dayStr += "(Today)";
                }
                else if(day+1 == dayOfMonth && monthOfYear == month && year == yearSel){
                    dayStr += "(Tomorrow)";
                }
                txtAppointmentDay.setText(dayStr);

                String monthString = new DateFormatSymbols().getMonths()[monthOfYear];
                txtAppointmentMonth.setText(monthString);
            }
        }, year, month, day);
        dPicker.show();
    }
}
