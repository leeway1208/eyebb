package com.twinly.eyebb.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.widget.Toast;

import com.twinly.eyebb.R;

public class RegularExpression {
	/**
	 * use to match the list view
	 */
	public static String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ字";

	public static boolean getStringToDetectionLetters(char str) {
		// System.out.println(str);
		String regex = "^[A-Za-z]+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str + "");
		if (!matcher.find()) {
			/**
			 * is not letters
			 */
			return true;
		} else {
			/**
			 * is letters
			 */
			return false;
		}
	}
	
	public static boolean isUsername(String usrname) {
		Pattern p = Pattern.compile("^[0-9_]{8,20}$");
		Matcher m = p.matcher(usrname);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	public static boolean isPassword(String password) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9]{6,20}$");
		Matcher m = p.matcher(password);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	public static boolean isEmail(String email) {
		Pattern p = Pattern
				.compile("^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$");
		Matcher m = p.matcher(email);
		System.out.println(m.matches() + "---");
		return m.matches();
	}
	
	public static String getValidMacAddress(Context context, String macAddress) {
		macAddress = macAddress.toUpperCase(Locale.US);
		Pattern p = Pattern
				.compile("^\\w{2}:\\w{2}:\\w{2}:\\w{2}:\\w{2}:\\w{2}$");
		Matcher m = p.matcher(macAddress);
		if (m.matches() == false) {
			Toast.makeText(context, R.string.text_wrong_format,
					Toast.LENGTH_SHORT).show();
			return null;
		} else {
			return macAddress;
		}
	}
}
