package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.RegularExpression;

/**
 * @author eyebb team
 * 
 * @category UpdatePasswordActivity
 * 
 *           UpdatePasswordActivity can let the user change the password, its
 *           position is in options activity (The eighth layer)
 * 
 */
public class UpdatePasswordActivity extends Activity {

	private EditText etOldPassword;
	private EditText etNewPassword;
	private EditText etNewRepeatPassword;

	private Button btnConfrim;

	private String oldPassword;
	private String newPassword;
	private String newRepeatPassword;
	Toast toast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_update_password));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_update_password);

		etOldPassword = (EditText) findViewById(R.id.et_old_password);
		etNewPassword = (EditText) findViewById(R.id.et_new_password);
		etNewRepeatPassword = (EditText) findViewById(R.id.et_repeat_new_password);
		btnConfrim = (Button) findViewById(R.id.btn_confirm);

		btnConfrim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(postUpdatePasswordToServerRunnable).start();
			}
		});
	}

	Runnable postUpdatePasswordToServerRunnable = new Runnable() {
		@Override
		public void run() {
			oldPassword = etOldPassword.getText().toString();
			newPassword = etNewPassword.getText().toString();
			newRepeatPassword = etNewRepeatPassword.getText().toString();

			if (oldPassword.length() > 0 && newPassword.length() > 0
					&& newRepeatPassword.length() > 0) {
				if (RegularExpression.isPassword(newPassword)) {
					if (RegularExpression.isPassword(newRepeatPassword))
						if (newPassword.equals(newRepeatPassword)) {
							postUpdatePasswordToServer();
						} else {
							Message msg = handler.obtainMessage();
							msg.what = Constants.TWO_DIFFERENT_PASSWORD_SUCCESS;
							handler.sendMessage(msg);
						}
					else {
						Message msg = handler.obtainMessage();
						msg.what = Constants.PASSWORD_FORMAT_ERROR;
						handler.sendMessage(msg);
					}
				} else {
					Message msg = handler.obtainMessage();
					msg.what = Constants.PASSWORD_FORMAT_ERROR;
					handler.sendMessage(msg);
				}

			} else if (newPassword.length() > 0 && oldPassword.length() <= 0) {
				Message msg = handler.obtainMessage();
				msg.what = Constants.NULL_FEEDBAKC_PASSWORD;
				handler.sendMessage(msg);
			} else if (oldPassword.length() > 0 && newPassword.length() <= 0
					&& newRepeatPassword.length() <= 0) {
				Message msg = handler.obtainMessage();
				msg.what = Constants.NULL_FEEDBAKC_NEW_PASSWORD;
				handler.sendMessage(msg);
			} else if (oldPassword.length() > 0 && newPassword.length() > 0
					&& newRepeatPassword.length() <= 0) {
				Message msg = handler.obtainMessage();
				msg.what = Constants.NULL_FEEDBAKC_REPEAT_NEW_PASSWORD;
				handler.sendMessage(msg);
			} else {
				Message msg = handler.obtainMessage();
				msg.what = Constants.NULL_FEEDBAKC_PASSWORD;
				handler.sendMessage(msg);
			}

		}
	};

	private void postUpdatePasswordToServer() {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println("info=>" + oldPassword + " " + newPassword + " "
				+ newRepeatPassword);
		map.put("oldPassword", CommonUtils.getSHAHashValue(oldPassword));
		map.put("newPassword", CommonUtils.getSHAHashValue(newPassword));

		try {
			String retStr = HttpRequestUtils.post(
					HttpConstants.UPDATE_PASSWORD, map);
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
					msg.what = Constants.UPDATE_PASSWORD_SUCCESS;
					handler.sendMessage(msg);

					finish();
				} else if (retStr.equals(HttpConstants.SERVER_RETURN_F)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.OLD_PASSWORD_ERROR;
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
			Toast toast = null;
			switch (msg.what) {

			case Constants.CONNECT_ERROR:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_network_error, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;
			case Constants.UPDATE_PASSWORD_SUCCESS:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_update_password_successful,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.NULL_FEEDBAKC_CONTENT:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_fill_in_something, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.NULL_FEEDBAKC_PASSWORD:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_fill_in_password, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.NULL_FEEDBAKC_NEW_PASSWORD:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_fill_in_new_password, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.NULL_FEEDBAKC_REPEAT_NEW_PASSWORD:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_fill_in_repeat_new_password,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.TWO_DIFFERENT_PASSWORD_SUCCESS:
				toast = Toast
						.makeText(getApplicationContext(),
								R.string.text_two_password_different,
								Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.OLD_PASSWORD_ERROR:
				Intent intent = new Intent(UpdatePasswordActivity.this,
						OldPasswordIncorrectDialog.class);
				startActivity(intent);

				break;

			case Constants.PASSWORD_FORMAT_ERROR:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_error_password, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				break;
			}

		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
