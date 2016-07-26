package com.ecarezone.android.patient.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.ecarezone.android.patient.R;

public class AppointmentAlarmReceiver extends BroadcastReceiver {
    public AppointmentAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equalsIgnoreCase("START_ALARM")) {
            String doctorName = intent.getStringExtra("doctor_name");
            String appointment_type = intent.getStringExtra("appointment_type");
            int docId = intent.getIntExtra("docId", 0);


            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getString(R.string.apointment_with_dr) + doctorName)
                    .setContentText(context.getString(R.string.you_have_an) +
                            appointment_type + " " + context.getString(R.string.apointment_with_dr) + doctorName)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setColor(Color.BLUE)
                    .setAutoCancel(true);

            Notification notification = mNotifyBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(docId, notification);
        }

    }
}
