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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.GrantKidsListViewFromGuestAdapter;
import com.twinly.eyebb.adapter.GrantKidsListViewFromMasterAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.model.ChildForGrant;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.woozzu.android.widget.IndexableListView;

public class GrantKidsActivity extends Activity {
	private IndexableListView listView;
	private GrantKidsListViewFromGuestAdapter guest_adapter;
	private GrantKidsListViewFromMasterAdapter master_adapter;
	private ArrayList<ChildForGrant> returnList;
	private ArrayList<ChildForGrant> childList;
	private String guestChildrenRetStr;

	private String guestdId;
	private String guestName;
	private String grantChildId;
	private String from_master_or_guest;
	private String noAccessGrantChildId;
	private boolean from_where = false;
	public static final int UPDATE_VIEW = 11111;
	private ArrayList<ChildForGrant> new_children_data;

	/**
	 * @author eyebb team
	 * 
	 * @category GrantKidsActivity
	 * 
	 *           this activity is used to grant child (in options, fifth layer-
	 *           authorization list). It shows a list that can be chosen. Then
	 *           showing which child is be granted.
	 */

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 
		 * from_where comes form which search activity
		 * 
		 */
		Intent intent = getIntent();
		new_children_data = new ArrayList<ChildForGrant>();
		guestdId = intent.getStringExtra("guestId");
		guestName = intent.getStringExtra("guestName");

		from_where = intent
				.getBooleanExtra("from_search_guest_activity", false);

		if (!from_where) {
			from_master_or_guest = intent.getStringExtra("from_where");
			if (from_master_or_guest.equals("master")) {
				new_children_data = (ArrayList<ChildForGrant>) intent
						.getSerializableExtra("child_data");
			}
		}

