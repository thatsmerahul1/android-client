package com.ecarezone.android.patient.gcm;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ecarezone.android.patient.model.database.ChatDbApi;
import com.ecarezone.android.patient.service.SinchService;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.SinchHelpers;

/**
 * Created by L&T Technology Services.
 */
public class GcmIntentService extends IntentService implements ServiceConnection {

    private Intent mIntent;
    private String TAG = GcmIntentService.class.getName();

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (SinchHelpers.isSinchPushIntent(intent)) {
            mIntent = intent;
            connectToService();
        } else {
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    /*Start sinchservice when you will get push Notification*/
    private void connectToService() {
        getApplicationContext().bindService(new Intent(this, SinchService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (mIntent == null) {
            return;
        }

        if (SinchHelpers.isSinchPushIntent(mIntent)) {
            SinchService.SinchServiceInterface sinchService = (SinchService.SinchServiceInterface) iBinder;
            if (sinchService != null) {
                NotificationResult result = sinchService.relayRemotePushNotificationPayload(mIntent);
                if (result != null && result.isMessage() && sinchService.isMessageNotifcationRequired()) {
                    // handle result, e.g. show a notification or similar
                    sendBroadcast();

                }
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(mIntent);
        mIntent = null;
    }

    public void sendBroadcast() {
        Intent intent = new Intent("send");
        intent.putExtra("chatCount", ChatDbApi.getInstance(this).getUnReadChatCount());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.i(TAG, "Service disconnected");
    }

}