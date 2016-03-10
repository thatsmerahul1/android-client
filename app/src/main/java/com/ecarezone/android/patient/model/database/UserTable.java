package com.ecarezone.android.patient.model.database;

import android.content.Context;

import com.ecarezone.android.patient.model.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;

/**
 * Created by L&T Technology Services on 2/23/2016.
 */
public class UserTable {
    private DbHelper mDbHelper;
    private Context context;

    public UserTable(Context context) {
        this.context = context;
        this.mDbHelper = new DbHelper(context);
    }

    /* Checks whether the user exists in db or not */
    public boolean userExists(String userId) {
        try {
            Dao<User, Integer> userDao = mDbHelper.getUserDao();
            QueryBuilder<User, Integer> queryBuilder = userDao.queryBuilder();
            long noOfUsers = queryBuilder.where().eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId).countOf();
            return noOfUsers > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* save the user data to db */
    public boolean saveUserData(String userId, String email, String password, String language, String role, String country) {
        try {
            Dao<User, Integer> userDao = mDbHelper.getUserDao();

            User user = new User();
            user.email = email;
            user.password = password;
            user.language = language;
            user.role = role;
            user.country = country;
            user.userId = userId;

            return userDao.create(user) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* Update user data to db */
    public boolean updateUserData(String userId, String email, String password, String language, String role, String country) {
        try {
            Dao<User, Integer> userDao = mDbHelper.getUserDao();
            UpdateBuilder<User, Integer> updateBuilder = userDao.updateBuilder();

            updateBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId);

            updateBuilder.updateColumnValue(DbContract.Users.COLUMN_NAME_EMAIL, email);
            updateBuilder.updateColumnValue(DbContract.Users.COLUMN_NAME_PASSWORD, password);
            updateBuilder.updateColumnValue(DbContract.Users.COLUMN_NAME_LANGUAGE, language);
            updateBuilder.updateColumnValue(DbContract.Users.COLUMN_NAME_ROLE, role);
            updateBuilder.updateColumnValue(DbContract.Users.COLUMN_NAME_COUNTRY, country);

            return updateBuilder.update() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* retrieve the user data */
    public User getUserData(String userId) {
        if (userId == null) {
            return null;
        }
        try {
            Dao<User, Integer> userDao = mDbHelper.getUserDao();
            QueryBuilder<User, Integer> queryBuilder = userDao.queryBuilder();
            queryBuilder.where()
                    .eq(DbContract.Users.COLUMN_NAME_USER_ID, userId);
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}