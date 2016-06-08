package com.ecarezone.android.patient.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ecarezone.android.patient.CallActivity;
import com.ecarezone.android.patient.ChatActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.VideoActivity;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.model.Chat;
import com.ecarezone.android.patient.model.database.ChatDbApi;
import com.ecarezone.android.patient.utils.SinchUtil;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;
import com.sinch.android.rtc.video.VideoController;
import com.sinch.android.rtc.video.VideoScalingType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L&T Technology Services.
 */

public class SinchService extends Service {

    private static final String APP_KEY = "6b84d17d-1cd8-4570-b29a-3783a1ba76e9";
    private static final String APP_SECRET = "Qr+7+SDyUkmTYpCGicC96w==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    public static final String CALL_ID = "CALL_ID";
    public static final String INCOMING_CALL_USER = "INCOMING_CALL_USER";
    static final String TAG = SinchService.class.getSimpleName();

    private SinchServiceInterface mSinchServiceInterface = new SinchServiceInterface();
    private SinchClient mSinchClient;
    private String mUserId;
    private static ArrayList<MessageClientListener> messageClientList;

    private StartFailedListener mListener;
    private PersistedSettings mSettings;
    private int numMessages = 0;
    Intent resultIntent;
    private MySinchClientListener mSinchClientListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mSettings = new PersistedSettings(getApplicationContext());
        messageClientList = new ArrayList<MessageClientListener>();
    }

    @Override
    public void onDestroy() {
        if (mSinchClient != null && mSinchClient.isStarted()) {
            mSinchClient.terminate();
        }
        super.onDestroy();
    }

    /*Start the sinch client by passing username*/
    private void start(String userName) {
        if (mSinchClient == null) {
            mSettings.setUsername(userName);
            createClient(userName);
            mSinchClient.start();
        }
    }

    /*create sinch client by passing proper username which will be unique
    * @param username
    * */
    private void createClient(String userName) {

        SharedPreferences perPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        if(! perPreferences.getBoolean(Constants.IS_LOGIN, true)){
            return;
        }

        mUserId = userName;
        mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(userName)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT).build();

        mSinchClient.setSupportMessaging(true);
        mSinchClient.setSupportCalling(true);
        mSinchClient.startListeningOnActiveConnection();
        mSinchClient.setSupportManagedPush(true);

        mSinchClientListener = new MySinchClientListener();
        mSinchClient.addSinchClientListener(mSinchClientListener);
        mSinchClient.getCallClient().setRespectNativeCalls(false);
        mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        mSinchClient.getMessageClient().addMessageClientListener(new MessageListner());
        mSinchClient.getVideoController().setResizeBehaviour(VideoScalingType.ASPECT_FILL);
    }

    /* stop sinch client*/
    private void stop() {
        if (mSinchClient != null) {
            mSinchClient.stopListeningOnActiveConnection();;
            mSinchClient.removeSinchClientListener(mSinchClientListener);
//            mSinchClient.unregisterManagedPush();
//            mSinchClient.unregisterPushNotificationData();;
            mSinchClient.terminate();
            mSinchClient = null;
        }
    }

    /*Sinch started or not before you start the client again*/
    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    public void sendMessage(String recipientUserId, String textBody) {
        if (isStarted()) {
            Log.i("sendMessage", "isStarted::" + recipientUserId);
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            mSinchClient.getMessageClient().send(message);
        }
    }

    /*Listner to listen all the incoming and outgoing messages*/
    public void addMessageClientListener(MessageClientListener listener) {
        if (mSinchClient != null) {
            registerMessageListner(listener);
        }
    }

    /*Remove all the message listner before destroying your activity*/
    public void removeMessageClientListener(MessageClientListener listener) {
        if (mSinchClient != null) {
            unRegisterMessageListner(listener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSinchServiceInterface;
    }

    /*Sinch interface which will handel all the operation of sinch*/
    public class SinchServiceInterface extends Binder {

        public Call callUserVideo(String userId) {
            return mSinchClient.getCallClient().callUserVideo(userId);
        }

        public Call callUser(String userId) {
            if (mSinchClient == null) {
                return null;
            }
            return mSinchClient.getCallClient().callUser(userId);
        }

        public String getUserName() {
            return mUserId;
        }

        public boolean isStarted() {
            return SinchService.this.isStarted();
        }

        public void startClient(String userName) {
            start(userName);
        }

        public void stopClient() {
            stop();
        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public Call getCall(String callId) {
            return mSinchClient.getCallClient().getCall(callId);
        }

        public VideoController getVideoController() {
            if (!isStarted()) {
                return null;
            }
            return mSinchClient.getVideoController();
        }

        public AudioController getAudioController() {
            if (!isStarted()) {
                return null;
            }
            return mSinchClient.getAudioController();
        }

        public void sendMessage(String recipientUserId, String textBody) {
            Log.i(TAG, "sendMessage::" + recipientUserId);
            SinchService.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            SinchService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            SinchService.this.removeMessageClientListener(listener);
        }

        public NotificationResult relayRemotePushNotificationPayload(Intent intent) {
            if (mSinchClient == null && !mSettings.getUsername().isEmpty()) {
                createClient(mSettings.getUsername());
            }
            if (mSinchClient == null) {
                Log.e(TAG, "Can't start a SinchClient as no username is available, unable to relay push.");
                return null;
            }
            return mSinchClient.relayRemotePushNotificationPayload(intent);
        }

        public boolean isMessageNotifcationRequired() {
            if (messageClientList.size() == 0) {
                return true;
            } else {
                return false;
            }
        }

    }

    /*SInch client start and failed listner*/
    public interface StartFailedListener {

        void onStartFailed(SinchError error);

        void onStarted();
    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {
            if (mListener != null) {
                mListener.onStartFailed(error);
            }
            mSinchClient.terminate();
            mSinchClient = null;
        }

        @Override
        public void onClientStarted(SinchClient client) {
            Log.d(TAG, "SinchClient started");
            if (mListener != null) {
                mListener.onStarted();
            }
        }

        @Override
        public void onClientStopped(SinchClient client) {
            Log.d(TAG, "SinchClient stopped");
        }

        @Override
        public void onLogMessage(int level, String area, String message) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    break;
                case Log.ERROR:
                    Log.e(area, message);
                    break;
                case Log.INFO:
                    Log.i(area, message);
                    break;
                case Log.VERBOSE:
                    Log.v(area, message);
                    break;
                case Log.WARN:
                    Log.w(area, message);
                    break;
            }
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient client,
                                                      ClientRegistration clientRegistration) {
        }
    }

    /* Sinch call Listner for all incoming call*/
    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Log.d(TAG, "Incoming call");
            Intent intent = null;
            if (call.getDetails().isVideoOffered()) {
                intent = new Intent(SinchService.this, VideoActivity.class);
            } else {
                intent = new Intent(SinchService.this, CallActivity.class);
            }
            intent.putExtra(CALL_ID, call.getCallId());
            intent.putExtra(INCOMING_CALL_USER, call.getRemoteUserId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SinchService.this.startActivity(intent);
        }
    }

    /* Message Listner for all incoming and outgoing messages */
    private class MessageListner implements MessageClientListener {
        @Override
        public void onIncomingMessage(MessageClient messageClient, Message message) {

            Log.i("inside listner", "mesaage incoming");
            SinchUtil.saveIncomingChatHistory(message, getApplicationContext());
            if (mSinchServiceInterface.isMessageNotifcationRequired()) {
                showNotification(message);
            }
            for (MessageClientListener messageClientListener : messageClientList) {
                messageClientListener.onIncomingMessage(messageClient, message);
            }
        }

        @Override
        public void onMessageSent(MessageClient messageClient, Message message, String s) {
            for (MessageClientListener messageClientListener : messageClientList) {
                messageClientListener.onMessageSent(messageClient, message, s);
            }
        }

        @Override
        public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
            for (MessageClientListener messageClientListener : messageClientList) {
                messageClientListener.onMessageFailed(messageClient, message, messageFailureInfo);
            }
        }

        @Override
        public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {
            for (MessageClientListener messageClientListener : messageClientList) {
                messageClientListener.onMessageDelivered(messageClient, messageDeliveryInfo);
            }
        }

        @Override
        public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {
            for (MessageClientListener messageClientListener : messageClientList) {
                messageClientListener.onShouldSendPushData(messageClient, message, list);
            }
        }
    }

    /* For storing the sinch user for getting all the push notification if your not online*/
    private class PersistedSettings {

        private SharedPreferences mStore;

        private static final String PREF_KEY = "Sinch";

        public PersistedSettings(Context context) {
            mStore = context.getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        }

        public String getUsername() {
            return mStore.getString("Username", "");
        }

        public void setUsername(String username) {
            SharedPreferences.Editor editor = mStore.edit();
            editor.putString("Username", username);
            editor.commit();
        }
    }

    /*Registering all the MessageClientListner whom want to listen the message*/
    public static void registerMessageListner(MessageClientListener messageClientListener) {
        messageClientList.add(messageClientListener);
    }

    /*UnRegistering all the MessageClientListner whom want to listen the message*/
    public static void unRegisterMessageListner(MessageClientListener messageClientListener) {
        messageClientList.remove(messageClientListener);
    }

    /*Show the notfication while the user is offline*/
    private void showNotification(Message message) {
        int notifyID = 1;
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Message from " + message.getSenderId())
                .setContentText("You've received new messages.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setColor(Color.BLUE)
                .setAutoCancel(true);

        mNotifyBuilder.setContentText(message.getTextBody())
                .setNumber(++numMessages);

        resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.putExtra(Constants.EXTRA_EMAIL,message.getSenderId());
        if (resultIntent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mNotifyBuilder.setContentIntent(pendingIntent);
        }
        Notification notification = mNotifyBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }

}
