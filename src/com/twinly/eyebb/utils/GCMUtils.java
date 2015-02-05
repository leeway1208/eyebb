package com.twinly.eyebb.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;

public class GCMUtils {
	public final static String TAG = "GCMUtils";
	private int gcmRegisterRetrySeconds = 5;
	private int gcmRegisterRetryTimes = 1;

	/**
	 * Check whether need to register to GCM server
	 * @param context application's context.
	 * @param receivedDeviceId server stored device id
	 */
	public void GCMRegistration(Context context, String receivedDeviceId) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		if (CommonUtils.isNull(receivedDeviceId)) {
			new RegisterInBackground(context, gcm)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else if (SharePrefsUtils.getDeviceId(context)
				.equals(receivedDeviceId) == false) {
			new RegisterInBackground(context, gcm)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	public class RegisterInBackground extends AsyncTask<Void, String, String> {
		private Context context;
		private GoogleCloudMessaging gcm;

		public RegisterInBackground(Context context, GoogleCloudMessaging gcm) {
			this.context = context;
			this.gcm = gcm;
		}

		@Override
		protected String doInBackground(Void... params) {
			String msg = "";
			String deviceId;
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				deviceId = gcm.register(Constants.SENDER_ID);
				//SharePrefsUtils.setDevice(context, deviceId);
				//MainActivity.account.setDeviceId(deviceId);

				msg = "Device registered, registration ID=" + deviceId;

				// update the stored device id
				storeRegistrationId(context, deviceId);

				// update registration id to server
				updateRegistrationId(context);

			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
				// If there is an error, don't just keep trying to register.
				// Require the user to click a button again, or perform
				// exponential back-off.
				Log.i(TAG, msg);
				if (gcmRegisterRetryTimes < 6) {
					this.publishProgress(context
							.getString(R.string.toast_register_to_gcm));
					try {
						Log.i(TAG, "The " + gcmRegisterRetryTimes
								+ " time, waiting " + gcmRegisterRetrySeconds
								+ " seconds retry ");
						Thread.sleep(gcmRegisterRetrySeconds * 1000);
						gcmRegisterRetrySeconds *= 2;
						gcmRegisterRetryTimes++;
						new RegisterInBackground(context, gcm).execute();
					} catch (InterruptedException e) {
						Log.i(TAG, e.getMessage());
					}
				} else {
					this.publishProgress(context
							.getString(R.string.toast_register_to_gcm_failed));
				}
				this.cancel(true);
			}
			return msg;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			Toast.makeText(context, values[0], Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i(TAG, result);
		}

	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		SharePrefsUtils.setDeviceId(context, regId);
		int appVersion = SystemUtils.getAppVersion(context);
		SharePrefsUtils.setAppVersion(context, appVersion);
		Log.i(TAG, "Saving regId on app version " + appVersion);
	}

	/**
	 * update registration id in server
	 */
	private void updateRegistrationId(final Context context) {

		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("deviceId", SharePrefsUtils.getDeviceId(context));
				System.out.println("map = " + map);
				return HttpRequestUtils.post(
						HttpConstants.UPDATE_REGISTRATION_ID, map);
			}

			@Override
			protected void onPostExecute(String result) {
				System.out.println("update device id result = " + result);
			}

		}.execute();
	}
}
