package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ecarezone.android.patient.ChatActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.app.widget.adapter.UserSearchAdapter;
import com.ecarezone.android.patient.service.Chat.ChatService;
import com.ecarezone.android.patient.service.WebService;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by CHAO WEI on 6/19/2015.
 */
public class SearchFragment extends EcareZoneBaseFragment implements AdapterView.OnItemClickListener {

    private ArrayList<QBUser> mQbUsers = null;
    private ListView mUserListView = null;
    private UserSearchAdapter mUserSearchAdapter = null;
    private QBPrivateChatManager mPrivateChatManager = null;

    @Override
    protected String getCallerName() {
        return SearchFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatService.initIfNeed(getApplicationContext());
        ChatService.getInstance().login(WebService.getInstance(getApplicationContext()).getCurrentLoginUser(), new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                Log.d(getCallerName(), "login OK ");
            }

            @Override
            public void onError(List errors) {
                Log.d(getCallerName(), "login errors " + Arrays.toString(errors.toArray(new String[errors.size()])));
            }
        });

        mQbUsers = new ArrayList<QBUser>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_search_user_list, container, false);
        mUserListView = (ListView)view.findViewById(R.id.list_view_users);
        WebService.getInstance(getApplicationContext()).getAllOtherUsers(new WebService.OnQuickbloxFetchUsersListener() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers) {
                if (qbUsers != null) {
                    mQbUsers.addAll(qbUsers);
                    for (QBUser u : qbUsers) {
                        Log.d(getCallerName(), "id " + u.getId());
                    }
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateList();
                        }
                    }, 50L);
                }
            }

            @Override
            public void onError() {
            }
        });
        return view;
    }

    private void updateList() {
        mUserSearchAdapter = new UserSearchAdapter(getApplicationContext(), mQbUsers);
        mUserListView.setAdapter(mUserSearchAdapter);
        mUserListView.setOnItemClickListener(this);
        mUserSearchAdapter.notifyDataSetChanged();
        mUserListView.invalidate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        QBUser chatTarget = mQbUsers.get(position);
        List<QBUser> two = new ArrayList<QBUser>(2);
        two.add(WebService.getInstance(getApplicationContext()).getCurrentLoginUser());
        two.add(chatTarget);
        final Activity activity = getActivity();
        // add to chat

        ChatService.getInstance().addDialogsUsers(two);

        final QBDialog dialogToCreate = new QBDialog();
        dialogToCreate.setName(usersListToChatName(two));
        dialogToCreate.setType(QBDialogType.GROUP);
        dialogToCreate.setOccupantsIds(getUserIds(two));
        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChatManager.createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>() {
            @Override
            public void onSuccess(QBDialog dialog, Bundle args) {
                Integer opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(dialogToCreate);
                if (activity != null) {
                    Intent intent = new Intent();
                    Bundle data = new Bundle();
                    data.putSerializable("QBChat", dialog);
                    intent.setClass(getApplicationContext(), ChatActivity.class);
                    intent.putExtras(data);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }

            @Override
            public void onError(List<String> errors) {
                Log.d(getCallerName(), "createDialog errors " + Arrays.toString(errors.toArray(new String[errors.size()])));
            }
        });

    }

    private String usersListToChatName(List<QBUser> two){
        String chatName = "";
        for(QBUser user : two){
            String prefix = chatName.equals("") ? "" : ", ";
            chatName = chatName + prefix + user.getLogin();
        }
        return chatName;
    }

    public static ArrayList<Integer> getUserIds(List<QBUser> users){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(QBUser user : users){
            ids.add(user.getId());
        }
        return ids;
    }

}
