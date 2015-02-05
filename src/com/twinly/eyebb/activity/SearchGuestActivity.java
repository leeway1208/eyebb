package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.SearchGuestListViewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.model.User;
import com.twinly.eyebb.utils.HttpRequestUtils;

/**
 * @author eyebb team
 * 
 * @category SearchGuestActivity
 * 
 *           this activity is used in AuthorizeKidsActivity. When user click the
 *           add button on top right corner. user will enter this activity.
 *           Then. user should fill in the user name which want to find. At
 *           last, the guest user will show on the list. If there is no this
 *           guest, the activity will display a share button for user. User can
 *           use this button to share to their guest.
 */
public class SearchGuestActivity extends Activity {
	private EditText edGuestname;
	private TextView btnSearchNewGuest;
	private String guestName;

	private SearchGuestListViewAdapter adapter;
	private ListView listView;
	private ArrayList<User> guestData;
	private String retStr;

	private TextView txShare;
	private RelativeLayout btnShare;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.text_authorization));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_search_guest_list);
		listView = (ListView) findViewById(R.id.listView);
		edGuestname = (EditText) findViewById(R.id.ed_guestname);
		btnSearchNewGuest = (TextView) findViewById(R.id.btn_search_new_guest);
		txShare = (TextView) findViewById(R.id.tx_share_two);
		btnShare = (RelativeLayout) findViewById(R.id.btn_share);

		guestData = new ArrayList<User>();

		btnSearchNewGuest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				guestName = edGuestname.getText().toString();

				if (guestName.length() > 0) {
					new Thread(postFeedBackToServerRunnable).start();
				} else {
					Toast.makeText(SearchGuestActivity.this,
							R.string.text_fill_in_something, Toast.LENGTH_LONG)
							.show();
				}

			}
		});

	}

	Runnable postFeedBackToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postFeedBackRelationToServer();

		}
	};

	private ArrayList<User> parseJson(String getData) {
		try {
			if (guestData != null && guestData.size() > 0)
				guestData.clear();
			JSONArray arr = new JSONArray(getData);
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);

				User guest = new User();
				System.out.println("--->"
						+ temp.getString(HttpConstants.JSON_KEY_USER_NAME));
				guest.setGuardianId(Long.parseLong(temp
						.getString(HttpConstants.JSON_KEY_USER_ID)) + "");
				guest.setName(temp.getString(HttpConstants.JSON_KEY_USER_NAME));
				guest.setPhoneNumber(temp
						.getString(HttpConstants.JSON_KEY_USER_PHONE));
				guestData.add(guest);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return guestData;
	}

	@SuppressLint("ShowToast")
	private void postFeedBackRelationToServer() {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println("info=>" + guestName);

		map.put("guestName", guestName);

		try {
			retStr = HttpRequestUtils.get(HttpConstants.SEARCH_GUEST, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.length() > 2) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.SUCCESS_SEARCH;
					handler.sendMessage(msg);

				} else if (retStr.equals("[]")) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.SEARCH_GUEST_NULL;
					handler.sendMessage(msg);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.CONNECT_ERROR:
				Toast.makeText(SearchGuestActivity.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				break;

			case Constants.SUCCESS_SEARCH:
				// Toast.makeText(SearchGuestActivity.this,
				// R.string.text_feed_back_successful, Toast.LENGTH_LONG)
				// .show();
				listView.setVisibility(View.VISIBLE);
				btnShare.setVisibility(View.GONE);
				adapter = new SearchGuestListViewAdapter(
						SearchGuestActivity.this, parseJson(retStr));

				System.out.println("parseJson(retStr)-->"
						+ parseJson(retStr).size());
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						Intent intent = new Intent(SearchGuestActivity.this,
								GrantKidsActivity.class);
						intent.putExtra("guestId",
								parseJson(retStr).get(position).getGuardianId());
						intent.putExtra("guestName",
								parseJson(retStr).get(position).getName());
						intent.putExtra("from_search_guest_activity", true);
						startActivityForResult(
								intent,
								ActivityConstants.REQUEST_GO_TO_GRANT_KIDS_ACTIVITY);

					}
				});
				break;

			case Constants.SEARCH_GUEST_NULL:
				// Toast.makeText(SearchGuestActivity.this,
				// R.string.text_search_guest_null, Toast.LENGTH_LONG)
				// .show();
				txShare.setText(getResources().getString(
						R.string.text_click_to_share)
						+ guestName + ")");
				btnShare.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);

				btnShare.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent shareIntent = new Intent(Intent.ACTION_SEND);
						shareIntent.setType("text/plain");
						shareIntent
								.putExtra(
										Intent.EXTRA_TEXT,
										getString(R.string.text_search_guset_activity_share_msg));
						startActivity(shareIntent);

					}
				});

				break;

			}

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_GRANT_KIDS_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				setResult(ActivityConstants.RESULT_RESULT_OK);
				finish();
			}
		}
	}
}
