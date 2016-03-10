package com.ecarezone.android.patient.utils;

import android.content.Context;

import com.ecarezone.android.patient.app.AudioPlayer;
import com.ecarezone.android.patient.service.SinchService;

/**
 * Created by L&T Technology Services on 2/17/2016.
 */
public class SinchUtil {
    private static SinchService.SinchServiceInterface mSinchServiceInterface = null;
    private static AudioPlayer audioplayer;

    public static SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    public static void setSinchServiceInterface(SinchService.SinchServiceInterface sinchServiceInterface) {
        mSinchServiceInterface = sinchServiceInterface;
    }

    public static AudioPlayer getSinchAudioPlayer() {
        return audioplayer;
    }

    public static void setSinchAudioPlayer(Context context) {
        audioplayer = new AudioPlayer(context);
    }
}
