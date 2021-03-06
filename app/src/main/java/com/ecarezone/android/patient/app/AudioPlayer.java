package com.ecarezone.android.patient.app;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.ecarezone.android.patient.R;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by L&T Technology Services.
 */
public class AudioPlayer {

    static final String LOG_TAG = AudioPlayer.class.getSimpleName();

    private Context mContext;

    private MediaPlayer mPlayer;

    private AudioTrack mProgressTone;
    private AudioManager audioManager;
        float VOLUME = 1.0f;
    private final static int SAMPLE_RATE = 16000;

    public AudioPlayer(Context context) {
        this.mContext = context.getApplicationContext();
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public void playRingtone() {


        // Honour silent mode

        AudioManager audioManager = (AudioManager)mContext.getSystemService(mContext.AUDIO_SERVICE);
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        Log.e("Volume", "currVolume::" + currVolume);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        Log.e("Volume", "maxVolume::" + maxVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, maxVolume, AudioManager.FLAG_PLAY_SOUND);
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Log.e("Volume", "ringtone::" + ringtone.getPath());
        try {
           mPlayer.setDataSource(mContext, ringtone);
            mPlayer.prepare();
        } catch (IOException e) {
            Log.e("Volume", "Could not setup media player for ringtone");
            mPlayer = null;
            return;
        }
        mPlayer.setLooping(true);
        mPlayer.setVolume(VOLUME, VOLUME);
        mPlayer.start();
        int Vol = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        Log.e("Volume", Vol + "");
    }




    public void stopRingtone() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void playProgressTone() {
        stopProgressTone();
        try {
            mProgressTone = createProgressTone(mContext);
            mProgressTone.play();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not play progress tone", e);
        }
    }

    public void stopProgressTone() {
        if (mProgressTone != null) {
            mProgressTone.stop();
            mProgressTone.release();
            mProgressTone = null;
        }
    }

    private static AudioTrack createProgressTone(Context context) throws IOException {
        AssetFileDescriptor fd = context.getResources().openRawResourceFd(R.raw.progress_tone);
        int length = (int) fd.getLength();

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STATIC);

        byte[] data = new byte[length];
        readFileToBytes(fd, data);

        audioTrack.write(data, 0, data.length);
        audioTrack.setLoopPoints(0, data.length / 2, 30);

        return audioTrack;
    }

    private static void readFileToBytes(AssetFileDescriptor fd, byte[] data) throws IOException {
        FileInputStream inputStream = fd.createInputStream();

        int bytesRead = 0;
        while (bytesRead < data.length) {
            int res = inputStream.read(data, bytesRead, (data.length - bytesRead));
            if (res == -1) {
                break;
            }
            bytesRead += res;
        }
    }


}
