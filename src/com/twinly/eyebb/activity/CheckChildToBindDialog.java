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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.qr_codescan.MipcaActivityCapture;
import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.CheckChildToBindAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.RegularExpression;

/**
 * @author eyebb team
 * 
 * @category CheckChildToBindDialog
 * 
 *           this activity is used for when you sign up your account and you
 *           want to bind a device. So you should select a child, after you fill
 *           in the child`s information. Then, this child dialog will be
 *           displayed
 * 
 */
public class CheckChildToBindDialog extends Activity {
	private final static int SCANNIN_GREQUEST_CODE = 1;
	public static String EXTRA_CHILDREN_LIST = "CHILDREN_LIST";

	private ListView listView;
	private CheckChildToBindAdapter adapter;
	private String childrenListJSON;
	private ArrayList<Child> childList;
	private long childIdToPost;
	private String childIcon;
	private long guardianId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_check_child_to_bind_list);
		Intent intent = getIntent();

		childrenListJSON = intent.getStringExtra(EXTRA_CHILDREN_LIST);
		guardianId = intent.getLongExtra(ActivityConstants.EXTRA_GUARDIAN_ID,
				-1L);

		listView = (ListView) findViewById(R.id.listView);
		parseJson(childrenListJSON);
		adapter = new CheckChildToBindAdapter(this, childList);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				childIdToPost = childList.get(position).getChildId();
				childIcon = childList.get(position).getIcon();
				new Thread(postCheckChildIsBindToServerRunnable).start();
			}
		});

	}

	private void parseJson(String getData) {
		childList = new ArrayList<Child>();
		try {
			childList.clear();
			JSONArray arr = new JSONArray(getData);
			for (int i = 0; i < arr.length(); i++) {
				JSONObject object = (JSONObject) arr.get(i);
				Child child = new Child(
						object.getInt(HttpConstants.JSON_KEY_CHILD_ID),
						object.getString(HttpConstants.JSON_KEY_CHILD_NAME),
						object.getString(HttpConstants.JSON_KEY_CHILD_ICON));
				childList.add(child);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	Runnable postCheckChildIsBindToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postCheckChildIsBindToServer();
		}
	};

	private void postCheckChildIsBindToServer() {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println("childId = " + childIdToPost + " guardianId = "
				+ guardianId);

		map.put("childId", String.valueOf(childIdToPost));
		map.put("guardianId",
				guardianId == -1L ? "" : String.valueOf(guardianId));

		try {
			String retStr = HttpRequestUtils.post(HttpConstants.CHILD_GUA_REL,
					map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals(HttpConstants.SERVER_RETURN_T)) {
					Intent intent = new Intent(CheckChildToBindDialog.this,
							MipcaActivityCapture.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
				} else if (retStr.equals(HttpConstants.SERVER_RETURN_F)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.MASTER_OF_CHILD_ALREAD_EXIST;
					handler.sendMessage(msg);
				} else if (retStr.equals(HttpConstants.SERVER_RETURN_WG)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.WRONG_LOGIN;
					handler.sendMessage(msg);
				} else if (retStr.substring(0, 1).equals(
						HttpConstants.SERVER_RETURN_E)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.ALREADY_RELATIONSHIP;
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
				Toast.makeText(CheckChildToBindDialog.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();
				break;
			case Constants.ALREADY_RELATIONSHIP:
				Toast.makeText(CheckChildToBindDialog.this,
						R.string.text_already_relationship, Toast.LENGTH_LONG)
						.show();
				adapter.notifyDataSetChanged();
				break;
			case Constants.WRONG_LOGIN:
				Toast.makeText(CheckChildToBindDialog.this,
						R.string.text_wrong_login_for_binding,
						Toast.LENGTH_LONG).show();
				adapter.notifyDataSetChanged();
				break;
			case Constants.MASTER_OF_CHILD_ALREAD_EXIST:
				Toast.makeText(CheckChildToBindDialog.this,
						R.string.text_master_of_the_child_exist_already,
						Toast.LENGTH_LONG).show();
				adapter.notifyDataSetChanged();
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				System.out.println("qrcode------->"
						+ bundle.getString("result"));
				String macAddress = bundle.getString("result");
				macAddress = RegularExpression.getValidMacAddress(this,
						macAddress);
				if (macAddress != null) {
					Intent intent = new Intent();
					intent.setClass(this, BindingChildMacaronActivity.class);
					intent.putExtra(ActivityConstants.EXTRA_FROM,
							ActivityConstants.ACTIVITY_CHECK_CHILD_TO_BIND);
					intent.putExtra(ActivityConstants.EXTRA_GUARDIAN_ID,
							guardianId);
					intent.putExtra(ActivityConstants.EXTRA_CHILD_ID,
							childIdToPost);
					intent.putExtra(ActivityConstants.EXTRA_CHILD_ICON,
							childIcon);
					intent.putExtra(ActivityConstants.EXTRA_MAC_ADDRESS,
							macAddress);
					startActivity(intent);
				}

			}
			break;
		}
	}
}