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
        public static final String COLUMN_NAME_RECOMMANDED_DOCTOR_ID = "recommandedDoctorId";

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
        public static final String COLUMN_NAME_IS_MYPROFILE = "isMyProfile";
    }
    /*Inner class that defines doctors profile table content*/
    public static abstract class DoctorProfiles implements BaseColumns {
        public static final String TABLE_NAME = "DoctorProfile";

        public static final String COLUMN_NAME_USER_ID = "doctorId";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_EMAIL = "emailId";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_AVATAR_URL = "avatarUrl";
        public static final String COLUMN_NAME_DOCTOR_DESCRIPTION = "doctorDescription";
        public static final String COLUMN_NAME_DOCTOR_CATEGORY = "doctorCategory";
        public static final String COLUMN_NAME_DOCTOR_REQUEST_PENDING = "requestPending";
    }

    /*Inner class that defines appointment table content*/
    public static abstract class Appointments implements BaseColumns {
        public static final String TABLE_NAME = "Appointment";

        public static final String COLUMN_NAME_APPOINTMENT_ID = "id";
        public static final String COLUMN_NAME_DATE_TIME = "dateTime";
        public static final String COLUMN_NAME_CALL_TYPE = "callType";
        public static final String COLUMN_NAME_DOCTOR_ID = "doctorId";
        public static final String COLUMN_NAME_PATIENT_ID = "patientId";
        public static final String COLUMN_NAME_IS_CONFIRMED = "isConfirmed";
    }
}