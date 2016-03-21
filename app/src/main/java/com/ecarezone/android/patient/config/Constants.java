package com.ecarezone.android.patient.config;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public class Constants {

    public static String ECARE_ZONE = "eCareZone";

    public static String API_KEY = "O0mwYEyHQ0c0Knk78Ct9MtJbui9noo9y";
    public static String deviceUnique = "34a5dae02b2156dbdfff8da457922b0d";

    // necessary for hashing the password
    public static String salt = "yaps74:purls";

    // Webservice domain
    public final static String ENDPOINTURL = "http://188.166.55.204";
    public final static String ENDPOINTPORT = "9000";
    public static String API_END_POINT = ENDPOINTURL + ":" + ENDPOINTPORT;

    // create account & settings
    public static String COUNTRY = "country";
    public static String LANGUAGE = "language";
    public static String TYPE = "type";
    public static String ITEM = "item";
    public static String ITEM_CODE = "item_code";

    public static String GRAVATOR_URL = "http://www.gravatar.com/avatar/";
    public static String DEFAULT_GRAVATOR_IMAGE_URL = "http%3A%2F%2F188.166.55.204%2Fimages%2Fnews%2Fothers1.jpg";

    //Doctor Details Constants
    public static String DOCTOR_DETAIL = "doctorDetail";
    public static String DOCTOR_BIO_DETAIL = "doctorBioDetail";
    public static String DOCTOR_LIST = "doctorList";

    public static String AVAILABLE = "available";
    public static String BUSY = "busy";

    public static String EXTRA_EMAIL ="email";
    public static String EXTRA_NAME = "name";

    //Login Screen Constants
    public static String USER_ID = "UserId";
    public static String IS_LOGIN = "is_login";
    public static String SHARED_PREF_NAME = "eCareZone";

    //Registration Constants
    public static String REGISTRATION_DIALOG_TAG = "RegDiaFragment";
}
