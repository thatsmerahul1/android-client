package com.ecarezone.android.patient.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ecarezone.android.patient.model.Chat;
import com.ecarezone.android.patient.model.User;
import com.ecarezone.android.patient.model.UserProfile;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


/**
 * Created by L&T Technology Services on 2/16/2016.
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ecarezone.db";

    private Context mContext;
    private Dao<UserProfile, Integer> mProfileDao = null;
    private Dao<User, Integer> mUserDao = null;
    private Dao<Chat, Integer> mChatDao = null;

    private static final String SQL_DELETE_PROFILES =
            "DELETE FROM " + DbContract.Profiles.TABLE_NAME;

    /* Constructor */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, UserProfile.class);
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Chat.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        // onUpgrade is not necessary in the first release, so simply dropping and recreating the table.
        try {
            TableUtils.dropTable(connectionSource, UserProfile.class, true);
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Chat.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onCreate(sqLiteDatabase, connectionSource);
    }

    // profile data-access-object(Dao).
    public Dao<UserProfile, Integer> getProfileDao() throws SQLException {
        if (mProfileDao == null) {
            mProfileDao = getDao(UserProfile.class);
        }
        return mProfileDao;
    }

    // user data-access-object(Dao).
    public Dao<User, Integer> getUserDao() throws SQLException {
        if (mUserDao == null) {
            mUserDao = getDao(User.class);
        }
        return mUserDao;
    }

    // Chat data-access-object(Dao).
    public Dao<Chat, Integer> getChatDao() throws SQLException {
        if (mChatDao == null) {
            mChatDao = getDao(Chat.class);
        }
        return mChatDao;
    }

    @Override
    public void close() {
        super.close();
        mProfileDao = null;
    }
}