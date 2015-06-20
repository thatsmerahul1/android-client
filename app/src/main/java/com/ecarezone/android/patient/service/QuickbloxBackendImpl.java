package com.ecarezone.android.patient.service;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.ecarezone.android.patient.R;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
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

    private static QuickbloxBackendImpl sQuickbloxBackendImpl = null;

    private QuickbloxBackendImpl(Context context) {
        mContext = context;
    }

    public static synchronized QuickbloxBackendImpl getInstance(Context context) {
        if(sQuickbloxBackendImpl == null) {
            sQuickbloxBackendImpl = new QuickbloxBackendImpl(context);
        }
        return sQuickbloxBackendImpl;
    }

    private Context mContext = null;
    private QBSettings mQBSettings = null;
    private QBUser mQbUser = null;

    void config(){
        if(mQBSettings == null) {
            Resources res = mContext.getResources();
            mQBSettings = QBSettings.getInstance().fastConfigInit(res.getString(R.string.qb_app_id),
                                                    res.getString(R.string.qb_app_auth_key),
                                                    res.getString(R.string.qb_app_auth_secret));
            Log.d(getCallerName(), "Quickblox config");
        }
    }

    QBUser getCurrentUser() {
        return  mQbUser;
    }


    /**
     * USER management
     */

    void createSession() {
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // success
                Log.d(getCallerName(), "Quickblox createSession " + session.toString());
            }

            @Override
            public void onError(List<String> errors) {
                // errors
                for (String e : errors) {
                    Log.d(getCallerName(), "Quickblox createSession error " + e);
                }
            }
        });
    }

    void register(final String username, final String password, final WebService.OnQuickbloxAuthenticationListener callback) {
        // Register new user
        QBUsers.signUp(new QBUser(username, password, username), new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                mQbUser = qbUser;
                mQbUser.setPassword(password);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(List<String> list) {

            }
        });
    }

    void login(final String username, final String password, final WebService.OnQuickbloxAuthenticationListener callback) {
        // Login
        QBUsers.signIn(new QBUser(username, password), new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                mQbUser = qbUser;
                mQbUser.setPassword(password);
                if(callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(List<String> list) {

            }
        });
    }

    void getAllOtherUsers(final WebService.OnQuickbloxFetchUsersListener callback) {
        final QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1).setPerPage(50);
        QBUsers.getUsers(pagedRequestBuilder, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                if(callback != null) {
                    if(mQbUser != null) {
                        for(QBUser u : qbUsers) {
                            if(mQbUser.getLogin().equals(u.getLogin())) {
                                qbUsers.remove(u);
                                break;
                            }
                        }
                    }
                    callback.onSuccess(qbUsers);
                }
            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(List<String> list) {
                if(callback != null) {
                    callback.onError();
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
                Log.d(getCallerName(), "Quickblox logout user " + user.toString());
                mQbUser = null;
            }

            @Override
            public void onError(List<String> errors) {
                // error
                for (String e : errors) {
                    Log.d(getCallerName(), "Quickblox logout error " + e);
                }
            }
        });
    }


    void getUserChats(final WebService.OnQuickbloxFetchChatsListener callback) {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setPagesLimit(100);

        QBChatService.getChatDialogs(QBDialogType.PRIVATE, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> dialogs, Bundle args) {
                if(callback != null) {
                    callback.onSuccess(dialogs);
                }
            }

            @Override
            public void onError(List<String> errors) {
                if(callback != null) {
                    callback.onError();
                }
            }
        });
    }

}
