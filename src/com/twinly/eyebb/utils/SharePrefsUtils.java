package com.twinly.eyebb.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.twinly.eyebb.constant.ActivityConstants;

public class SharePrefsUtils {

	public static void clear(Context context) {
		getPrefs(context).edit().clear().commit();
	}

	public static String getLoginAccount(Context context) {
		return getString(context,
				ActivityConstants.SHARE_PREFS_ITEM_LOGIN_ACCOUNT);
	}

	public static void setLoginAccount(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_LOGIN_ACCOUNT,
				value);
	}

	public static String getPassword(Context context) {
		return getString(context, ActivityConstants.SHARE_PREFS_ITEM_PASSWORD);
	}

	public static void setPassowrd(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_PASSWORD, value);
	}

	public static Boolean isLogin(Context context) {
		return getBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_IS_LOGIN,
				false);
	}

	public static void setLogin(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_IS_LOGIN, value);
	}

	public static String getUserType(Context context) {
		return getString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_TYPE);
	}

	public static void setUserType(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_TYPE, value);
	}

	public static String getUserName(Context context) {
		return getString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_NAME);
	}

	public static void setUserName(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_NAME, value);
	}

	public static String getUserPhone(Context context) {
		return getString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_PHONE);
	}

	public static void setUserPhone(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_USER_PHONE, value);
	}

	public static long getUserId(Context context, long defValue) {
		return getLong(context, ActivityConstants.SHARE_PREFS_ITEM_USER_ID,
				defValue);
	}

	public static void setUserId(Context context, long value) {
		setLong(context, ActivityConstants.SHARE_PREFS_ITEM_USER_ID, value);
	}

	public static long getReportChildId(Context context, long defValue) {
		return getLong(context,
				ActivityConstants.SHARE_PREFS_ITEM_REPORT_CHILD_ID, defValue);
	}

	public static void setReportChildId(Context context, long value) {
		setLong(context, ActivityConstants.SHARE_PREFS_ITEM_REPORT_CHILD_ID,
				value);
	}

	public static boolean isAutoUpdate(Context context) {
		return getBoolean(context,
				ActivityConstants.SHARE_PREFS_ITEM_AUDO_UPDATE, false);
	}

	public static void setAutoUpdate(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_AUDO_UPDATE,
				value);
	}

	public static boolean isSoundOn(Context context) {
		return getBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_SOUND,
				true);
	}

	public static void setSoundOn(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_SOUND, value);
	}

	public static boolean isVibrateOn(Context context) {
		return getBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_VIBRATE,
				true);
	}

	public static void setVibrateOn(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_VIBRATE, value);
	}

	public static int getLanguage(Context context) {
		int language = getInt(context,
				ActivityConstants.SHARE_PREFS_ITEM_LANGUAGE);
		if (language < 0) {
			language = SystemUtils.getLocale(context);
			setLanguage(context, language);
		}
		return language;
	}

	public static void setLanguage(Context context, int value) {
		setInt(context, ActivityConstants.SHARE_PREFS_ITEM_LANGUAGE, value);
	}

	public static Long getAutoUpdateTime(Context context, long defValue) {
		return getLong(context,
				ActivityConstants.SHARE_PREFS_ITEM_AUTO_UPDATE_TIME, defValue);
	}

	public static void setAutoUpdateTime(Context context, Long value) {
		setLong(context, ActivityConstants.SHARE_PREFS_ITEM_AUTO_UPDATE_TIME,
				value);
	}

	public static int getAppVersion(Context context) {
		return getInt(context, ActivityConstants.SHARE_PREFS_ITEM_APP_VERSION);
	}

	public static void setAppVersion(Context context, int value) {
		setInt(context, ActivityConstants.SHARE_PREFS_ITEM_APP_VERSION, value);
	}

	public static String getDeviceId(Context context) {
		String deviceId = getString(context,
				ActivityConstants.SHARE_PREFS_ITEM_DEVICE_ID);
		if (TextUtils.isEmpty(deviceId)) {
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = getAppVersion(context);
		int currentVersion = SystemUtils.getAppVersion(context);
		if (registeredVersion != currentVersion) {
			return "";
		}

		return deviceId;
	}

	public static void setDeviceId(Context context, String value) {
		setString(context, ActivityConstants.SHARE_PREFS_ITEM_DEVICE_ID, value);
	}

	public static boolean isNotificationDot(Context context) {
		return getBoolean(context,
				ActivityConstants.SHARE_PREFS_ITEM_NOTIFICATION_DOT, false);
	}

	public static void setNotificationDot(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_NOTIFICATION_DOT,
				value);
	}

	public static boolean isAntiLostOn(Context context) {
		return getBoolean(context,
				ActivityConstants.SHARE_PREFS_ITEM_IS_ANTI_LOST_ON, false);
	}

	public static void setAntiLostOn(Context context, boolean value) {
		setBoolean(context, ActivityConstants.SHARE_PREFS_ITEM_IS_ANTI_LOST_ON,
				value);
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(
				ActivityConstants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	private static String getString(Context context, String name) {
		return getPrefs(context).getString(name, "");
	}

	private static boolean getBoolean(Context context, String name,
			boolean defaultValue) {
		return getPrefs(context).getBoolean(name, defaultValue);
	}

	private static int getInt(Context context, String name) {
		return getPrefs(context).getInt(name, Integer.MIN_VALUE);
	}

	private static long getLong(Context context, String name, long defValue) {
		return getPrefs(context).getLong(name, defValue);
	}

	private static void setString(Context context, String name, String value) {
		getPrefs(context).edit().putString(name, value).commit();
	}

	private static void setBoolean(Context context, String name, boolean value) {
		getPrefs(context).edit().putBoolean(name, value).commit();
	}

	private static void setInt(Context context, String name, int value) {
		getPrefs(context).edit().putInt(name, value).commit();
	}

	private static void setLong(Context context, String name, long value) {
		getPrefs(context).edit().putLong(name, value).commit();
	}

}
