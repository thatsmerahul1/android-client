package com.ecarezone.android.patient.model.database;

import android.content.Context;

import com.ecarezone.android.patient.model.Chat;
import com.ecarezone.android.patient.model.UserProfile;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by L&T Technology Services
 */
public class ChatDbApi {

    private static DbHelper mDbHelper;
    private static ChatDbApi mChatDbapi;
    private static Context mContext;
    private final static String COLUMN_NAME_CHAT_USER_ID = "chatUserId";
    private final static String COLUMN_NAME_CHAT_READ_STATUS = "readStatus";

    public final static String CHAT_READ_STATUS = "read";
    public final static String CHAT_UNREAD_STATUS = "unread";

    public final static String CHAT_INCOMING = "incoming";
    public final static String CHAT_OUTGOING = "outgoing";

    private ChatDbApi() {

    }

    public static ChatDbApi getInstance(Context context) {
        mContext = context;
        if (mDbHelper == null || mChatDbapi == null) {
            mDbHelper = new DbHelper(context);
            mChatDbapi = new ChatDbApi();
        }
        return mChatDbapi;

    }

    /* Saves a user chat in local database. Returns success failure response. */
    public static boolean saveChat(Chat chat) {
        try {
            Dao<Chat, Integer> chatDao = mDbHelper.getChatDao();
            int status = chatDao.create(chat);
            return status != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* retrieve the ChatHistory of a particular user */
    public static List<Chat> getChatHistory(String userId) {
        try {
            Dao<Chat, Integer> chatDao = mDbHelper.getChatDao();
            QueryBuilder<Chat, Integer> queryBuilder = chatDao.queryBuilder();
            return queryBuilder.where().eq(COLUMN_NAME_CHAT_USER_ID, userId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* deletes chat History of particular user */
    public static boolean deleteChat(String userId) {
        try {
            Dao<Chat, Integer> chatDao = mDbHelper.getChatDao();
            DeleteBuilder<Chat, Integer> deleteBuilder = chatDao.deleteBuilder();
            deleteBuilder.where()
                    .eq(COLUMN_NAME_CHAT_USER_ID, userId);
            int count = deleteBuilder.delete();
            return count > 0 ? true : false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* update read status of user Chat history */
    public static boolean updateChatReadStatus(String userId, String readStatus) {
        try {
            Dao<Chat, Integer> chatDao = mDbHelper.getChatDao();
            UpdateBuilder<Chat, Integer> updateBuilder = chatDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId);

            updateBuilder.updateColumnValue(COLUMN_NAME_CHAT_READ_STATUS, readStatus);

            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* retrieve unread ChatCount from chat History by userId */
    public static int getUnReadChatCountByUserId(String userId) {
        try {
            Dao<Chat, Integer> chatDao = mDbHelper.getChatDao();
            QueryBuilder<Chat, Integer> queryBuilder = chatDao.queryBuilder();
            return queryBuilder.where()
                    .eq(COLUMN_NAME_CHAT_USER_ID, userId)
                    .and()
                    .eq(COLUMN_NAME_CHAT_READ_STATUS, CHAT_UNREAD_STATUS)
                    .query().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /* retrieve unread ChatCount from chat History*/
    public static int getUnReadChatCount() {
        try {
            Dao<Chat, Integer> chatDao = mDbHelper.getChatDao();
            QueryBuilder<Chat, Integer> queryBuilder = chatDao.queryBuilder();
            return queryBuilder.where()
                    .eq(COLUMN_NAME_CHAT_READ_STATUS, CHAT_UNREAD_STATUS)
                    .query().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
