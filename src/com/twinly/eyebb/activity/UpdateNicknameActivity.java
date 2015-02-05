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
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.RegularExpression;
import com.twinly.eyebb.utils.SharePrefsUtils;

/**
 * @author eyebb team
 * 
 * @category UpdateNicknameActivity
 * 
 *           UpdateNicknameActivity can let the user change the nickname after
 *           fill in the correct password, its position is in options activity
 *           (The ninth layer)
 * 
 */
public class UpdateNicknameActivity extends Activity {

	private EditText etNewNickname;
	private EditText etPassword;

	private Button btnConfrim;

	private String newNickname = "";
	private String password = "";

	Toast toast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_update_nickname));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_update_nickname);

		etNewNickname = (EditText) findViewById(R.id.et_new_nickname);
		etPassword = (EditText) findViewById(R.id.et_password);
		btnConfrim = (Button) findViewById(R.id.btn_confirm);

		btnConfrim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(postUpdateNicknameToServerRunnable).start();
			}
		});
	}

	Runnable postUpdateNicknameToServerRunnable = new Runnable() {
		@Override
		public void run() {
			newNickname = etNewNickname.getText().toString();
			password = etPassword.getText().toString();

			if (newNickname.length() > 0 && password.length() > 0) {
				if (RegularExpression.isPassword(password)) {
					postUpdateNicknameToServer();
				} else {
					Message msg = handler.obtainMessage();
					msg.what = Constants.PASSWORD_FORMAT_ERROR;
					handler.sendMessage(msg);
				}
			} else if (newNickname.length() > 0) {
				Message msg = handler.obtainMessage();
				msg.what = Constants.NULL_FEEDBAKC_PASSWORD;
				handler.sendMessage(msg);
			} else if (password.length() > 0) {
				Message msg = handler.obtainMessage();
				msg.what = Constants.NULL_FEEDBAKC_NICKNAME;
				handler.sendMessage(msg);
			} else {
				Message msg = handler.obtainMessage();
				msg.what = Constants.NULL_FEEDBAKC_NICKNAME;
				handler.sendMessage(msg);
			}

		}
	};

	private void postUpdateNicknameToServer() {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println("info=>" + newNickname + " " + password);

		map.put("password", CommonUtils.getSHAHashValue(password));
		map.put("newNickname", newNickname);

		try {
			String retStr = HttpRequestUtils.post(
					HttpConstants.CHANGE_NICKNAME, map);
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
					msg.what = Constants.UPDATE_NICKNAME_SUCCESS;
					handler.sendMessage(msg);
					SharePrefsUtils.setUserName(UpdateNicknameActivity.this,
							newNickname);
					setResult(ActivityConstants.RESULT_UPDATE_NICKNAME_SUCCESS);
					finish();
				} else if (retStr.equals(HttpConstants.SERVER_RETURN_F)) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.UPDATE_NICKNAME_FAIL_WRONG_PASSWORD;
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
			case Constants.UPDATE_NICKNAME_SUCCESS:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_update_nickname_successful,
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

			case Constants.NULL_FEEDBAKC_NICKNAME:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_fill_in_nickname, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;

			case Constants.UPDATE_NICKNAME_FAIL_WRONG_PASSWORD:
				Intent intent = new Intent(UpdateNicknameActivity.this,
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
