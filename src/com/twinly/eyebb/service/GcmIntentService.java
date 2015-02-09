package com.twinly.eyebb.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.twinly.eyebb.activity.WebViewActivity;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.GCMConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBActivityInfo;
import com.twinly.eyebb.database.DBNotifications;
import com.twinly.eyebb.model.ActivityInfo;
import com.twinly.eyebb.model.Notifications;
import com.twinly.eyebb.utils.NotificationUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class GcmIntentService extends IntentService {

	private static final String TAG = "GcmIntentService";

	public GcmIntentService() {
		super("GcmIntentService");
		Log.i(TAG, "Start GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		String request = bundle.getString(GCMConstants.GCM_COMMAND);

		// if receive nothing, return
		if (TextUtils.isEmpty(request)) {
			return;
		}

		Log.i(TAG, "Received message: " + request);

		if (request.equals(GCMConstants.GCM_NEW_ACTIVITY)) {
			handleNewActivity(bundle);
		} else if (request.equals(GCMConstants.GCM_NEW_NOTICE)) {
			handleNewNotice(bundle);
		}

		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void handleNewActivity(Bundle bundle) {
		ActivityInfo activityInfo = new ActivityInfo();
		activityInfo.setChildId(SharePrefsUtils.getReportChildId(this, -1L));
		activityInfo.setTitle(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE));
		activityInfo
				.setTitleSc(bundle
						.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_SC));
		activityInfo
				.setTitleTc(bundle
						.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_TC));
		activityInfo.setUrl(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL));
		activityInfo.setUrlSc(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL_SC));
		activityInfo.setUrlTc(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_URL_TC));
		activityInfo.setDate(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_DATE));
		activityInfo.setIcon(bundle
				.getString(HttpConstants.JSON_KEY_REPORT_ACTIVITY_INFO_ICON));
		DBActivityInfo.insert(this, activityInfo);

		Intent notificationIntent = new Intent(getApplicationContext(),
				WebViewActivity.class);
		Bundle data = new Bundle();
		data.putInt("from", ActivityConstants.FRAGMENT_REPORT_ACTIVITY);
		data.putSerializable("activityInfo", activityInfo);
		notificationIntent.putExtras(bundle);

		String title = "";
		switch (SharePrefsUtils.getLanguage(this)) {
		case Constants.LOCALE_CN:
			title = activityInfo.getTitleSc();
			break;
		case Constants.LOCALE_TW:
			title = activityInfo.getTitleTc();
			break;
		case Constants.LOCALE_HK:
			title = activityInfo.getTitleTc();
			break;
		default:
			title = activityInfo.getTitle();
			break;
		}

		NotificationUtils
				.sendNotification(notificationIntent, getApplicationContext(),
						WebViewActivity.class, title, "", true);
	}

	private void handleNewNotice(Bundle bundle) {
		Notifications notice = new Notifications();
		notice.setTitle(bundle.getString(HttpConstants.JSON_KEY_NOTICES_TITLE));
		notice.setTitleTc(bundle
				.getString(HttpConstants.JSON_KEY_NOTICES_TITLE_TC));
		notice.setTitleSc(bundle
				.getString(HttpConstants.JSON_KEY_NOTICES_TITLE_SC));
		notice.setUrl(bundle.getString(HttpConstants.JSON_KEY_NOTICES_NOTICE));
		notice.setUrlTc(bundle
				.getString(HttpConstants.JSON_KEY_NOTICES_NOTICE_TC));
		notice.setUrlSc(bundle
				.getString(HttpConstants.JSON_KEY_NOTICES_NOTICE_SC));
		notice.setIcon(bundle.getString(HttpConstants.JSON_KEY_NOTICES_ICON));
		notice.setDate(bundle
				.getString(HttpConstants.JSON_KEY_NOTICES_VALID_UNTIL));
		DBNotifications.insert(this, notice);

		Intent notificationIntent = new Intent(getApplicationContext(),
				WebViewActivity.class);
		Bundle data = new Bundle();
		data.putInt("from", ActivityConstants.FRAGMENT_PROFILE);
		data.putSerializable("notifications", notice);
		notificationIntent.putExtras(data);

		String title = "";
		switch (SharePrefsUtils.getLanguage(this)) {
		case Constants.LOCALE_CN:
			title = notice.getTitleSc();
			break;
		case Constants.LOCALE_TW:
			title = notice.getTitleTc();
			break;
		case Constants.LOCALE_HK:
			title = notice.getTitleTc();
			break;
		default:
			title = notice.getTitle();
			break;
		}

		NotificationUtils
				.sendNotification(notificationIntent, getApplicationContext(),
						WebViewActivity.class, title, "", true);
	}
}
