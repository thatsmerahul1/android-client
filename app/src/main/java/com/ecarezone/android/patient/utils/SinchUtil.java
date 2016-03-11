package com.ecarezone.android.patient.utils;

        import android.content.Context;

        import com.ecarezone.android.patient.app.AudioPlayer;
        import com.ecarezone.android.patient.config.Constants;
        import com.ecarezone.android.patient.model.Chat;
        import com.ecarezone.android.patient.model.database.ChatDbApi;
        import com.ecarezone.android.patient.service.SinchService;
        import com.sinch.android.rtc.messaging.Message;

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

    public static void saveIncomingChatHistory(Message message, Context context) {
        Chat chat = new Chat();
        if (message.getTextBody().contains(Constants.ENDPOINTURL)) {
            chat.setInComingImageUrl(message.getTextBody());
        } else {
            chat.setMessageText(message.getTextBody());
        }
        chat.setTimeStamp(message.getTimestamp());
        chat.setChatUserId(message.getSenderId());
        chat.setChatType(ChatDbApi.CHAT_INCOMING);
        chat.setReadStatus(ChatDbApi.CHAT_UNREAD_STATUS);

        ChatDbApi.getInstance(context).saveChat(chat);
    }
}
