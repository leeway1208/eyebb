package com.twinly.eyebb.constant;

import android.os.Environment;

public class Constants {
	public static final String SENDER_ID = "434117603645";

	public static final String DB_NAME = "eyebb.db";
	public static final String DEVICE_NAME = "Macaron";
	public static final int DB_VERSION = 1;
	public static final int REQUEST_ENABLE_BT = 1;

	public static final int LOCALE_EN = 0;
	public static final int LOCALE_CN = 1;
	public static final int LOCALE_HK = 2;
	public static final int LOCALE_TW = 3;

	public static final long validTimeDuration = 600000; // 10 minutes

	public static final int CONNECT_ERROR = 10001;
	public static final int SUCCESS_SEARCH = 10002;
	public static final int SEARCH_GUEST_NULL = 10003;
	public static final int UNBIND_SUCCESS = 10004;
	public static final int UNBIND_FAIL = 10005;
	public static final int NULL_FEEDBAKC_CONTENT = 10006;
	public static final int NO_SELECT_CHILDREN = 10007;
	public static final int GRANT_SUCCESS = 10008;
	public static final int UPDATE_PASSWORD_SUCCESS = 10008;
	public static final int TWO_DIFFERENT_PASSWORD_SUCCESS = 10009;
	public static final int OLD_PASSWORD_ERROR = 10010;
	public static final int PASSWORD_FORMAT_ERROR = 10011;
	public static final int PASSWORD_RESET_SUCCESS = 10012;
	public static final int ACCOUNT_NOT_EXIST = 10013;
	public static final int FINISH_WRITE_MAJOR_CHARA = 10014;
	public static final int CHILD_EXIST = 10015;
	public static final int ALREADY_RELATIONSHIP = 10016;
	public static final int WRONG_LOGIN = 10017;
	public static final int MASTER_OF_CHILD_ALREAD_EXIST = 10018;
	public static final int ACCOUNT_DO_NOT_HAS_THIS_CHILD = 10019;
	public static final int UPDATE_NICKNAME_SUCCESS = 10020;
	public static final int UPDATE_NICKNAME_FAIL_WRONG_PASSWORD = 10021;
	public static final int NULL_FEEDBAKC_PASSWORD = 10022;
	public static final int NULL_FEEDBAKC_NICKNAME = 10023;
	public static final int NULL_FEEDBAKC_NEW_PASSWORD = 10024;
	public static final int NULL_FEEDBAKC_REPEAT_NEW_PASSWORD = 10025;
	public static final int FEEDBACK_DIALOG_CHOOSE_TYPE = 10026;
	public static final int GET_QR_CODE_SUCCESS = 10027;
	public static final int GET_QR_CODE_FAIL = 10028;
	
	public static final String EYEBB_FOLDER = Environment
			.getExternalStorageDirectory() + "/eyebb/";
}
