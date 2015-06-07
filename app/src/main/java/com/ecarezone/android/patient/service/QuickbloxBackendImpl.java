package com.ecarezone.android.patient.service;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.ecarezone.android.patient.R;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHAO WEI on 5/17/2015.
 */
class QuickbloxBackendImpl {


    final String getCallerName() {
        return QuickbloxBackendImpl.class.getSimpleName();
    }

    /**
     * Quickblox credentials
     * username: chao.wei@ecarezone.com
     * passworrd: front123
     *
     * Account id: 23331
     * Account key: dPFd-gPBDxJ288v
     * Authorization secret: c-LBfS-B5tW-ujn
     */

    private static QuickbloxBackendImpl sQuickbloxBackendImpl = null;

    private QuickbloxBackendImpl(Context context) {
        mContext = context;
        if(mQBSettings == null) {
            Resources res = mContext.getResources();
            mQBSettings = QBSettings.getInstance().fastConfigInit(res.getString(R.string.qb_app_id),
                    res.getString(R.string.qb_app_auth_key),
                    res.getString(R.string.qb_app_auth_secret));
        }
    }

    public static synchronized QuickbloxBackendImpl getInstance(Context context) {
        if(sQuickbloxBackendImpl == null) {
            sQuickbloxBackendImpl = new QuickbloxBackendImpl(context);
        }
        return sQuickbloxBackendImpl;
    }

    private Context mContext = null;
    private QBSettings mQBSettings = null;

    void createSession() {
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // success
                Log.d(getCallerName(), "createSession " + session.toString());
            }

            @Override
            public void onError(List<String> errors) {
                // errors
                for(String e : errors) {
                    Log.d(getCallerName(), "createSession error " + e);
                }
            }
        });
    }

    void register(String username, String password) {
        // Register new user
        final QBUser user = new QBUser(username, password);
        QBUsers.signUp(user, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                // success
                Log.d(getCallerName(), "register user " + user.toString());
            }

            @Override
            public void onError(List<String> errors) {
                // error
                for (String e : errors) {
                    Log.d(getCallerName(), "register error " + e);
                }
            }
        });
    }

    void login(String username, String password) {
        final QBUser user = new QBUser(username, password);
        // Login
        QBUsers.signIn(user, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                // success
                Log.d(getCallerName(), "login user " + user.toString());

            }

            @Override
            public void onError(List<String> errors) {
                // error
                for (String e : errors) {
                    Log.d(getCallerName(), "login error " + e);
                }
            }
        });
    }

    void getAllUsers() {
        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        QBUsers.getUsers(requestBuilder, new QBEntityCallback<ArrayList<QBUser>>() {

            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                for (QBUser u : qbUsers) {
                    Log.d(getCallerName(), "user " + u.toString());
                }
            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(List<String> list) {
                for (String s : list) {
                    Log.d(getCallerName(), "list item " + s);
                }
            }
        });
    }


    void updateUser(QBUser user) {
        QBUsers.updateUser(user, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(List<String> list) {

            }
        });
    }

    void logout() {
        QBUsers.signOut(new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                // success
                Log.d(getCallerName(), "logout user " + user.toString());
            }

            @Override
            public void onError(List<String> errors) {
                // error
                for(String e : errors) {
                    Log.d(getCallerName(), "logout error " + e);
                }
            }
        });
    }





}
