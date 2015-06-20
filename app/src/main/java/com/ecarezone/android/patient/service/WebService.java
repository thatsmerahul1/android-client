package com.ecarezone.android.patient.service;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by CHAO WEI on 5/17/2015.
 */
public class WebService {

    final String getCallerName() {
        return  WebService.class.getSimpleName();
    }

    private static final String UTF_8 =  "UTF-8";

    /**
     * WebService singleton instance
     */
    private static WebService sInstance;
    private Context mContext = null;
    private Resources mResources = null;
    private final QuickbloxBackendImpl mQuickbloxBackendImpl;

    /**
     * Get the {@link WebService} instance.
     *
     * @param context
     *            Application context.
     *
     * @return {@link WebService} instance.
     */
    public static synchronized WebService getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WebService(context);
        }
        return sInstance;
    }

    private WebService(Context context) {
        mContext = context;
        mResources = context.getResources();
        mQuickbloxBackendImpl = QuickbloxBackendImpl.getInstance(mContext);
    }

    public void configQuickblox() {
        mQuickbloxBackendImpl.config();
    }

    public void createQuickbloxSession() {
        mQuickbloxBackendImpl.createSession();
    }

    public JSONObject register(String username, String password, OnQuickbloxAuthenticationListener callback) {
        mQuickbloxBackendImpl.register(username, password, callback);
        return  null;
    }

    public JSONObject login(String username, String password, OnQuickbloxAuthenticationListener callback) {
        mQuickbloxBackendImpl.login(username, password, callback);
        return null;
    }

    public JSONObject logut() {
        mQuickbloxBackendImpl.logout();
        return  null;
    }

    public void getAllOtherUsers(OnQuickbloxFetchUsersListener callback) {
        mQuickbloxBackendImpl.getAllOtherUsers(callback);
    }

    public void getUserChats(OnQuickbloxFetchChatsListener callback) {
        mQuickbloxBackendImpl.getUserChats(callback);
    }

    public QBUser getCurrentLoginUser() {
        return mQuickbloxBackendImpl.getCurrentUser();
    }



    public static interface OnQuickbloxAuthenticationListener {
        void onSuccess();
        void onError();
    }

    public static interface OnQuickbloxFetchUsersListener {
        void onSuccess(ArrayList<QBUser> qbUsers);
        void onError();
    }

    public static interface OnQuickbloxFetchChatsListener {
        void onSuccess(ArrayList<QBDialog> dialogs);
        void onError();
    }

}
