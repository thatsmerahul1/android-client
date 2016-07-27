package com.ecarezone.android.patient.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.model.database.AppointmentDbApi;

import org.apache.commons.lang3.text.WordUtils;

public class AppointmentAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("Appointment alarm", "Heartbeat method called. ");
        if(intent.getAction().equalsIgnoreCase("START_ALARM")) {
            String doctorName = intent.getStringExtra("doctor_name");
            String appointment_type = intent.getStringExtra("appointment_type");
            int docId = intent.getIntExtra("docId", 0);

            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(WordUtils.capitalize(context.getString(R.string.appointment)))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setColor(Color.BLUE)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(appointment_type + " " + context.getString(R.string.apointment_with_dr) + doctorName));

            Notification notification = mNotifyBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(docId, notification);
        }
        else{
            Log.e("Appointment alarm", "Heartbeat else section");
        }

    }
}
