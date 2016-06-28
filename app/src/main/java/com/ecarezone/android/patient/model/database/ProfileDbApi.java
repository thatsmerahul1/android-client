package com.ecarezone.android.patient.model.database;

import android.content.Context;
import android.content.Intent;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.UserProfile;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by L&T Technology Services on 3/2/2016.
 */

public class ProfileDbApi {

    private static DbHelper mDbHelper;
    private static Context mContext;
    private static ProfileDbApi mProfileDbApi;

    private ProfileDbApi() {
    }

    public static ProfileDbApi getInstance(Context context) {
        mContext = context;
        if (mDbHelper == null || mProfileDbApi == null) {
            mDbHelper = new DbHelper(context);
            mProfileDbApi = new ProfileDbApi();
        }
        return mProfileDbApi;

    }

    /* checks whether the user has any profiles. */
    public boolean hasProfile(String userId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            QueryBuilder<UserProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
            long noOfProfiles = queryBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId)
                    .countOf();
            return noOfProfiles > 0 ? true : false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* Saves multiple user profiles at a time in local database. Used during login */
    public boolean saveMultipleProfiles(String userId, UserProfile[] userProfiles) {
        for (UserProfile userProfile : userProfiles) {
            saveProfile(userId, userProfile, userProfile.profileId);
        }
        return true;
    }

    public int deleteProfiles(String userId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            DeleteBuilder<UserProfile, Integer> deleteBuilder = userProfileDao.deleteBuilder();
            deleteBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId);
            return deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /* Saves a user profile in local database. Returns success failure response. */
    public boolean saveProfile(String userId, UserProfile userProfile, String profileId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            userProfile.userId = userId;
            userProfile.profileId = profileId;
            userProfile.isComplete = areAllFieldsFilled(userProfile);
            int status = userProfileDao.create(userProfile);
            return status != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* Saves a user profile in local database. Returns success failure response. */
    public boolean updateProfile(String userId, UserProfile userProfile, String profileId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            UpdateBuilder<UserProfile, Integer> updateBuilder = userProfileDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId)
                    .and()
                    .eq(DbContract.Profiles.COLUMN_NAME_PROFILE_ID, profileId);

            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_NAME, userProfile.name);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_PROFILE_NAME, userProfile.profileName);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_EMAIL, userProfile.email);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_ADDRESS, userProfile.address);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_BIRTH_DATE, userProfile.birthdate);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_ETHNICITY, userProfile.ethnicity);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_GENDER, userProfile.gender);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_HEIGHT, userProfile.height);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_WEIGHT, userProfile.weight);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_AVATAR_URL, userProfile.avatarUrl);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_IS_COMPLETE, areAllFieldsFilled(userProfile));

            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateIsMyProfile(String userId, boolean isMyProfile, String  profileId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            UpdateBuilder<UserProfile, Integer> updateBuilder = userProfileDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId)
                    .and()
                    .eq(DbContract.Profiles.COLUMN_NAME_PROFILE_ID, profileId);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_IS_MYPROFILE, isMyProfile);

            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* retrieve the details of a particular profile */
    public UserProfile getProfile(String userId, String profileId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            QueryBuilder<UserProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
            return queryBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId)
                    .and()
                    .eq(DbContract.Profiles.COLUMN_NAME_PROFILE_ID, profileId)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* Retrieve all the profiles associated with particular user */
    public UserProfile[] getProfiles(String userId) {
        UserProfile[] profiles = new UserProfile[0];
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            QueryBuilder<UserProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
            List<UserProfile> userProfilesList = queryBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId)
                    .query();
            profiles = userProfilesList.toArray(profiles);
            return profiles;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* Retrieve all the profiles associated with particular user */
    public int getProfileIdUsingEmail(String email) {
        UserProfile[] profiles = new UserProfile[0];
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            QueryBuilder<UserProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
            List<UserProfile> userProfilesList = queryBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_EMAIL, email)
                    .query();
           if(userProfilesList!=null && userProfilesList.size() > 0){
               return Integer.parseInt(userProfilesList.get(0).userId);
           }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /* deletes a particular profile */
    public boolean deleteProfile(String userId, String profileId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            DeleteBuilder<UserProfile, Integer> deleteBuilder = userProfileDao.deleteBuilder();
            deleteBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId)
                    .and()
                    .eq(DbContract.Profiles.COLUMN_NAME_PROFILE_ID, profileId);
            int count = deleteBuilder.delete();
            return count > 0 ? true : false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserProfile getMyProfile() {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            QueryBuilder<UserProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
            return queryBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, LoginInfo.userId)
                    .and()
                    .eq(DbContract.Profiles.COLUMN_NAME_PROFILE_NAME, mContext.getString(R.string.profile_mine))
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isMyProfileComplete() {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            QueryBuilder<UserProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
            UserProfile myProfile = queryBuilder
                    .where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, LoginInfo.userId)
                    .and()
                    .eq(DbContract.Profiles.COLUMN_NAME_PROFILE_NAME, mContext.getString(R.string.profile_mine))
                    .queryForFirst();
            if(myProfile!=null){
                return myProfile.isComplete;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean areAllFieldsFilled(UserProfile userProfile) {
        try {
            if (userProfile.profileName.length() < 2) {
                return false;
            } else if (userProfile.address.length() < 2) {
                return false;
            } else if (userProfile.avatarUrl.length() < 2) {
                return false;
            } else if (userProfile.birthdate.length() < 2) {
                return false;
            } else if (userProfile.email.length() < 2) {
                return false;
            } else if (userProfile.ethnicity.length() < 2) {
                return false;
            } else if (userProfile.gender.length() < 2) {
                return false;
            } else if (userProfile.name.length() < 2) {
                return false;
            } else if (userProfile.height.length() < 1) {
                return false;
            } else if (userProfile.weight.length() < 1) {
                return false;
            } else {
                // all fields have some data
                return true;
            }
        } catch (NullPointerException e) {
            // if any of the field is not set, null pointer exception is caught. it means profile is not Finished.
            return false;
        }
    }
}