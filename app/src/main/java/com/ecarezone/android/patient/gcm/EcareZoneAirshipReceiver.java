/* Copyright 2016 Urban Airship and Contributors */

package com.ecarezone.android.patient.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ecarezone.android.patient.ChatActivity;
import com.ecarezone.android.patient.NewsListActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.fragment.NewsCategoriesFragment;
import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.database.DoctorProfileDbApi;
import com.sinch.android.rtc.messaging.Message;
import com.urbanairship.AirshipReceiver;
import com.urbanairship.push.PushMessage;

public class EcareZoneAirshipReceiver extends AirshipReceiver {

    private static final String TAG = "UrbanAirshipReceiver";
    
    /**
     * Intent action sent as a local broadcast to update the channel.
     */
    public static final String ACTION_UPDATE_CHANNEL = "ACTION_UPDATE_CHANNEL";

    @Override
    protected void onChannelCreated(@NonNull Context context, @NonNull String channelId) {
        Log.i(TAG, "Channel created. Channel Id:" + channelId + ".");

        // Broadcast that the channel was created. Used to refresh the channel ID on the home fragment
//        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_UPDATE_CHANNEL));
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(Constants.UA_CHANNEL_NUMBER, channelId).apply();
    }

    @Override
    protected void onChannelUpdated(@NonNull Context context, @NonNull String channelId) {
        Log.i(TAG, "Channel updated. Channel Id:" + channelId + ".");

        // Broadcast that the channel was updated. Used to refresh the channel ID on the home fragment
//        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_UPDATE_CHANNEL));
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(Constants.UA_CHANNEL_NUMBER, channelId).apply();
    }

    @Override
    protected void onChannelRegistrationFailed(Context context) {
        Log.i(TAG, "Channel registration failed.");
    }

    @Override
    protected void onPushReceived(@NonNull Context context, @NonNull PushMessage message, boolean notificationPosted) {
        Log.i(TAG, "Received push message. Alert: " + message.getAlert() + ". posted notification: " + notificationPosted);

        if(message != null && !message.getAlert().isEmpty()){
            if(message.getAlert().startsWith("News")) {
                Intent intent = new Intent(Constants.PUSH_NEWS_UPDATE);
                intent.putExtra(Constants.NEWS_MESSAGE, message.getAlert());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                showNotification(context, message.getAlert());

            }
            else if(message.getAlert().startsWith("Doctor")){
                Intent intent = new Intent(Constants.BROADCAST_STATUS_CHANGED);
                intent.putExtra(Constants.SET_STATUS, message.getAlert());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }
    }

    private void showNotification(Context context, String message) {
        int notifyID = 1;

        String[] category = message.split(",");

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("ECareZone News")
                .setContentText("There is a news update on "+category[1])
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setColor(Color.BLUE)
                .setAutoCancel(true);


        Intent resultIntent = new Intent(context, NewsListActivity.class);
        if (resultIntent != null) {
            resultIntent.putExtra(NewsCategoriesFragment.NEWS_CATEGORY_NAME, category[1]);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mNotifyBuilder.setContentIntent(pendingIntent);
        }
        Notification notification = mNotifyBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyID, notification);

    }

    @Override
    protected void onNotificationPosted(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        Log.i(TAG, "Notification posted. Alert: " + notificationInfo.getMessage().getAlert() + ". NotificationId: " + notificationInfo.getNotificationId());
    }

    @Override
    protected boolean onNotificationOpened(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        Log.i(TAG, "Notification opened. Alert: " + notificationInfo.getMessage().getAlert() + ". NotificationId: " + notificationInfo.getNotificationId());

        // Return false here to allow Urban Airship to auto launch the launcher activity
        return false;
    }

    @Override
    protected boolean onNotificationOpened(@NonNull Context context, @NonNull NotificationInfo notificationInfo, @NonNull ActionButtonInfo actionButtonInfo) {
        Log.i(TAG, "Notification action button opened. Button ID: " + actionButtonInfo.getButtonId() + ". NotificationId: " + notificationInfo.getNotificationId());

        // Return false here to allow Urban Airship to auto launch the launcher
        // activity for foreground notification action buttons
        return false;
    }

    @Override
    protected void onNotificationDismissed(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        Log.i(TAG, "Notification dismissed. Alert: " + notificationInfo.getMessage().getAlert() + ". Notification ID: " + notificationInfo.getNotificationId());
    }
}
