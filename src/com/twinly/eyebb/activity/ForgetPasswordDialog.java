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
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
/**
 * @author eyebb team
 * 
 * @category ForgetPasswordDialog
 * 
 *           this activity is used when you fill in the child`s information
 *           (during the sign-up time). you should finish 3 parts. The first is
 *           child`s name. The second is child`s birthday. The third is child`s
 *           kindergarten.
 * 
 */
public class ForgetPasswordDialog extends Activity {

	private String userAccout = "";
	private String birthday = "";
	private String child_name = "";
	private LinearLayout btn_confirm;
	private LinearLayout btn_cancel;
	private EditText edAccount;
	private EditText edchild_name;
	private TextView txchild_birthday;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_forget_password);

		edAccount = (EditText) findViewById(R.id.enter_account);
		edchild_name = (EditText) findViewById(R.id.enter_child_name);

		txchild_birthday = (TextView) findViewById(R.id.enter_child_birthday);
		edAccount.setFocusable(true);
		edAccount.setFocusableInTouchMode(true);
		edAccount.requestFocus();

		// get child birthday
		txchild_birthday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (CommonUtils.isFastDoubleClick()) {
					return;
				} else {
					/**
					 * hide keyboard
					 */

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(
							txchild_birthday.getWindowToken(), 0);

					Intent intent = new Intent(ForgetPasswordDialog.this,
							ChildBirthdayDialog.class);
					if (birthday != null) {
						intent.putExtra("birthday", birthday);
					}
					startActivityForResult(intent,
							ActivityConstants.REQUEST_GO_TO_BIRTHDAY_ACTIVITY);
				}
			}
		});

		btn_confirm = (LinearLayout) findViewById(R.id.btn_confirm);

		btn_confirm.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {

				child_name = edchild_name.getText().toString();
				userAccout = edAccount.getText().toString();
				if (birthday.length() > 0 && child_name.length() > 0
						&& userAccout.length() > 0) {
					new Thread(postResetPasswordToServerRunnable).start();
				} else {
					Message msg = handler.obtainMessage();
					msg.what = Constants.NULL_FEEDBAKC_CONTENT;
					handler.sendMessage(msg);
				}

			}
		});

		btn_cancel = (LinearLayout) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	Runnable postResetPasswordToServerRunnable = new Runnable() {
		@Override
		public void run() {

			postResetPasswordToServer();
		}

	};

	@SuppressLint("ShowToast")
	private void postResetPasswordToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out.println("ResetPassword=>" + userAccout + " " + child_name
				+ " " + birthday);

		map.put("accName", userAccout);
		map.put("childName", child_name);
		map.put("dob", birthday);

		try {
			String retStr = HttpRequestUtils.post(HttpConstants.RESET_PASSWORD,
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
					Message msg = handler.obtainMessage();
					msg.what = Constants.PASSWORD_RESET_SUCCESS;
					handler.sendMessage(msg);

				} else if (retStr.equals(HttpConstants.SERVER_RETURN_F)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.ACCOUNT_NOT_EXIST;
					handler.sendMessage(msg);
				} else if (retStr.equals(HttpConstants.SERVER_RETURN_NC)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.ACCOUNT_DO_NOT_HAS_THIS_CHILD;
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
			Toast toast = null;
			switch (msg.what) {

			case Constants.CONNECT_ERROR:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_network_error, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;
			case Constants.PASSWORD_RESET_SUCCESS:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_feed_back_successful, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.NULL_FEEDBAKC_CONTENT:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_fill_in_something, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.ACCOUNT_NOT_EXIST:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_user_do_not_exist, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				break;

			case Constants.ACCOUNT_DO_NOT_HAS_THIS_CHILD:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_account_user_do_not_have_this_child,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;
			}

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ActivityConstants.REQUEST_GO_TO_BIRTHDAY_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_BIRTHDAY_OK) {
				txchild_birthday.setText(data.getStringExtra("childBirthday"));
				birthday = data.getStringExtra("childBirthday");
			}
		}
	}

}
