package com.ecarezone.android.patient.model.database;

import android.content.Context;

import com.ecarezone.android.patient.model.Doctor;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 20109804 on 5/18/2016.
 */
public class DoctorProfileDbApi {
    private static DbHelper mDbHelper;
    private static Context mContext;
    private static DoctorProfileDbApi mDoctorProfileDbApi;


    public DoctorProfileDbApi() {
    }
    public static DoctorProfileDbApi getInstance(Context context) {
        mContext = context;
        if (mDbHelper == null || mDoctorProfileDbApi == null) {
            mDbHelper = new DbHelper(context);
            mDoctorProfileDbApi = new DoctorProfileDbApi();
        }
        return mDoctorProfileDbApi;

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
            updateBuilder.updateColumnValue(DbContract.DoctorProfiles.COLUMN_NAME_DOCTOR_CATEGORY, doctorProfile.category);
            updateBuilder.updateColumnValue(DbContract.DoctorProfiles.COLUMN_NAME_DOCTOR_DESCRIPTION, doctorProfile.doctorDescription);
            updateBuilder.updateColumnValue(DbContract.DoctorProfiles.COLUMN_NAME_DOCTOR_REQUEST_PENDING, doctorProfile.requestPending);

            int updatedRowCount = updateBuilder.update();
            if(updatedRowCount > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePendingReqProfile(String doctorId, boolean pendingReq) {
        try {
            Dao<Doctor, Integer> userProfileDao = mDbHelper.getDoctorsProfileDao();
            UpdateBuilder<Doctor, Integer> updateBuilder = userProfileDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.DoctorProfiles.COLUMN_NAME_USER_ID, doctorId);

             updateBuilder.updateColumnValue(DbContract.DoctorProfiles.COLUMN_NAME_DOCTOR_REQUEST_PENDING, pendingReq);

            int updatedRowCount = updateBuilder.update();
            if(updatedRowCount > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateCategory(String doctorId, String category) {
        try {
            Dao<Doctor, Integer> userProfileDao = mDbHelper.getDoctorsProfileDao();
            UpdateBuilder<Doctor, Integer> updateBuilder = userProfileDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.DoctorProfiles.COLUMN_NAME_USER_ID, doctorId);

            updateBuilder.updateColumnValue(DbContract.DoctorProfiles.COLUMN_NAME_DOCTOR_CATEGORY, category);

            int updatedRowCount = updateBuilder.update();
            if(updatedRowCount > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /* retrieve the details of a particular profile */
    public Doctor getProfile(String emailId) {
        try {
            Dao<Doctor, Integer> userProfileDao = mDbHelper.getDoctorsProfileDao();
            QueryBuilder<Doctor, Integer> queryBuilder = userProfileDao.queryBuilder();
            return queryBuilder.where()
                    .eq(DbContract.DoctorProfiles.COLUMN_NAME_EMAIL, emailId)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param id
     * @return
     */
    public Doctor getProfileById(String id){
        try {
            Dao<Doctor, Integer> userProfileDao = mDbHelper.getDoctorsProfileDao();
            QueryBuilder<Doctor, Integer> queryBuilder = userProfileDao.queryBuilder();
            return queryBuilder.where()
                    .eq(DbContract.DoctorProfiles.COLUMN_NAME_USER_ID, id)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getProfileIdUsingEmail(String emailId) {
        try {
            Dao<Doctor, Integer> userProfileDao = mDbHelper.getDoctorsProfileDao();
            QueryBuilder<Doctor, Integer> queryBuilder = userProfileDao.queryBuilder();
            List<Doctor> userProfilesList = queryBuilder.where()
                    .eq(DbContract.DoctorProfiles.COLUMN_NAME_EMAIL, emailId)
                    .query();
            if(userProfilesList!=null && userProfilesList.size() > 0){
                return Integer.parseInt(String.valueOf(userProfilesList.get(0).doctorId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getByDocId(long doctorId) {
        try {
            Dao<Doctor, Integer> userProfileDao = mDbHelper.getDoctorsProfileDao();
            QueryBuilder<Doctor, Integer> queryBuilder = userProfileDao.queryBuilder();
            List<Doctor> userProfilesList = queryBuilder.where()
                    .eq(DbContract.DoctorProfiles.COLUMN_NAME_USER_ID, doctorId)
                    .query();
            if(userProfilesList!=null && userProfilesList.size() > 0){
                return Integer.parseInt(String.valueOf(userProfilesList.get(0).doctorId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public ArrayList<Doctor> getPendingRequest(boolean requestPending) {
        try {
            Dao<Doctor, Integer> userProfileDao = mDbHelper.getDoctorsProfileDao();
            QueryBuilder<Doctor, Integer> queryBuilder = userProfileDao.queryBuilder();
            return (ArrayList<Doctor>) queryBuilder.where().eq(DbContract.DoctorProfiles.COLUMN_NAME_DOCTOR_REQUEST_PENDING, requestPending).query();
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
