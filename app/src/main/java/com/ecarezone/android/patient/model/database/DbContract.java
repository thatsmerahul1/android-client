package com.ecarezone.android.patient.model.database;

import android.provider.BaseColumns;

/**
 * Created by L&T Technology Services on 2/16/2016.
 */
public final class DbContract {

    public DbContract() {
    }

    /* Inner class that defines the Users table contents */
    public static abstract class Users implements BaseColumns {
        public static final String TABLE_NAME = "User";

        public static final String COLUMN_NAME_USER_ID = "userId";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_ROLE = "role";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_LANGUAGE = "language";
    }

    /* Inner class that defines the Profiles table contents */
    public static abstract class Profiles implements BaseColumns {
        public static final String TABLE_NAME = "UserProfile";

        public static final String COLUMN_NAME_USER_ID = "userId";
        public static final String COLUMN_NAME_PROFILE_ID = "profileId";
        public static final String COLUMN_NAME_PROFILE_NAME = "profileName";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_BIRTH_DATE = "birthdate";
        public static final String COLUMN_NAME_ETHNICITY = "ethnicity";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_AVATAR_URL = "avatarUrl";
        public static final String COLUMN_NAME_IS_COMPLETE = "isComplete";
    }
}