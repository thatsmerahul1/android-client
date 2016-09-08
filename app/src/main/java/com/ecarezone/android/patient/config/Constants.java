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
    public static String DEFAULT_GRAVATOR_IMAGE_URL = "http%3A%2F%2F188.166.55.204%2Fimages%2Fpatient%2Fdefault_profile_pic";

    //Doctor Details Constants
    public static String DOCTOR_DETAIL = "doctorDetail";
    public static final String DOCTOR_ALEADY_ADDED = "doc_already_added";
    public static String DOCTOR_BIO_DETAIL = "doctorBioDetail";
    public static String DOCTOR_LIST = "doctorList";

    public static String AVAILABLE = "available";
    public static String OFFLINE_TEXT = "offline";
    public static String IDLE_TEXT = "idle";

    public static String EXTRA_EMAIL ="email";
    public static String EXTRA_NAME = "name";
    public static String EXTRA_APPOINMENT_TYPE = "video";


    //Login Screen Constants
    public static final String USER_ID = "UserId";
    public static final String IS_LOGIN = "is_login";
    public static final String SHARED_PREF_NAME = "eCareZone";

    //Registration Constants
    public static final String REGISTRATION_DIALOG_TAG = "RegDiaFragment";

    //status changes
    public static String STATUS_CHANGE = "statusChange";
    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;
    public static final int IDLE = 2;

    public static final String SEND_HEART_BEAT = "send_heart_beat";
    public static final String UPDATE_STATUS = "update_status";
    public static final int UPDATE_STATUS_REQ_ID = 1;

    public static final String UA_CHANNEL_NUMBER = "ua_channel_number";
    public static final String PUSH_NEWS_UPDATE = "ua_news_update";

    public static final String BROADCAST_STATUS_CHANGED = "broadcast_status_changed";
    public static final String SET_STATUS = "set_status";
    public static final String USER_ROLE = "1";

    // news
    public static final String NEWS_MESSAGE = "news_message";
    public static final String UNREAD_NEWS_CATEGORY = "unread_news_category";
    public static final String NEWS_MESSAGE_CATEGORY_SET_KEY = "news_message_key_set";
    public static final String NEWS_CATEGORY_PREPEND_STRING = "news_";
}
