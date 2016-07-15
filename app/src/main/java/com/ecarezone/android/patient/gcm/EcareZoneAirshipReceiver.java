/* Copyright 2016 Urban Airship and Contributors */

package com.ecarezone.android.patient.gcm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ecarezone.android.patient.config.Constants;
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(Constants.UA_CHANNEL_NUMBER, channelId).apply();
    }

    @Override
    protected void onChannelUpdated(@NonNull Context context, @NonNull String channelId) {
        Log.i(TAG, "Channel updated. Channel Id:" + channelId + ".");

        // Broadcast that the channel was updated. Used to refresh the channel ID on the home fragment
//        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_UPDATE_CHANNEL));
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
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
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.PUSH_NEWS_UPDATE));
            }
            else if(message.getAlert().startsWith("Doctor")){
                Intent intent = new Intent(Constants.BROADCAST_STATUS_CHANGED);
                intent.putExtra(Constants.SET_STATUS, message.getAlert());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }
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
