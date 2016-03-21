package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.ChatActivity;
import com.ecarezone.android.patient.ProfileDetailsActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.adapter.ChatAdapter;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.Chat;
import com.ecarezone.android.patient.model.database.ChatDbApi;
import com.ecarezone.android.patient.utils.ImageUtil;
import com.ecarezone.android.patient.utils.PermissionUtil;
import com.ecarezone.android.patient.utils.SinchUtil;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by L&T Technology Services on 2/18/2016.
 */
public class ChatFragment extends EcareZoneBaseFragment implements View.OnClickListener, MessageClientListener {
    private RecyclerView chatList;
    private ChatAdapter chatAdapter;
    private EditText chatBox;
    private ImageView cameraBtn;
    private static String TAG = ChatFragment.class.getName();
    private Chat chat;
    private final static String CHAT_IMAGE = "image";
    private final static String CHAT_TEXT = "text";
    String recipient = "ecareuser@mail.com";
    private String deviceImagePath;
    Map<String, Integer> perms = new HashMap<>();

    @Override
    protected String getCallerName() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_chat, container, false);
        getAllComponent(view);
        ((ChatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getText(R.string.doctor_details_chat));
        return view;
    }

    private void getAllComponent(View view) {
        chatAdapter = new ChatAdapter(getActivity());
        chatList = (RecyclerView) view.findViewById(R.id.chat_mesage_list);
        chatList.setAdapter(chatAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        chatList.setLayoutManager(linearLayoutManager);
        chatBox = (EditText) view.findViewById(R.id.chatBox);
        chatBox.setImeActionLabel("SEND", EditorInfo.IME_ACTION_DONE);
        chatBox.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        sendMessage(chatBox.getText().toString(), CHAT_TEXT);
                        return true;
                    default:
                        return false;

                }
            }
        });
        cameraBtn = (ImageView) view.findViewById(R.id.chatCameraBtn);
        cameraBtn.setOnClickListener(this);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        recipient = getArguments().getString(Constants.EXTRA_EMAIL);
        chatAdapter.getChatHistory(recipient);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.chatCameraBtn:
                if (PermissionUtil.isPermissionRequired()
                        && PermissionUtil.getAllpermissionRequired(getActivity(), PermissionUtil.CAPTURE_PHOTO_FROM_CAMERA_PERMISSIONS).length > 0) {
                    PermissionUtil.setAllPermission(getActivity(), PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                            , PermissionUtil.CAPTURE_PHOTO_FROM_CAMERA_PERMISSIONS);
                } else {
                    takePicture();
                }

                break;
        }
    }

    @Override
    public void onIncomingMessage(MessageClient messageClient, Message message) {
        if (!message.getRecipientIds().get(0).equals(recipient)) {
            return;
        }
        chatAdapter.addMessage(incomingMessage(message));
        chatList.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }

    private void takePicture(){
        deviceImagePath = ImageUtil.dispatchTakePictureIntent(getActivity());
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(deviceImagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onMessageSent(MessageClient messageClient, Message message, String s) {

    }

    @Override
    public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sending failed: ")
                .append(messageFailureInfo.getSinchError().getMessage());

        Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, sb.toString());
    }

    @Override
    public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {
        Log.d(TAG, "onDelivered");
    }

    @Override
    public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {
        Log.d(TAG, "onShouldSendPushData");
    }

    private void sendMessage(String textBody, String messageType) {
        chat = new Chat();
        if (recipient.isEmpty()) {
            Toast.makeText(getActivity(), "No recipient added", Toast.LENGTH_SHORT).show();
            return;
        }
        if (textBody.isEmpty() && messageType.equals(CHAT_TEXT)) {
            Toast.makeText(getActivity(), "No text message", Toast.LENGTH_SHORT).show();
            return;
        }

        chat.setChatUserId(recipient);
        chatBox.setText("");
        chat.setTimeStamp(new Date());
        chat.setChatType(ChatDbApi.CHAT_OUTGOING);
        chat.setReadStatus(ChatDbApi.CHAT_READ_STATUS);

        try {
            if (messageType.equals(CHAT_IMAGE)) {
                chat.setDeviceImagePath(deviceImagePath);
            } else {
                chat.setMessageText(textBody);
                SinchUtil.getSinchServiceInterface().sendMessage(recipient, textBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        chat.setIsChatSending(true);
        chatAdapter.addMessage(chat);
        chatList.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        new Thread() {
            @Override
            public void run() {
                ChatDbApi.getInstance(getApplicationContext()).saveChat(chat);
            }
        }.start();
    }

    private Chat incomingMessage(Message message) {
        chat = new Chat();
        chat.setChatUserId(message.getSenderId());
        if (message.getTextBody().contains(Constants.ENDPOINTURL)) {
            chat.setInComingImageUrl(message.getTextBody());
        } else {
            chat.setMessageText(message.getTextBody());
        }
        chat.setTimeStamp(message.getTimestamp());
        chat.setChatType(ChatDbApi.CHAT_INCOMING);
        chatBox.setText("");

        return chat;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        File file = new File(deviceImagePath);
        if (file.exists()
                && requestCode == ImageUtil.REQUEST_IMAGE_CAPTURE
                && resultCode == Activity.RESULT_OK) {
            sendMessage("", CHAT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;
        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // Fill with results
                perms.clear();
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                for (int count = 0; count < perms.size(); count++) {
                    if (perms.get(permissions[count]).equals(PackageManager.PERMISSION_GRANTED)) {
                        // All Permissions Granted
                        permissionGranted = true;
                    } else {
                        permissionGranted = false;
                        break;
                    }
                }

                if (permissionGranted) {
                    takePicture();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
