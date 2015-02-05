package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.TabsAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.fragment.IndoorLocatorFragment;
import com.twinly.eyebb.fragment.ProfileFragment;
import com.twinly.eyebb.fragment.RadarFragment;
import com.twinly.eyebb.fragment.ReportFragment;
import com.twinly.eyebb.utils.BroadcastUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
/**
 * @author eyebb team
 * 
 * @category MainActivity
 * 
 *           this activity is the main framework, which uses for four fragment activities.
 * 
 */
public class MainActivity extends FragmentActivity implements
		ReportFragment.CallbackInterface,
		IndoorLocatorFragment.CallbackInterface {
	public static final String EXTRA_NEED_LOGIN = "NEED_LOGIN";

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;
	private IndoorLocatorFragment indoorLocatorFragment;
	private ReportFragment reportFragment;
	private ProfileFragment profileFragment;

	private LinearLayout networkBar;
	private TextView networkState;
	private SmoothProgressBar progressBar;
	private SmoothProgressBar bar;
	private boolean isRefreshing;
	private KeepSessionAliveTask keepSessionAliveTask;
	private int timeoutCounter;
	private View profileLabel;
	private HandleNotificationDot handleNotificationDot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setUpTab(savedInstanceState);
		setUpProgressBar();
		setUpNetworkBar();

		keepSessionAliveTask = new KeepSessionAliveTask();
		keepSessionAliveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected void onResume() {
		super.onResume();
		bar.setVisibility(View.INVISIBLE);

		handleNotificationDot = new HandleNotificationDot();
		registerReceiver(handleNotificationDot, new IntentFilter(
				BroadcastUtils.BROADCAST_ADD_NOTIFICATION_DOT));
		registerReceiver(handleNotificationDot, new IntentFilter(
				BroadcastUtils.BROADCAST_CANCEL_NOTIFICATION_DOT));

		// set the current tab
		if (SharePrefsUtils.isAntiLostOn(this)) {
			mTabHost.setCurrentTab(1);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		System.out.println("onCreate ==>> onSaveInstanceState");
		outState.putString("tab", mTabHost.getCurrentTabTag());
		//super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		System.out.println("MainActivity ==>> " + "onDestroy");
		if (keepSessionAliveTask != null) {
			keepSessionAliveTask.cancel(true);
		}

		//unregisterReceiver
		try {
			unregisterReceiver(handleNotificationDot);
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("Receiver not registered")) {
				// Ignore this exception. This is exactly what is desired
			} else {
				// unexpected, re-throw
				throw e;
			}
		}
		super.onDestroy();
	}

	@SuppressLint("InflateParams")
	private void setUpTab(Bundle savedInstanceState) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);
		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		indoorLocatorFragment = new IndoorLocatorFragment();
		indoorLocatorFragment.setCallbackInterface(this);
		// main
		View mainLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		mainLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_home_selector);
		mainLabel.findViewById(R.id.notification_number).setVisibility(
				View.GONE);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Main").setIndicator(mainLabel),
				indoorLocatorFragment);

		// radar
		//radarTrackingFragment = new RadarFragment();
		RadarFragment temp = new RadarFragment();
		View trackingLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		trackingLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_tracking_selector);
		trackingLabel.findViewById(R.id.notification_number).setVisibility(
				View.GONE);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Radar").setIndicator(trackingLabel), temp);

		// report
		reportFragment = new ReportFragment();
		reportFragment.setCallbackInterface(this);
		View reportLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		reportLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_report_selector);
		reportLabel.findViewById(R.id.notification_number).setVisibility(
				View.GONE);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Report").setIndicator(reportLabel),
				reportFragment);

		// profile
		profileFragment = new ProfileFragment();
		profileLabel = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_label, null);
		profileLabel.findViewById(R.id.label).setBackgroundResource(
				R.drawable.btn_actbar_profile_selector);
		mTabsAdapter.addFragment(
				mTabHost.newTabSpec("Profile").setIndicator(profileLabel),
				profileFragment);

		if (SharePrefsUtils.isNotificationDot(MainActivity.this)) {

		} else {
			profileLabel.findViewById(R.id.notification_number).setVisibility(
					View.GONE);
		}

		/*if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}*/

	}

	private void setUpProgressBar() {
		bar = (SmoothProgressBar) findViewById(R.id.bar);
		bar.setVisibility(View.INVISIBLE);

		progressBar = (SmoothProgressBar) findViewById(R.id.progressBar);
		ShapeDrawable shape = new ShapeDrawable();
		shape.setShape(new RectShape());
		shape.getPaint().setColor(
				getResources().getColor(R.color.spb_default_color));
		ClipDrawable clipDrawable = new ClipDrawable(shape, Gravity.CENTER,
				ClipDrawable.HORIZONTAL);
		progressBar.setProgressDrawable(clipDrawable);

		progressBar.setVisibility(View.INVISIBLE);
		progressBar.setProgress(0);
		progressBar.setIndeterminate(false);
	}

	private void setUpNetworkBar() {
		networkBar = (LinearLayout) findViewById(R.id.network_bar);
		networkState = (TextView) findViewById(R.id.network_state);

		networkBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AutoLoginTask()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});

		if (getIntent().getBooleanExtra(EXTRA_NEED_LOGIN, false)) {
			new AutoLoginTask()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	private class AutoLoginTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			networkBar.setEnabled(false);
			networkState.setText(getString(R.string.text_network_connecting));
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("j_username",
					SharePrefsUtils.getLoginAccount(MainActivity.this));
			map.put("j_password",
					SharePrefsUtils.getPassword(MainActivity.this));

			return HttpRequestUtils.post(HttpConstants.LOGIN, map);
		}

		@Override
		protected void onPostExecute(String result) {
			networkBar.setEnabled(true);
			networkState.setText(getString(R.string.text_network_unavailable));
			System.out.println("auto login result = " + result);
			try {
				new JSONObject(result);
				networkBar.setVisibility(View.GONE);
			} catch (JSONException e) {
				networkBar.setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void updateProgressBarForIndoorLocator(int value) {
		updateProgressBar(value, 0);
	}

	@Override
	public void updateProgressBarForReport(int value) {
		updateProgressBar(value, 1);
	}

	private void updateProgressBar(int value, int fragmentId) {
		if (isRefreshing == false) {
			progressBar.setVisibility(View.VISIBLE);
			progressBar.setProgress(progressBar.getProgress() + value);
			if (progressBar.getProgress() >= 100) {
				isRefreshing = true;
				bar.setVisibility(View.VISIBLE);
				bar.progressiveStart();
				switch (fragmentId) {
				case 0:
					indoorLocatorFragment.updateView();
					break;
				case 1:
					reportFragment.updateView();
					break;
				}
			}
		}
	}

	@Override
	public void cancelProgressBar() {
		if (isRefreshing == false) {
			progressBar.setProgress(0);
		}
	}

	@Override
	public void resetProgressBar() {
		isRefreshing = false;
		progressBar.setProgress(0);
		bar.progressiveStop();
		// reportFragment.setRefreshing(false);
	}

	private class KeepSessionAliveTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (true) {
				try {
					Thread.sleep(600000); // 10min = 600000s
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("KeepSessionAliveTask runs once --->>>");
				this.publishProgress(HttpRequestUtils.get(
						"reportService/api/refreshSession", null));
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			if (values[0].equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
				timeoutCounter++;
				if (timeoutCounter == 2) {
					finish();
				}
			} else {
				timeoutCounter = 0;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_WELCOME_ACTIVITY) {
			if (resultCode != ActivityConstants.RESULT_RESULT_OK) {
				finish();
			}
		}
	}

	/**
	 *  broadcast notificaiton dot receiver 
	 *  
	 *  
	 */
	private class HandleNotificationDot extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastUtils.BROADCAST_ADD_NOTIFICATION_DOT)) {
				profileLabel.findViewById(R.id.notification_number)
						.setVisibility(View.VISIBLE);
				SharePrefsUtils.setNotificationDot(MainActivity.this, true);

			} else if (action
					.equals(BroadcastUtils.BROADCAST_CANCEL_NOTIFICATION_DOT)) {
				profileLabel.findViewById(R.id.notification_number)
						.setVisibility(View.GONE);
				SharePrefsUtils.setNotificationDot(MainActivity.this, false);
			}
		}
	};

}
