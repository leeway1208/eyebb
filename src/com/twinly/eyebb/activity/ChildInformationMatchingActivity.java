package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class ChildInformationMatchingActivity extends Activity {
	private EditText userName;
	private LinearLayout childBirthdayLayout;
	private RelativeLayout kindergartenItem;
	private TextView childBirthday;
	private TextView kindergarten;
	private Button binding;

	private int kindergartenId = -1;
	private String childName;
	private String birthday;

	private boolean childNameFlag = false;
	private boolean birthdayFlag = false;
	private boolean kindergartenFlag = false;

	private ImageView icKindergarten;
	private TextView icChildName;
	private TextView icBirthday;

	public static final int CONNECT_ERROR = 1;
	public static final int CHILD_EXIST = 2;
	public static final int CHILD_NOT_EXIST = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_information_matching);
		setTitle(getString(R.string.text_child_information_matching));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		initView();
	}

	private void initView() {
		userName = (EditText) findViewById(R.id.ed_username);
		childBirthdayLayout = (LinearLayout) findViewById(R.id.child_birthday_layout);
		kindergartenItem = (RelativeLayout) findViewById(R.id.kindergartenItem);
		kindergarten = (TextView) findViewById(R.id.kindergarten);
		childBirthday = (TextView) findViewById(R.id.birthday);
		binding = (Button) findViewById(R.id.btn_confirm);
		icKindergarten = (ImageView) findViewById(R.id.ic_kindergarten);
		icChildName = (TextView) findViewById(R.id.ic_child_name);
		icBirthday = (TextView) findViewById(R.id.ic_birthday);

		kindergartenItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						ChildInformationMatchingActivity.this,
						KindergartenListActivity.class);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_KINDERGARTEN_ACTIVITY);
			}
		});

		childBirthdayLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtils.isFastDoubleClick()) {
					return;
				} else {
					/**
					 * hide keyboard
					 */

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(
							childBirthdayLayout.getWindowToken(), 0);

					Intent intent = new Intent(
							ChildInformationMatchingActivity.this,
							ChildBirthdayDialog.class);
					if (birthday != null) {
						intent.putExtra("birthday", birthday);
						System.out.println("birthday--child-->" + birthday);
					}
					startActivityForResult(intent,
							ActivityConstants.REQUEST_GO_TO_BIRTHDAY_ACTIVITY);
				}
			}
		});

		binding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				childName = userName.getText().toString();
				if (childName != null && childName.length() > 0) {
					childNameFlag = true;
					icChildName.setBackground(getResources().getDrawable(
							R.drawable.ic_login_name));
				} else {
					childNameFlag = false;
					setTitle(getString(R.string.text_something_has_gone_wrong));
					icChildName.setBackground(getResources().getDrawable(
							R.drawable.ic_cross));
				}

				if (birthday != null && birthday.length() > 0) {
					birthdayFlag = true;
					icBirthday.setBackground(getResources().getDrawable(
							R.drawable.ic_login_email));
				} else {
					birthdayFlag = false;
					setTitle(getString(R.string.text_something_has_gone_wrong));
					icBirthday.setBackground(getResources().getDrawable(
							R.drawable.ic_cross));
				}

				if (kindergartenId >= 0) {
					kindergartenFlag = true;
					//setTitle(getString(R.string.text_something_has_gone_wrong));
					icKindergarten.setVisibility(View.INVISIBLE);
				} else {
					kindergartenFlag = false;
					setTitle(getString(R.string.text_something_has_gone_wrong));
					icKindergarten.setVisibility(View.VISIBLE);

				}

				if (kindergartenFlag && birthdayFlag && childNameFlag) {
					new Thread(postRegParentsCheckToServerRunnable).start();
					// startActivity(intent);
				}

			}
		});

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
		if (requestCode == ActivityConstants.REQUEST_GO_TO_KINDERGARTEN_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				kindergarten.setText(data.getStringExtra("displayName"));
				kindergartenId = data.getIntExtra("kindergartenId", -1);
			}
		}
		if (requestCode == ActivityConstants.REQUEST_GO_TO_BIRTHDAY_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_BIRTHDAY_OK) {
				childBirthday.setText(data.getStringExtra("childBirthday"));
				birthday = data.getStringExtra("childBirthday");
			}
		}
	}

	Runnable postRegParentsCheckToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postChildInformationToServer();
		}
	};

	private void postChildInformationToServer() {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println("username=>" + childName + " " + birthday + " "
				+ kindergartenId);

		map.put("childName", childName);
		map.put("dateOfBirth", birthday);
		map.put("kId", String.valueOf(kindergartenId));

		try {
			String retStr = HttpRequestUtils.post(HttpConstants.CHILD_CHECKING,
					map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals("[]")) {
					Message msg = handler.obtainMessage();
					msg.what = CHILD_NOT_EXIST;
					handler.sendMessage(msg);
				} else {
					Intent intent = new Intent(
							ChildInformationMatchingActivity.this,
							CheckChildToBindDialog.class);
					intent.putExtra(
							ActivityConstants.EXTRA_GUARDIAN_ID,
							getIntent().getLongExtra(
									ActivityConstants.EXTRA_GUARDIAN_ID, -1L));
					intent.putExtra(CheckChildToBindDialog.EXTRA_CHILDREN_LIST,
							retStr);
					startActivity(intent);
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
			case CHILD_EXIST:
				break;
			case CONNECT_ERROR:
				Toast.makeText(ChildInformationMatchingActivity.this,
						R.string.text_network_error, Toast.LENGTH_LONG).show();
				break;
			case CHILD_NOT_EXIST:
				Toast.makeText(ChildInformationMatchingActivity.this,
						R.string.text_child_not_exist, Toast.LENGTH_LONG)
						.show();
				break;
			}
		}
	};

}
