package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ecarezone.android.patient.DoctorActivity;
import com.ecarezone.android.patient.ProfileDetailsActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.SearchActivity;
import com.ecarezone.android.patient.service.WebService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHAO WEI on 5/25/2015.
 */
public class DoctorListFragment extends EcareZoneBaseFragment {

    private ListView mChatList = null;
    private ArrayList<QBDialog> mChatDialogList = null;

    @Override
    protected String getCallerName() {
        return DoctorListFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setHasOptionsMenu(true);
        } catch (Exception e) {
        }
        mChatDialogList = new ArrayList<QBDialog>();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item == null) return false;

        final int itemId = item.getItemId();
        if(itemId == R.id.menu_action_search) {
            final Activity activity = getActivity();
            if(activity != null) {
                activity.startActivity(new Intent(activity.getApplicationContext(), SearchActivity.class));
            }
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_doctor_list, container, false);
        mChatList = (ListView) view.findViewById(R.id.list_view_chat_dialogs);
        // fetch chats
        WebService.getInstance(getApplicationContext()).getUserChats(new WebService.OnQuickbloxFetchChatsListener() {
            @Override
            public void onSuccess(ArrayList<QBDialog> dialogs) {
                if(dialogs != null) {
                    mChatDialogList.addAll(dialogs);
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

    private  void updateList () {

    }

}
