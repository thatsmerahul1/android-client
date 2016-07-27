package com.ecarezone.android.patient.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ecarezone.android.patient.PatientApplication;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.gcm.HeartbeatService;
import com.ecarezone.android.patient.model.Appointment;
import com.ecarezone.android.patient.model.AppointmentResponse;
import com.ecarezone.android.patient.model.database.AppointmentDbApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * Created by 10603675 on 04-06-2016.
 */
public class Util {

    private static long LAST_STATUS_CHANGE_TIME = System.currentTimeMillis();

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Sets the status change variable to true or false depending upon the status of the application.
     * It also starts a service that sends the status update to the server.
     *
     * @param status
     * @param context
     */
    public static void changeStatus(Integer status, Context context){
        if(System.currentTimeMillis() - LAST_STATUS_CHANGE_TIME > 2000) {
            LAST_STATUS_CHANGE_TIME = System.currentTimeMillis();
            PatientApplication globalVariable = (PatientApplication) context.getApplicationContext();
            if(globalVariable.getNameValuePair() != null) {
                if(globalVariable.getNameValuePair().containsKey(Constants.STATUS_CHANGE)) {
                    Integer lastState = globalVariable.getNameValuePair().get(Constants.STATUS_CHANGE);
                    if (status != lastState) {
                        globalVariable.getNameValuePair().put(Constants.STATUS_CHANGE, status);
                        Intent intent = new Intent(context, HeartbeatService.class);
                        intent.putExtra(Constants.UPDATE_STATUS, true);
                        context.startService(intent);
                    }
                }
                else{
                    globalVariable.getNameValuePair().put(Constants.STATUS_CHANGE, status);
                }
            }
        }
    }

    /**
     * coverts the date time from string to long format
     *
     * @param dateTime
     * @return date and time in long format
     */
    public static long getTimeInLongFormat(String dateTime) {
        long timeInLongFormat = -1;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        try {
            Date date = format.parse(dateTime);
            timeInLongFormat = date.getTime();
        } catch (ParseException e) {
            try {
                timeInLongFormat = Long.parseLong(dateTime);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        return timeInLongFormat;
    }

    /**
     *
     * @param dateTime
     * @return
     */
    public static String getTimeInStringFormat(long dateTime) {
        String timeInDateFormat  = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        try {
            timeInDateFormat = format.format(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeInDateFormat;
    }

    /**
     *
     * @param dateTime
     * @param format
     * @return
     */
    public static String getTimeInStringFormat(long dateTime, DateFormat format) {
        String timeInDateFormat  = null;
        try {
            timeInDateFormat = format.format(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeInDateFormat;
    }

    /**
     * refresh all the alarms that have been set for appointments.
     *
     * @param context activity context
     */
    public static void setAppointmentAlarm(Context context) {

        AppointmentDbApi appointmentDb = AppointmentDbApi.getInstance(context);
        List<Appointment> appointmentList = appointmentDb.getAppointmentsByPatientId(String.valueOf(LoginInfo.userId), true);
        int size = appointmentList.size();
        if (size > 0) {

            for (int i = 0; i < size; i++) {

                Appointment app = appointmentList.get(i);
                long dateInLong = Util.getTimeInLongFormat(app.getTimeStamp());
                if (dateInLong >= System.currentTimeMillis()) {

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    Intent appointmentIntent = new Intent(context, AppointmentAlarmReceiver.class);
                    appointmentIntent.putExtra("doctor_name", "");
                    appointmentIntent.putExtra("appointment_type", app.getCallType());
                    appointmentIntent.putExtra("docId", app.getDoctorId());
                    PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(context, (int) app.getAppointmentId(),
                            appointmentIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    // Cancel alarms
                    try {
                        alarmManager.cancel(pendingUpdateIntent);
                    } catch (Exception e) {
                        Log.e("Appointment alarm", "AlarmManager update was not cancelled. " + e.toString());
                    }

                    if(android.os.Build.VERSION.SDK_INT > 18) {
                        alarmManager.setWindow(AlarmManager.RTC_WAKEUP,
                                Util.getTimeInLongFormat(app.getTimeStamp()), 1000L, pendingUpdateIntent);
                    }
                    else{
                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                Util.getTimeInLongFormat(app.getTimeStamp()), pendingUpdateIntent);
                    }

                }
            }
        }
    }

    public static List<AppointmentResponse> getAppointmentResponseList(List<Appointment> appointmentList) {

        List<AppointmentResponse> appointmentResponseList = null;
        if (appointmentList != null) {

            appointmentResponseList = new ArrayList<>(appointmentList.size());
            ListIterator<Appointment> appointmentIterator = appointmentList.listIterator();
            while (appointmentIterator.hasNext()) {

                Appointment appointment = appointmentIterator.next();
                AppointmentResponse appointmentResponse = new AppointmentResponse();

                appointmentResponse.doctorId = appointment.getDoctorId();
                appointmentResponse.dateTime = appointment.getTimeStamp();
                appointmentResponse.id = (int) appointment.getAppointmentId();
                appointmentResponse.callType = appointment.getCallType();
                appointmentResponse.patientId = appointment.getpatientId();
                appointmentResponseList.add(appointmentResponse);
            }
        }
        return appointmentResponseList;
    }

    /**
     * reads the input stream and returns the content of the stream
     *
     * @param stream the stream to read the content from
     * @return the content of the stream in string format
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static String readDataFromInputStream(InputStream stream)
            throws IOException, UnsupportedEncodingException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream));
        char[] bytesRead = new char[1024];
        int read = -1;

        StringBuilder response = new StringBuilder();
        while (-1 != (read = reader.read(bytesRead))) {
            response.append(bytesRead, 0, read);
        }
        if (response.length() > 0) {
            Log.i("doctor_client", "Response from server -> " + response.toString());
        } else {
            Log.i("doctor_client", "No Data received from the server");
        }
        return response.toString();
    }
}
