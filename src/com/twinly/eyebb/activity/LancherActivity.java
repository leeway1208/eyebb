package com.twinly.eyebb.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.GCMUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;
import com.twinly.eyebb.utils.SystemUtils;

/**
 * @author eyebb team
 * 
 * @category LancherActivity
 * 
 *           this activity is used to restart the activity.
 * 
 */
public class LancherActivity extends Activity {
	private ImageView logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lancher);

		SystemUtils.initImageLoader(getApplicationContext());
		checkLogo();
		setLanguage();
		mkdir();
		new HttpRequestUtils();

		if (SharePrefsUtils.isAntiLostOn(this)) {
			Intent intent = new Intent(LancherActivity.this, MainActivity.class);
			intent.putExtra(MainActivity.EXTRA_NEED_LOGIN, true);
			startActivity(intent);
			finish();
		} else {
			if (SharePrefsUtils.isLogin(this)) {
				new AutoLoginTask()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				Intent intent = new Intent(this, WelcomeActivity.class);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_WELCOME_ACTIVITY);
				finish();
			}
		}

	}

	private void checkLogo() {
		logo = (ImageView) findViewById(R.id.icon);
		switch (SharePrefsUtils.getLanguage(this)) {
		case Constants.LOCALE_TW:
		case Constants.LOCALE_HK:
		case Constants.LOCALE_CN:
			logo.setBackgroundResource(R.drawable.logo_cht);
			break;
		default:
			logo.setBackgroundResource(R.drawable.logo_en);
			break;
		}
	}

	private void setLanguage() {
		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		switch (SharePrefsUtils.getLanguage(this)) {
		case Constants.LOCALE_TW:
		case Constants.LOCALE_HK:
		case Constants.LOCALE_CN:
			config.locale = Locale.TRADITIONAL_CHINESE;
			resources.updateConfiguration(config, dm);
			break;
		default:
			config.locale = Locale.ENGLISH;
			resources.updateConfiguration(config, dm);
			break;
		}
	}

	private void mkdir() {
		File floder = new File(Constants.EYEBB_FOLDER);
		if (floder.exists() == false) {
			floder.mkdirs();
		}
	}

	private class AutoLoginTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("j_username",
					SharePrefsUtils.getLoginAccount(LancherActivity.this));
			map.put("j_password",
					SharePrefsUtils.getPassword(LancherActivity.this));

			return HttpRequestUtils.post(HttpConstants.LOGIN, map);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("auto login result = " + result);
			try {
				JSONObject json = new JSONObject(result);
				String receivedDeviceId = json
						.getString(HttpConstants.JSON_KEY_REGISTRATION_ID);

				json = json.getJSONObject(HttpConstants.JSON_KEY_USER);

				SharePrefsUtils.setUserId(LancherActivity.this,
						json.getLong(HttpConstants.JSON_KEY_USER_ID));
				SharePrefsUtils.setUserName(LancherActivity.this,
						json.getString(HttpConstants.JSON_KEY_USER_NAME));
				SharePrefsUtils.setUserPhone(LancherActivity.this,
						json.getString(HttpConstants.JSON_KEY_USER_PHONE));
				SharePrefsUtils.setUserType(LancherActivity.this,
						json.getString(HttpConstants.JSON_KEY_USER_TYPE));

				new GCMUtils().GCMRegistration(LancherActivity.this,
						receivedDeviceId);

				new GetChildrenInfoTask()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (JSONException e) {
				goBackToLogin();
			}
		}
	}

	private class GetChildrenInfoTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			return HttpRequestUtils.get(HttpConstants.GET_CHILDREN_INFO_LIST,
					null);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println(HttpConstants.GET_CHILDREN_INFO_LIST + " ==>> "
					+ result);
			try {
				JSONObject json = new JSONObject(result);
				JSONArray childrenInfoJSONList = json
						.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_INFO);
				for (int i = 0; i < childrenInfoJSONList.length(); i++) {
					JSONObject childrenInfoObject = childrenInfoJSONList
							.getJSONObject(i);
					JSONObject childRelObject = childrenInfoObject
							.getJSONObject(HttpConstants.JSON_KEY_CHILD_REL);
					JSONObject childObject = childRelObject
							.getJSONObject(HttpConstants.JSON_KEY_CHILD);

					Child child = new Child(
							childObject.getInt(HttpConstants.JSON_KEY_CHILD_ID),
							childObject
									.getString(HttpConstants.JSON_KEY_CHILD_NAME),
							childObject
									.getString(HttpConstants.JSON_KEY_CHILD_ICON));
					child.setRelationWithUser(childRelObject
							.getString(HttpConstants.JSON_KEY_CHILD_RELATION));
					child.setMacAddress(childrenInfoObject
							.getString(HttpConstants.JSON_KEY_CHILD_MAC_ADDRESS));
					// get parents' phone
					if (CommonUtils.isNotNull(childrenInfoObject
							.getString(HttpConstants.JSON_KEY_PARENTS))) {
						JSONObject parentObject = childrenInfoObject
								.getJSONObject(HttpConstants.JSON_KEY_PARENTS);
						child.setPhone(parentObject
								.getString(HttpConstants.JSON_KEY_PARENTS_PHONE));
					}
					DBChildren.insert(LancherActivity.this, child);
				}

				Intent intent = new Intent(LancherActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			} catch (JSONException e) {
				goBackToLogin();
			}
		}
	}

	private void goBackToLogin() {
		Toast.makeText(this, getString(R.string.toast_login_failed),
				Toast.LENGTH_LONG).show();
		Intent intent = new Intent(LancherActivity.this, WelcomeActivity.class);
		startActivityForResult(intent,
				ActivityConstants.REQUEST_GO_TO_WELCOME_ACTIVITY);
		finish();
	}
}
