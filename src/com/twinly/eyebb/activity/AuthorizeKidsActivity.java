package com.twinly.eyebb.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.GuestListViewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.customview.LinearLayoutForListView;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.User;
import com.twinly.eyebb.utils.HttpRequestUtils;

/**
 * @author eyebb team
 * 
 * @category AuthorizeKidsActivity
 * 
 *           this activity is in options activity (The fifth layer), it shows
 *           the list of other users who are authorized by master. you also can
 *           find which master people authorizes you on list.
 */

public class AuthorizeKidsActivity extends Activity {
	// private LinearLayoutForListView guest_listView;
	// private LinearLayoutForListView master_listView;
	private LinearLayoutForListView listView;
	private GuestListViewAdapter guestAdapter;

	// private Button btnAddNewGuest;
	private ArrayList<User> authToGuestData;
	private ArrayList<User> authFromMasterData;
	private ArrayList<Child> authFromMasterChildrenData;

	private String retStr;

	public static final int UPDATE_VIEW = 11111;
	private Dialog authDialog;
	private Runnable postFindGuestsToServerRunnable;
	public CircleImageView avatar;
	public TextView name;
	public TextView phone;
	public RelativeLayout btn_guest_view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.btn_auth_list));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_authorize_guest_list);

		authDialog = LoadingDialog.createLoadingDialog(
				AuthorizeKidsActivity.this, getString(R.string.toast_loading));
		authDialog.show();
		postFindGuestsToServerRunnable = new Runnable() {

			@Override
			public void run() {
				postFindGuestsToServer();
			}
		};
		new Thread(postFindGuestsToServerRunnable).start();

		listView = (LinearLayoutForListView) findViewById(R.id.listView_authorization_from_others);

		authToGuestData = new ArrayList<User>();
		authFromMasterData = new ArrayList<User>();
		authFromMasterChildrenData = new ArrayList<Child>();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			System.out.println("=========>onKeyDown");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void postFindGuestsToServer() {
		try {
			retStr = HttpRequestUtils.get(HttpConstants.AUTH_FIND_GUESTS, null);
			// System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.length() > 0) {
					Message msg = handler.obtainMessage();
					msg.what = UPDATE_VIEW;
					handler.sendMessage(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private ArrayList<User> parseGuestJson(String getData) {
		try {
			authToGuestData.clear();
			if (!JSONObject.NULL.equals(getData)) {
				// guest_data.clear();
				boolean isGusetNull = new JSONObject(getData)
						.isNull(HttpConstants.JSON_KEY_GUESTS);
				if (!isGusetNull) {
					JSONArray guests = new JSONObject(getData)
							.getJSONArray(HttpConstants.JSON_KEY_GUESTS);
					if (guests.length() > 0) {
						for (int i = 0; i < guests.length(); i++) {
							JSONObject guest = ((JSONObject) guests.opt(i))
									.getJSONObject(HttpConstants.JSON_KEY_USER);

							User guestMode = new User();

							guestMode.setGuardianId(guest
									.getString(HttpConstants.JSON_KEY_USER_ID));
							guestMode
									.setName(guest
											.getString(HttpConstants.JSON_KEY_USER_NAME));
							guestMode
									.setPhoneNumber(guest
											.getString(HttpConstants.JSON_KEY_USER_PHONE));
							guestMode
									.setType(guest
											.getString(HttpConstants.JSON_KEY_USER_TYPE));

							authToGuestData.add(guestMode);
						}
					}
				}
			}

			// System.out.println("guest_data>" + authToGuestData.size());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return authToGuestData;
	}

	private ArrayList<User> parseMasterJson(String getData) {
		try {
			authFromMasterData.clear();
			authFromMasterChildrenData.clear();
			if (!JSONObject.NULL.equals(getData)) {
				boolean isMasterNull = new JSONObject(getData)
						.isNull(HttpConstants.JSON_KEY_MASTERS);
				if (!isMasterNull) {
					JSONArray masters = new JSONObject(getData)
							.getJSONArray(HttpConstants.JSON_KEY_MASTERS);

					if (masters.length() > 0) {
						for (int i = 0; i < masters.length(); i++) {
							JSONObject master = ((JSONObject) masters.opt(i))
									.getJSONObject(HttpConstants.JSON_KEY_USER);

							User masterMode = new User();

							masterMode.setGuardianId(master
									.getString(HttpConstants.JSON_KEY_USER_ID));
							masterMode
									.setName(master
											.getString(HttpConstants.JSON_KEY_USER_NAME));
							masterMode
									.setPhoneNumber(master
											.getString(HttpConstants.JSON_KEY_USER_PHONE));
							masterMode
									.setType(master
											.getString(HttpConstants.JSON_KEY_USER_TYPE));

							authFromMasterData.add(masterMode);

							JSONArray master_children = ((JSONObject) masters
									.opt(i))
									.getJSONArray(HttpConstants.JSON_KEY_CHILDREN_BY_GUARDIAN);

							for (int j = 0; j < master_children.length(); j++) {
								JSONObject master_child_json = master_children
										.getJSONObject(j);

								Child master_child = new Child();

								master_child
										.setChildId(master_child_json
												.getLong(HttpConstants.JSON_KEY_CHILD_ID));
								master_child
										.setName(master_child_json
												.getString(HttpConstants.JSON_KEY_CHILD_NAME));
								master_child
										.setIcon(master_child_json
												.getString(HttpConstants.JSON_KEY_CHILD_ICON));

								/**
								 * keep USER id
								 */
								master_child
										.setPhone(master
												.getString(HttpConstants.JSON_KEY_USER_ID));

								authFromMasterChildrenData.add(master_child);
							}
						}
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return authFromMasterData;
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.CONNECT_ERROR:
				Toast.makeText(AuthorizeKidsActivity.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();

				if (authDialog.isShowing() && authDialog != null) {
					authDialog.dismiss();
				}
				break;

			case Constants.UNBIND_SUCCESS:
				Toast.makeText(AuthorizeKidsActivity.this,
						R.string.text_unbind_success, Toast.LENGTH_LONG).show();

				break;

			case Constants.UNBIND_FAIL:

				Toast.makeText(AuthorizeKidsActivity.this,
						R.string.text_unbind_fail, Toast.LENGTH_LONG).show();

				// parseJson(getData).clear();
				break;

			case UPDATE_VIEW:
				if (authDialog.isShowing() && authDialog != null) {
					authDialog.dismiss();
				}

				guestAdapter = new GuestListViewAdapter(
						AuthorizeKidsActivity.this, parseGuestJson(retStr),
						parseMasterJson(retStr), authFromMasterChildrenData);
				listView.setAdapter(guestAdapter);

				break;
			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.btn_add).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == 0) {
			// Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(AuthorizeKidsActivity.this,
					SearchGuestActivity.class);
			startActivityForResult(intent,
					ActivityConstants.REQUEST_GO_TO_SEARCH_GUEST_ACTIVITY);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_SEARCH_GUEST_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				finish();
			}
		}
	}

}
