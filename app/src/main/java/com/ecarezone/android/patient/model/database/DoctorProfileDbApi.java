package com.ecarezone.android.patient.model.database;

import android.content.Context;

import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.UserProfile;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by 20109804 on 5/18/2016.
 */
public class DoctorProfileDbApi {
    private static DbHelper mDbHelper;
    private Context mContext;

    public DoctorProfileDbApi(Context context) {
        mContext = context;
        mDbHelper = new DbHelper(context);
    }

    public static boolean saveProfile(Long doctorId, Doctor doctorProfile) {
        try {
            Dao<Doctor, Integer> userProfileDao = mDbHelper.getDoctorsProfileDao();
            doctorProfile.doctorId = doctorId;
            int status = userProfileDao.create(doctorProfile);
            return status != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveMultipleProfiles(Long userId, ArrayList<Doctor> userProfiles) {
        for (Doctor userProfile : userProfiles) {
            saveProfile(userId, userProfile);
        }
        return true;
    }
    /* Saves a user profile in local database. Returns success failure response. */
    public boolean updateProfile(String doctorId, Doctor doctorProfile) {
        try {
            Dao<Doctor, Integer> userProfileDao = mDbHelper.getDoctorsProfileDao();
            UpdateBuilder<Doctor, Integer> updateBuilder = userProfileDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.DoctorProfiles.COLUMN_NAME_USER_ID, doctorId);

            updateBuilder.updateColumnValue(DbContract.DoctorProfiles.COLUMN_NAME_NAME, doctorProfile.name);
            updateBuilder.updateColumnValue(DbContract.DoctorProfiles.COLUMN_NAME_EMAIL, doctorProfile.emailId);
            updateBuilder.updateColumnValue(DbContract.DoctorProfiles.COLUMN_NAME_STATUS, doctorProfile.status);
            updateBuilder.updateColumnValue(DbContract.DoctorProfiles.COLUMN_NAME_AVATAR_URL, doctorProfile.avatarUrl);
            updateBuilder.updateColumnValue(DbContract.DoctorProfiles.COLUMN_NAME_DOCTOR_CATEGORY, doctorProfile.doctorCategory);
            updateBuilder.updateColumnValue(DbContract.DoctorProfiles.COLUMN_NAME_DOCTOR_DESCRIPTION, doctorProfile.doctorDescription);

            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* retrieve the details of a particular profile */
    public Doctor getProfile(String userId /*String profileId*/) {
        try {
            Dao<Doctor, Integer> userProfileDao = mDbHelper.getDoctorsProfileDao();
            QueryBuilder<Doctor, Integer> queryBuilder = userProfileDao.queryBuilder();
            return queryBuilder.where()
                    .eq(DbContract.DoctorProfiles.COLUMN_NAME_EMAIL, userId)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean areAllFieldsFilled(Doctor userProfile) {
        try {
            if (userProfile.emailId.length() < 2) {
                return false;
            }  else if (userProfile.name.length() < 2) {
                return false;
            }  /*else if (userProfile.gender.length() < 2) {
                return false;
            }*/else {
                // all fields have some data
                return true;
            }
        } catch (NullPointerException e) {
            // if any of the field is not set, null pointer exception is caught. it means profile is not Finished.
            return false;
        }
    }
}
