package com.ecarezone.android.patient.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ecarezone.android.patient.config.Constants;

/**
 * Created by 10603675 on 27-06-2016.
 */
public class HeartBeatReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent heartBeatService = new Intent(context, HeartbeatService.class);
        heartBeatService.putExtra(Constants.SEND_HEART_BEAT, true);
        heartBeatService.putExtra(Constants.UPDATE_STATUS, true);
        context.startService(heartBeatService);
    }
}
