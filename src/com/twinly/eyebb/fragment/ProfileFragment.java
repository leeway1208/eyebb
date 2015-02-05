package com.twinly.eyebb.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.LancherActivity;
import com.twinly.eyebb.activity.SettingsActivity;
import com.twinly.eyebb.activity.WebViewActivity;
import com.twinly.eyebb.adapter.NotificationsListViewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBNotifications;
import com.twinly.eyebb.dialog.FeedbackDialog;
import com.twinly.eyebb.model.Notifications;
import com.twinly.eyebb.utils.BroadcastUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class ProfileFragment extends Fragment {

	private TextView settingBtn;
	private View notificationDetailsBtn;
	private ArrayList<Notifications> list;
	private ListView listView;
	private NotificationsListViewAdapter adapter;

	int child;

	private ProfileFragment profileFragment;
	private View v;

	private UpdateTheOptionsUi updateTheOptionsUi;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_profile, container, false);
		listView = (ListView) v.findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), WebViewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("from", ActivityConstants.FRAGMENT_PROFILE);
				bundle.putSerializable("notifications",
						(Notifications) adapter.getItem(position));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		/**
		 * FORMAT nickname(username)
		 */
		((TextView) v.findViewById(R.id.username)).setText(SharePrefsUtils
				.getUserName(getActivity()));

		settingBtn = (TextView) v.findViewById(R.id.options_btn);
		settingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (RadarFragment.radarState == BluetoothAdapter.STATE_ON
						|| RadarFragment.radarState == BluetoothAdapter.STATE_TURNING_ON) {
					Toast.makeText(
							getActivity(),
							getResources().getString(
									R.string.text_can_not_open_options),
							Toast.LENGTH_LONG).show();

				} else {
					Intent intent = new Intent(getActivity(),
							SettingsActivity.class);
					startActivityForResult(intent,
							ActivityConstants.REQUEST_GO_TO_SETTING_ACTIVITY);
				}

			}
		});

		notificationDetailsBtn = v.findViewById(R.id.notification_details_btn);

		notificationDetailsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(getActivity(), FeedbackDialog.class);

				startActivity(intent);
			}
		});
		updateView();
		return v;
	}

	/**
	 * broadcast whether the radar function is open or not receiver
	 * 
	 * 
	 */
	private class UpdateTheOptionsUi extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastUtils.BROADCAST_OPEN_RADAR)) {
				settingBtn.setAlpha(0.3f);
				System.out.println("BROADCAST_OPEN_RADAR");
			} else if (action.equals(BroadcastUtils.BROADCAST_CLOSE_RADAR)) {
				settingBtn.setAlpha(1.0f);
			}
		}
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateTheOptionsUi = new UpdateTheOptionsUi();
		getActivity().registerReceiver(updateTheOptionsUi,
				new IntentFilter(BroadcastUtils.BROADCAST_OPEN_RADAR));
		getActivity().registerReceiver(updateTheOptionsUi,
				new IntentFilter(BroadcastUtils.BROADCAST_CLOSE_RADAR));

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// unregisterReceiver
		try {
			getActivity().unregisterReceiver(updateTheOptionsUi);
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("Receiver not registered")) {
				// Ignore this exception. This is exactly what is desired
			} else {
				// unexpected, re-throw
				throw e;
			}
		}
	}

	public void refreshProfileFragment() {
		if (profileFragment != null) {
			// performanceFragment.updateAdapter();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new UpdateView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void updateView() {
		list = DBNotifications.getNotifications(getActivity());
		adapter = new NotificationsListViewAdapter(getActivity(), list);
		listView.setAdapter(adapter);
	}

	private class UpdateView extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String result = HttpRequestUtils.get(HttpConstants.GET_NOTICES,
					null);
			try {
				new JSONObject(result);
			} catch (JSONException e) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					result = HttpRequestUtils.get(HttpConstants.GET_NOTICES,
							null);
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("notice = " + result);
			try {
				JSONObject json = new JSONObject(result);
				JSONArray array = json
						.getJSONArray(HttpConstants.JSON_KEY_NOTICES);
				DBNotifications.clear(getActivity());
				for (int i = 0; i < array.length(); i++) {
					JSONObject JSONNotice = array.getJSONObject(i);
					Notifications notice = new Notifications();
					notice.setTitle(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_TITLE));
					notice.setTitleTc(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_TITLE_TC));
					notice.setTitleSc(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_TITLE_SC));
					notice.setUrl(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_NOTICE));
					notice.setUrlTc(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_NOTICE_TC));
					notice.setUrlSc(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_NOTICE_SC));
					notice.setIcon(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_ICON));
					notice.setDate(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_VALID_UNTIL));
					DBNotifications.insert(getActivity(), notice);
				}
				updateView();
			} catch (JSONException e) {
				System.out.println(HttpConstants.GET_NOTICES + e.getMessage());
			}
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_SETTING_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_LOGOUT) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), LancherActivity.class);
				startActivity(intent);
				getActivity().finish();
			} else if (resultCode == ActivityConstants.RESULT_UPDATE_NICKNAME_SUCCESS) {
				((TextView) v.findViewById(R.id.username))
						.setText(SharePrefsUtils.getUserName(getActivity()));
				System.out.println("UPDATE NICK NAME!!!");
			}
		}
	}
}
