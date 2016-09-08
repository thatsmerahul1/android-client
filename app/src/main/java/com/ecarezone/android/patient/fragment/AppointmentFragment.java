package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
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
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.fragment.dialog.EcareZoneAlertDialog;
import com.ecarezone.android.patient.model.Appointment;
import com.ecarezone.android.patient.model.database.AppointmentDbApi;
import com.ecarezone.android.patient.model.rest.BookAppointmentRequest;
import com.ecarezone.android.patient.model.rest.BookAppointmentResponse;
import com.ecarezone.android.patient.model.rest.LoginRequest;
import com.ecarezone.android.patient.model.rest.LoginResponse;
import com.ecarezone.android.patient.model.rest.RescheduleAppointmentRequest;
import com.ecarezone.android.patient.model.rest.base.BaseResponse;
import com.ecarezone.android.patient.utils.PasswordUtil;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.ecarezone.android.patient.utils.Util;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private TextView txtAppointmentYear;

    private int selectedDate, selectedMonth, selectedYear, selectedTimeHr, selectedTimeMin;
    private int selectedDateUpdated, selectedMonthUpdated, selectedYearUpdated, selectedTimeHrUpdated, selectedTimeMinUpdated;
    private long doctorId;
    private int typeOfCall;

    private TextView txtErrorMsg;
    private ProgressDialog progressDialog;
    private AppointmentFragment appointmentFragment;

    private Appointment mExistingAppointment;
    private boolean  appointmentExist = false;

    @Override
    protected String getCallerName() {
        return AppointmentFragment.class.getName().toString();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        doctorId = getArguments().getLong("doctorId", -1);
        typeOfCall = getArguments().getInt("typeOfCall", -1);
        Object obj = getArguments().getSerializable("currentAppointment");
        if(obj != null) {
            this.mExistingAppointment = (Appointment)obj;
            appointmentExist= true;
        }
        appointmentFragment = this;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_appointment, container, false);

        getAllComponent(view);

        ((AppointmentActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getText(R.string.doctor_appointment_header));
        Util.setAppointmentAlarm(getApplicationContext());
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

        if(getResources().getInteger(R.integer.video_call_value) == typeOfCall){
            radioVideo.setChecked(true);
        }
        else{
            radioVoip.setChecked(true);
        }

        radioVideo.setOnClickListener(this);
        radioVoip.setOnClickListener(this);
        btnAppointment.setOnClickListener(this);

        txtAppointmentDay = (TextView) view.findViewById(R.id.appointment_day);
        txtAppointmentDay.setOnClickListener(this);


        txtAppointmentYear = (TextView) view.findViewById(R.id.appointment_year);
        txtAppointmentYear.setOnClickListener(this);

        txtAppointmentMonth = (TextView) view.findViewById(R.id.appointment_month);
        txtAppointmentMonth.setOnClickListener(this);

        txtAppointmentTime = (TextView) view.findViewById(R.id.appointment_time);
        txtAppointmentTime.setOnClickListener(this);

        txtErrorMsg = (TextView) view.findViewById(R.id.txtErrorMsg);

        Calendar mcurrentDate = Calendar.getInstance();
        if(mExistingAppointment != null){
            String timeStamp = mExistingAppointment.getTimeStamp();
            long timeStampInLong = Util.getTimeInLongFormat(timeStamp);
            if(timeStampInLong > 0){
                mcurrentDate.setTimeInMillis(timeStampInLong);
            }
        }

        selectedDate = mcurrentDate.get(Calendar.DATE);
        selectedMonth = mcurrentDate.get(Calendar.MONTH);
        selectedYear = mcurrentDate.get(Calendar.YEAR);
        selectedDateUpdated = selectedDate;
        selectedMonthUpdated=selectedMonth;
        selectedYearUpdated = selectedYear;


        String dayStr = String.valueOf(selectedDate);

        Calendar mTodayDate = Calendar.getInstance();
        final int day = mTodayDate.get(Calendar.DATE);
        final int month = mTodayDate.get(Calendar.MONTH);
        final int year = mTodayDate.get(Calendar.YEAR);

        if(selectedDate == day && month == selectedMonth && year == selectedYear) {
            dayStr += "(Today)";
        }
        else if (day + 1 == day && month == selectedMonth && year == selectedYear) {
            dayStr += "(Tomorrow)";
        }
        txtAppointmentDay.setText(dayStr);

        String monthString = new DateFormatSymbols().getMonths()[selectedMonth];
        txtAppointmentMonth.setText(monthString);


        selectedTimeHr = mcurrentDate.get(Calendar.HOUR_OF_DAY);
        selectedTimeMin = mcurrentDate.get(Calendar.MINUTE);
        selectedTimeHrUpdated =selectedTimeHr;
        selectedTimeMinUpdated = selectedTimeMin;

        String timeStr = formattedTime(mcurrentDate);
        txtAppointmentTime.setText(timeStr);
    }

    private String formattedTime(Calendar mcurrentDate){
//        String ampmStr = mcurrentDate.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
        String minute = null;
        if(mcurrentDate.get(Calendar.MINUTE) < 10){
            minute = "0"+mcurrentDate.get(Calendar.MINUTE);
        }
        else{
            minute = String.valueOf(mcurrentDate.get(Calendar.MINUTE));
        }

        String hour = null;
        if(mcurrentDate.get(Calendar.HOUR_OF_DAY) < 10){
            hour = "0"+mcurrentDate.get(Calendar.HOUR_OF_DAY);
        }
        else{
            hour = String.valueOf(mcurrentDate.get(Calendar.HOUR_OF_DAY));
        }
        String timeStr = hour + ":" + minute + "("+getString(R.string.thirty_mins)+")";
        return timeStr;
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
//               Toast.makeText(getActivity(), "button appointment", Toast.LENGTH_SHORT).show();
//               date in YYYY-MM-DD format

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, selectedMonth);
                calendar.set(Calendar.DATE, selectedDate);
                calendar.set(Calendar.HOUR_OF_DAY, selectedTimeHr);
                calendar.set(Calendar.MINUTE, selectedTimeMin);

                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    txtErrorMsg.setText(R.string.appointment_cannot_be_set);
                    return;
                }
                if(appointmentExist)
                {

                    RescheduleAppointmentRequest request =
                            new RescheduleAppointmentRequest(LoginInfo.userName, LoginInfo.hashedPassword, Constants.API_KEY, Constants.deviceUnique,
                                    selectedYear + "-" + (selectedMonth+1) + "-" + selectedDate + " " + selectedTimeHr + ":" + selectedTimeMin,
                                    selectedYearUpdated + "-" + (selectedMonthUpdated+1) + "-" + selectedDateUpdated + " " + selectedTimeHrUpdated + ":" + selectedTimeMinUpdated,
                                    radioVideo.isChecked() ? "video" : "voice", doctorId);
                    //  final BookAppointmentRequest response = new BaseResponse();
                    progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), "Processing........");
                    getSpiceManager().execute(request, new BookAppointmentRequestListener());
                }
                else
                {
                    BookAppointmentRequest request =
                            new BookAppointmentRequest(LoginInfo.userName, LoginInfo.hashedPassword, Constants.API_KEY, Constants.deviceUnique,
                                    selectedYearUpdated + "-" + (selectedMonthUpdated+1) + "-" + selectedDateUpdated + " " + selectedTimeHrUpdated + ":" + selectedTimeMinUpdated,
                                    radioVideo.isChecked() ? "video" : "voice", doctorId);
            //        final BaseResponse response = new BaseResponse();
                    progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), "Processing........");
                    getSpiceManager().execute(request, new BookAppointmentRequestListener());
                }


                break;
            case R.id.appointment_time:
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        selectedTimeHrUpdated = selectedHour;
                        selectedTimeMinUpdated = selectedMinute;

                        Calendar mcurrentDate = Calendar.getInstance();
                        mcurrentDate.set(Calendar.HOUR_OF_DAY, selectedHour);
                        mcurrentDate.set(Calendar.MINUTE, selectedMinute);
                        String formattedDate = formattedTime(mcurrentDate);
                        txtAppointmentTime.setText(formattedDate);
                        txtErrorMsg.setText("");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

                break;
            case R.id.appointment_year:
                showDateTimePicker();
                break;
            case R.id.appointment_day:
                showDateTimePicker();
                break;
            case R.id.appointment_month:
                showDateTimePicker();
                break;

        }
    }

    private void showDateTimePicker() {
        Calendar mcurrentDate = Calendar.getInstance();
        final int day = mcurrentDate.get(Calendar.DATE);
        final int month = mcurrentDate.get(Calendar.MONTH);
        final int year = mcurrentDate.get(Calendar.YEAR);
        DatePickerDialog dPicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearSel, int monthOfYear, int dayOfMonth) {

                if (dayOfMonth < day) {
                    txtErrorMsg.setText(getString(R.string.invalid_date));
                    return;
                } else {
                    txtErrorMsg.setText("");
                }

                String dayStr = String.valueOf(dayOfMonth);
                if (day == dayOfMonth && monthOfYear == month && year == yearSel) {
                    dayStr += "(Today)";
                } else if (day + 1 == dayOfMonth && monthOfYear == month && year == yearSel) {
                    dayStr += "(Tomorrow)";
                }
                txtAppointmentDay.setText(dayStr);

                String monthString = new DateFormatSymbols().getMonths()[monthOfYear];
                txtAppointmentMonth.setText(monthString);

                txtAppointmentYear.setText(String.valueOf(yearSel));

                selectedDateUpdated = dayOfMonth;
                selectedMonthUpdated = monthOfYear;
                selectedYearUpdated = yearSel;
                txtErrorMsg.setText("");
            }
        }, year, month, day);
        dPicker.show();
    }

    /**
     *
     */
    private class BookAppointmentRequestListener implements RequestListener<BookAppointmentResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(getActivity(), spiceException.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(BookAppointmentResponse baseResponse) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (baseResponse.status.code == 409) {
                EcareZoneAlertDialog.showAlertDialog(getActivity(), "Alert",
                        getString(R.string.no_slots_open),
                        "OK");
                return;
            }

            if(baseResponse.data != null) {
//                Calendar calendar = Calendar.getInstance();
//                Date date = null;
////            2014-6-23 12:50
//                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
//                try {
//                    date = format.parse(baseResponse.data.dateTime);
//                    calendar.setTime(date);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                if (date == null) {
//                    calendar.set(Calendar.YEAR, selectedYear);
//                    calendar.set(Calendar.MONTH, selectedMonth);
//                    calendar.set(Calendar.DATE, selectedDate);
//                    calendar.set(Calendar.HOUR_OF_DAY, selectedTimeHr);
//                    calendar.set(Calendar.MINUTE, selectedTimeMin);
//                }
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(baseResponse.data.id);
                appointment.setCallType(baseResponse.data.callType);
                appointment.setTimeStamp(baseResponse.data.dateTime);
                appointment.setpatientId(String.valueOf(LoginInfo.userId));
                AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getActivity());
                if(appointmentExist)
                {
                    boolean isUpdated = appointmentDbApi.updateAppointment(baseResponse.data.id ,appointment);
                    if (isUpdated) {
                        Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                        mActivity.finish();
                        mActivity.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    } else {

                    }
                }
                else {
                    appointment.setDoctorId(String.valueOf(doctorId));
//                appointment.setTimeStamp(calendar.getTimeInMillis());



                    boolean isInserted = appointmentDbApi.saveAppointment(appointment);
                    if (isInserted) {
                        mActivity.finish();
                        mActivity.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    } else {

                    }
                }
            }
        }
    }
}
