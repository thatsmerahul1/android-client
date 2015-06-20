package com.ecarezone.android.patient.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.app.widget.adapter.ChatAdapter;
import com.ecarezone.android.patient.service.Chat.ChatService;
import com.ecarezone.android.patient.service.Chat.GroupChatImpl;
import com.ecarezone.android.patient.service.Chat.IChat;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by CHAO WEI on 6/1/2015.
 */
public class ChatFragment extends EcareZoneBaseFragment implements View.OnClickListener {

    private final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

    private QBDialog mChatDialog = null;
    private ListView mMessagesContainer = null;
    private EditText mEditTextMessage = null;
    private IChat mChat = null;

    private QBGroupChatManager groupChatManager;
    private QBGroupChat groupChat;

    private ChatAdapter mChatAdapter = null;

    @Override
    protected String getCallerName() {
        return ChatFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        if(data != null) {
            mChatDialog = (QBDialog)data.getSerializable("QBChat");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_chat, container, false);
        mMessagesContainer = (ListView) view.findViewById(R.id.messagesContainer);
        mEditTextMessage = (EditText) view.findViewById(R.id.messageEdit);
        //TextView companionLabel = (TextView) view.findViewById(R.id.companionLabel);


        view.findViewById(R.id.chatSendButton).setOnClickListener(this);
        if(mChatDialog == null) {
            getActivity().finish();
        } else {
            if(mChatDialog.getType() == QBDialogType.GROUP) {
                mChat = new GroupChatImpl(getActivity());
                joinGroupChat();
            }
        }
        return view;
    }


    private void joinGroupChat(){
        ((GroupChatImpl) mChat).joinGroupChat(mChatDialog, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                // Load Chat history
                loadChatHistory();
            }

            @Override
            public void onError(List list) {

            }
        });
    }

    private void loadChatHistory(){
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);
        customObjectRequestBuilder.sortDesc("date_sent");

        QBChatService.getDialogMessages(mChatDialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {

                mChatAdapter = new ChatAdapter(getApplicationContext(), new ArrayList<QBChatMessage>());
                mMessagesContainer.setAdapter(mChatAdapter);

                for(int i=messages.size()-1; i>=0; --i) {
                    QBChatMessage msg = messages.get(i);
                    showMessage(msg);
                }


            }

            @Override
            public void onError(List<String> errors) {

            }
        });
    }

    public void showMessage(QBChatMessage message) {
        mChatAdapter.add(message);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatAdapter.notifyDataSetChanged();
                //scrollDown();
            }
        });
    }



    @Override
    public void onClick(View v) {
        if(v == null) return;

        final int viewId = v.getId();
        if(viewId == R.id.chatSendButton) {
            String messageText = mEditTextMessage.getText().toString();
            if (TextUtils.isEmpty(messageText)) {
                return;
            }

            // Send chat message
            QBChatMessage chatMessage = new QBChatMessage();
            chatMessage.setBody(messageText);
            chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
            chatMessage.setDateSent(new Date().getTime() / 1000);

            try {
                mChat.sendMessage(chatMessage);
            } catch (XMPPException e) {
                Log.e(getCallerName(), "failed to send a message", e);
            } catch (SmackException sme) {
                Log.e(getCallerName(), "failed to send a message", sme);
            }

            mEditTextMessage.setText("");
        }
    }



}