		setTitle(getString(R.string.text_auth_to_user) + guestName);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.dialog_grant_kids_list);

		listView = (IndexableListView) findViewById(R.id.listView);
		listView.setFastScrollEnabled(true);
		childList = new ArrayList<ChildForGrant>();
		returnList = new ArrayList<ChildForGrant>();
		new Thread(postGuestChildrenToServerRunnable).start();

	}

	Runnable postGuestChildrenToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postGuestChildrenToServer();

		}
	};

	private ArrayList<ChildForGrant> parseChildJson(String getData) {
		try {
			childList.clear();
			if (!JSONObject.NULL.equals(getData)) {
				// guest_data.clear();
				boolean isChildNull = new JSONObject(getData)
						.isNull(HttpConstants.JSON_KEY_CHILDREN_QUOTA);
				if (!isChildNull) {
					JSONArray children = new JSONObject(getData)
							.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_QUOTA);
					if (children.length() > 0) {
						for (int i = 0; i < children.length(); i++) {
							JSONObject child = ((JSONObject) children.opt(i))
									.getJSONObject(HttpConstants.JSON_KEY_CHILD);

							ChildForGrant childForGrant = new ChildForGrant();
							System.out
									.println("--->"
											+ child.getString(HttpConstants.JSON_KEY_CHILD_ID));
							System.out
									.println("--->"
											+ child.getString(HttpConstants.JSON_KEY_CHILD_NAME));
							System.out
									.println("--->"
											+ child.getString(HttpConstants.JSON_KEY_CHILD_ICON));

							System.out
									.println("--->"
											+ ((JSONObject) children.opt(i))
													.getBoolean(HttpConstants.JSON_KEY_WITH_ACCESS));
							System.out
									.println("--->"
											+ ((JSONObject) children.opt(i))
													.getString(HttpConstants.JSON_KEY_TOTAL_QUOTA));
							System.out
									.println("--->"
											+ ((JSONObject) children.opt(i))
													.getString(HttpConstants.JSON_KEY_QUOTA_LEFT));
							System.out
									.println("--------------------------------------");

							childForGrant
									.setChildId(Long.valueOf(child
											.getString(HttpConstants.JSON_KEY_CHILD_ID)));
							childForGrant
									.setName(child
											.getString(HttpConstants.JSON_KEY_CHILD_NAME));
							childForGrant
									.setIcon(child
											.getString(HttpConstants.JSON_KEY_CHILD_ICON));
							childForGrant
									.setWithAccess(((JSONObject) children
											.opt(i))
											.getBoolean(HttpConstants.JSON_KEY_WITH_ACCESS));
							childForGrant
									.setTotalQuota(((JSONObject) children
											.opt(i))
											.getString(HttpConstants.JSON_KEY_TOTAL_QUOTA));

							childForGrant
									.setQuotaLeft(((JSONObject) children.opt(i))
											.getString(HttpConstants.JSON_KEY_QUOTA_LEFT));

							childList.add(childForGrant);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return childList;
	}

	@SuppressLint("ShowToast")
	private void postGuestChildrenToServer() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("guestId", guestdId);

		try {
			guestChildrenRetStr = HttpRequestUtils.post(
					HttpConstants.GUEST_CHILDREN, map);
			System.out.println("guestchildren======>" + guestChildrenRetStr);
			if (guestChildrenRetStr
					.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| guestChildrenRetStr.equals("")
					|| guestChildrenRetStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (guestChildrenRetStr.length() > 0) {
					Message msg = handler.obtainMessage();
					msg.what = UPDATE_VIEW;
					handler.sendMessage(msg);

				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	Runnable postGrantToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postGrantToServer();

		}
	};

	@SuppressLint("ShowToast")
	private void postGrantToServer() {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println("info=>" + guestdId + " ");

		map.put("guestId", guestdId);
		if (grantChildId.length() > 0) {
			map.put("accessChildIds",
					grantChildId.substring(0, grantChildId.length() - 1));

		} else {
			map.put("accessChildIds", "");
		}

		if (noAccessGrantChildId.length() > 0) {
			map.put("noAccessChildIds",
					noAccessGrantChildId.substring(0,
							noAccessGrantChildId.length() - 1));
		} else {
			map.put("noAccessChildIds", "");
		}

		try {
			String retStr = HttpRequestUtils.post(HttpConstants.GRANT_GUESTS,
					map);
			System.out.println("grant======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals(HttpConstants.SERVER_RETURN_T)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.GRANT_SUCCESS;
					handler.sendMessage(msg);

				} else if (retStr.equals(HttpConstants.SERVER_RETURN_F)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.CONNECT_ERROR;
					handler.sendMessage(msg);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.CONNECT_ERROR:
				Toast.makeText(GrantKidsActivity.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				break;

			case Constants.GRANT_SUCCESS:
				Toast.makeText(GrantKidsActivity.this,
						R.string.text_grant_success, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(GrantKidsActivity.this,
						AuthorizeKidsActivity.class);
				startActivity(intent);
				setResult(ActivityConstants.RESULT_RESULT_OK);
				finish();
				break;

			case Constants.NO_SELECT_CHILDREN:
				Toast.makeText(GrantKidsActivity.this,
						R.string.text_select_child, Toast.LENGTH_LONG).show();

				break;

			case UPDATE_VIEW:
				if (!from_where) {
					if (from_master_or_guest.equals("master")) {
						master_adapter = new GrantKidsListViewFromMasterAdapter(
								GrantKidsActivity.this, new_children_data);
						listView.setAdapter(master_adapter);
					} else if (from_master_or_guest.equals("guest")) {
						returnList = parseChildJson(guestChildrenRetStr);
						guest_adapter = new GrantKidsListViewFromGuestAdapter(
								GrantKidsActivity.this, returnList);
						listView.setAdapter(guest_adapter);
					}
				} else {
					returnList = parseChildJson(guestChildrenRetStr);
					guest_adapter = new GrantKidsListViewFromGuestAdapter(
							GrantKidsActivity.this, returnList);
					listView.setAdapter(guest_adapter);
				}

				break;

			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!from_where) {
			if (from_master_or_guest.equals("guest")) {
				menu.add(0, 0, 0, R.string.btn_confirm).setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			}
		} else {
			menu.add(0, 0, 0, R.string.btn_confirm).setShowAsAction(
					MenuItem.SHOW_AS_ACTION_IF_ROOM
							| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (from_where) {
				finish();
			} else {
				Intent intent = new Intent(GrantKidsActivity.this,
						AuthorizeKidsActivity.class);
				startActivity(intent);
				finish();
			}

			return true;
		} else if (item.getItemId() == 0) {
			grantChildId = "";
			noAccessGrantChildId = "";
			for (int i = 0; i < GrantKidsListViewFromGuestAdapter.grantkidId
					.size(); i++) {
				grantChildId += GrantKidsListViewFromGuestAdapter.grantkidId
						.get(i).toString() + ",";
			}
			for (int i = 0; i < GrantKidsListViewFromGuestAdapter.noAccessGrantkidId
					.size(); i++) {
				noAccessGrantChildId += GrantKidsListViewFromGuestAdapter.noAccessGrantkidId
						.get(i).toString() + ",";
			}

			System.out.println("grantChildId-->" + grantChildId
					+ "    nograntChildId--->" + noAccessGrantChildId);

			new Thread(postGrantToServerRunnable).start();

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (from_where) {
				finish();
			} else {
				Intent intent = new Intent(GrantKidsActivity.this,
						AuthorizeKidsActivity.class);
				startActivity(intent);
				finish();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
