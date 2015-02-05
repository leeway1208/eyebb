package com.twinly.eyebb.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class CommonUtils {
	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static boolean isNull(String value) {
		if (TextUtils.isEmpty(value)) {
			return true;
		} else if (value.equalsIgnoreCase("null")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNotNull(String value) {
		if (TextUtils.isEmpty(value)) {
			return false;
		} else if (value.equalsIgnoreCase("null")) {
			return false;
		} else {
			return true;
		}
	}

	public static String getSHAHashValue(String password) {
		String passwordSHA256 = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());
			BigInteger bigInt = new BigInteger(1, md.digest());
			passwordSHA256 = bigInt.toString(16).toUpperCase(Locale.US);
		} catch (Exception e) {
			return null;
		}
		return passwordSHA256;
	}

	public static String minutesToHours(int minutes) {
		int hours = minutes / 60;
		minutes = minutes - hours * 60;
		return hours + "hr " + minutes + "min.";
	}

	public static void switchSoftKeyboardstate(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void hideSoftKeyboard(View view, Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void showSoftKeyboard(View view, Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}
}
