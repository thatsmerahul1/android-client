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
import com.ecarezone.android.patient.model.Appointment;
import com.ecarezone.android.patient.model.database.AppointmentDbApi;
import com.ecarezone.android.patient.model.database.DoctorProfileDbApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by 10603675 on 04-06-2016.
 */
public class Util {

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Sets the status change variable to true or false depending upon the status of the application.
     * This variable is later used to send an update to the server every 5 minutes.
     * @param status
     * @param activity
     */
    public static void changeStatus(boolean status, Activity activity){
        if(status) {
            PatientApplication.nameValuePair.put(Constants.STATUS_CHANGE, true);
        } else {
            PatientApplication.nameValuePair.put(Constants.STATUS_CHANGE, false);
        }
    }

    /**
     * coverts the date time from string to long format
     * @param dateTime
     * @return date and time in long format
     */
    public static long getTimeInLongFormat(String dateTime){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        try {
            Date date = format.parse(dateTime);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * refresh all the alarms that have been set for appointments.
     * @param context activity context
     */
    public static void setAppointmentAlarm(Context context){

        AppointmentDbApi appointmentDb = AppointmentDbApi.getInstance(context);
        List<Appointment> appointmentList = appointmentDb.getAppointmentsByPatientId(String.valueOf(LoginInfo.userId), true);

        if (appointmentList.size() > 0) {

            for (int i = 0; i < appointmentList.size(); i++) {

                Appointment app = appointmentList.get(i);
                long dateInLong = Util.getTimeInLongFormat(app.getTimeStamp());
                if(dateInLong >= System.currentTimeMillis()) {

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    Intent appointmentIntent = new Intent(context, AppointmentAlarmReceiver.class);
                    appointmentIntent.putExtra("doctor_name", "");
                    appointmentIntent.putExtra("appointment_type", app.getCallType());
                    appointmentIntent.putExtra("docId", app.getDoctorId());
                    PendingIntent pendingUpdateIntent = PendingIntent.getService(context, 0, appointmentIntent, 0);

                    // Cancel alarms
                    try {
                        alarmManager.cancel(pendingUpdateIntent);
                    } catch (Exception e) {
                        Log.e("Appointment alarm", "AlarmManager update was not canceled. " + e.toString());
                    }

                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                            Util.getTimeInLongFormat(app.getTimeStamp()), pendingUpdateIntent);
                }
            }
        }

    }
}
