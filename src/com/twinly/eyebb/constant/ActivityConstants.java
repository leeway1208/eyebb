package com.twinly.eyebb.constant;

public class ActivityConstants {
	// share_preference item name
	public static final String SHARE_PREFERENCES_NAME = "EyeBB";
	public static final String SHARE_PREFS_ITEM_APP_VERSION = "app_version";
	public static final String SHARE_PREFS_ITEM_IS_LOGIN = "is_login";
	public static final String SHARE_PREFS_ITEM_REGISTRATION_ID = "registrationId";
	public static final String SHARE_PREFS_ITEM_LOGIN_ACCOUNT = "login_account";
	public static final String SHARE_PREFS_ITEM_PASSWORD = "password";
	public static final String SHARE_PREFS_ITEM_LANGUAGE = "language";
	public static final String SHARE_PREFS_ITEM_USER_ID = "user_id";
	public static final String SHARE_PREFS_ITEM_USER_NAME = "user_name";
	public static final String SHARE_PREFS_ITEM_USER_TYPE = "user_type";
	public static final String SHARE_PREFS_ITEM_USER_PHONE = "user_phone";
	public static final String SHARE_PREFS_ITEM_KINDERGARTEN_ID = "kindergarten_id";
	public static final String SHARE_PREFS_ITEM_AUDO_UPDATE = "auto_update";
	public static final String SHARE_PREFS_ITEM_AUTO_UPDATE_TIME = "auto_update_time";
	public static final String SHARE_PREFS_ITEM_SOUND = "sound";
	public static final String SHARE_PREFS_ITEM_VIBRATE = "vibrate";
	public static final String SHARE_PREFS_ITEM_DEVICE_ID = "device_id";
	public static final String SHARE_PREFS_ITEM_NOTIFICATION_DOT = "notification_dot";
	public static final String SHARE_PREFS_ITEM_REPORT_CHILD_ID = "report_child_id";
	public static final String SHARE_PREFS_ITEM_IS_ANTI_LOST_ON = "is_anti_lost_on";

	public static final int REQUEST_GO_TO_WELCOME_ACTIVITY = 100;
	public static final int REQUEST_GO_TO_LOGIN_ACTIVITY = 101;
	public static final int REQUEST_GO_TO_SIGN_UP_ACTIVITY = 102;
	public static final int REQUEST_GO_TO_KINDERGARTEN_ACTIVITY = 103;
	public static final int REQUEST_GO_TO_SETTING_ACTIVITY = 104;
	public static final int REQUEST_GO_TO_CHANGE_KIDS_ACTIVITY = 105;
	public static final int REQUEST_GO_TO_BIRTHDAY_ACTIVITY = 106;
	public static final int REQUEST_GO_TO_KID_PROFILE_ACTIVITY = 107;
	public static final int REQUEST_GO_TO_UNBIND_ACTIVITY = 108;
	public static final int REQUEST_GO_TO_BEACON_LIST_ACTIVITY = 109;
	public static final int REQUEST_GO_TO_BIND_CHILD_MACARON_DIALOG = 110;
	public static final int REQUEST_GO_TO_BIND_CHILD_MACARON_ACTIVITY = 111;
	public static final int REQUEST_GO_TO_SIGNUP_ASK_TO_BIND_DIALOG = 112;
	public static final int REQUEST_GO_TO_CHILD_INFO_MATCHING_ACTIVITY = 113;
	public static final int REQUEST_GO_TO_CHECK_CHILD_TO_BIND_DIALOG = 114;
	public static final int REQUEST_GO_TO_GRANT_KIDS_ACTIVITY = 115;
	public static final int REQUEST_GO_TO_SEARCH_GUEST_ACTIVITY = 116;
	public static final int REQUEST_GO_TO_UPDATE_NICKNAME_ACTIVITY = 117;
	public static final int REQUEST_GO_TO_OPTIONS_DIALOG = 118;

	public static final int RESULT_RESULT_OK = 200;
	public static final int RESULT_LOGOUT = 201;
	public static final int RESULT_AUTO_UPDATE_ON = 202;
	public static final int RESULT_AUTO_UPDATE_OFF = 203;
	public static final int RESULT_RESULT_BIRTHDAY_OK = 204;
	public static final int RESULT_UNBIND_SUCCESS = 205;
	public static final int RESULT_UNBIND_CANCEL = 206;
	public static final int RESULT_WRITE_MAJOR_MINOR_SUCCESS = 207;
	public static final int RESULT_WRITE_MAJOR_MINOR_FAIL = 208;
	public static final int RESULT_UPDATE_NICKNAME_SUCCESS = 209;

	public static final int FRAGMENT_REPORT_ACTIVITY = 1;
	public static final int FRAGMENT_PROFILE = 2;
	public static final int ACTIVITY_CHECK_CHILD_TO_BIND = 3;
	public static final int ACTIVITY_KID_PROFILE = 4;

	public static final String EXTRA_FROM = "FROM";
	public static final String EXTRA_USER_NAME = "USER_NAME";
	public static final String EXTRA_HASH_PASSWORD = "HASH_PASSWORD";
	public static final String EXTRA_GUARDIAN_ID = "GUARDIAN_ID";
	public static final String EXTRA_MAC_ADDRESS = "MAC_ADDRESS";
	public static final String EXTRA_CHILD_ID = "CHILD_ID";
	public static final String EXTRA_CHILD_ICON = "CHILD_ICON";
}
