package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.GCMUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.RegularExpression;
import com.twinly.eyebb.utils.SharePrefsUtils;
import com.twinly.eyebb.utils.SystemUtils;

/**
 * @author eyebb team
 * 
 * @category SignUpActivity
 * 
 *           SignUpActivity will let the user register their own information.
 *           this information will post to the server.
 */
public class SignUpActivity extends Activity {
	private Button btnSignup;

	private EditText etUsername;
	private EditText etEmail;
	private EditText etPassword;
	private EditText etNickname;
	private String userName;
	private String email;
	private String password;
	private String nickname;
	private String phone;

	private TextView tvUsername;
	private TextView tvEmail;
	private TextView tvPassword;
	private TextView tvNickname;

	private boolean usernameFlag = false;

	public static final int CHECK_ACC_SUCCESS = 1;
	public static final int CHECK_ACC_FALSE = 2;
	public static final int CHECK_ACC_ERROR = 4;
	public static final int CONNECT_ERROR = 3;
	public static final int REG_SUCCESSFULLY = 5;

	private String hashPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		setTitle(getString(R.string.btn_sign_up));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		// username password email
		etUsername = (EditText) findViewById(R.id.et_phone_number);
		etEmail = (EditText) findViewById(R.id.et_email);
		etPassword = (EditText) findViewById(R.id.et_password);
		etNickname = (EditText) findViewById(R.id.et_nickname);

		tvUsername = (TextView) findViewById(R.id.ic_signup_phone);
		tvEmail = (TextView) findViewById(R.id.ic_signup_email);
		tvPassword = (TextView) findViewById(R.id.ic_signup_pw);
		tvNickname = (TextView) findViewById(R.id.ic_signup_nickname);

		btnSignup = (Button) findViewById(R.id.btn_signup);

		etUsername.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (etUsername.hasFocus() == false) {
					userName = etUsername.getText().toString();
					if (RegularExpression.isUsername(userName)) {
						new Thread(postAccNameCheckToServerRunnable).start();

					} else {
						Message msg = handler.obtainMessage();
						msg.what = CHECK_ACC_ERROR;
						handler.sendMessage(msg);
					}
				}

			}
		});

		etUsername.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int editStart;
			private int editEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				editStart = etUsername.getSelectionStart();
				editEnd = etUsername.getSelectionEnd();
				if (temp.length() > 8) {
					s.delete(editStart - 1, editEnd);
					int tempSelection = editStart;
					etUsername.setText(s);
					etUsername.setSelection(tempSelection);
				}

			}
		});

		btnSignup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userName = etUsername.getText().toString();
				email = etEmail.getText().toString();
				password = etPassword.getText().toString();
				nickname = etNickname.getText().toString();
				phone = etUsername.getText().toString();

				if (userName != null && userName.length() > 0) {

					if (nickname != null && nickname.length() > 0) {

						if (RegularExpression.isPassword(password)) {
							if (RegularExpression.isEmail(email)
									|| phone.length() > 0) {
								if (phone != null || phone.length() > 0) {
									if (usernameFlag) {
										new Thread(
												postRegParentsCheckToServerRunnable)
												.start();
									} else {
										new Thread(
												postAccNameCheckToServerRunnable)
												.start();
									}
								}
							} else {

								Toast.makeText(SignUpActivity.this,
										R.string.text_fill_email_or_phone,
										Toast.LENGTH_SHORT).show();
								tvEmail.setBackgroundResource(R.drawable.ic_cross);

							}
						} else {
							Toast.makeText(SignUpActivity.this,
									R.string.text_error_password,
									Toast.LENGTH_SHORT).show();

							tvPassword
									.setBackgroundResource(R.drawable.ic_cross);
						}

					} else {
						Toast.makeText(SignUpActivity.this,
								R.string.text_error_nickname,
								Toast.LENGTH_SHORT).show();
						tvNickname.setBackgroundResource(R.drawable.ic_cross);
					}

				} else {
					Toast.makeText(SignUpActivity.this,
							R.string.text_error_username, Toast.LENGTH_SHORT)
							.show();
					tvUsername.setBackgroundResource(R.drawable.ic_cross);
				}
			}
		});
	}

	Runnable postAccNameCheckToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postCheckAccToServer();
		}
	};

	private void postCheckAccToServer() {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println("username=>" + userName);
		map.put("accName", userName);
		try {
			String retStr = HttpRequestUtils.post(HttpConstants.ACC_NAME_CHECK,
					map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals("true")) {
					Message msg = handler.obtainMessage();
					msg.what = CHECK_ACC_SUCCESS;
					handler.sendMessage(msg);
					usernameFlag = true;
				} else if (retStr.equals("false")) {
					Message msg = handler.obtainMessage();
					msg.what = CHECK_ACC_FALSE;
					handler.sendMessage(msg);
					usernameFlag = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Runnable postRegParentsCheckToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postRegParentsToServer();
		}
	};

	private void postRegParentsToServer() {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println("username=>" + nickname + " " + password + " "
				+ email + " " + phone);

		hashPassword = CommonUtils.getSHAHashValue(password);
		map.put("accName", userName);
		map.put("name", nickname);
		map.put("password", hashPassword);
		map.put("email", email);
		map.put("phoneNum", phone);

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.post(HttpConstants.REG_PARENTS,
					map);
			System.out.println("signUp======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {

				if (!retStr.equals(HttpConstants.SERVER_RETURN_F)
						&& retStr.length() > 0 && retStr.length() < 20) {
					Message msg = handler.obtainMessage();
					msg.what = REG_SUCCESSFULLY;
					handler.sendMessage(msg);

					SystemUtils.clearData(SignUpActivity.this);

					SharePrefsUtils.setLogin(SignUpActivity.this, true);
					SharePrefsUtils.setLoginAccount(SignUpActivity.this,
							userName);
					SharePrefsUtils.setPassowrd(SignUpActivity.this,
							hashPassword);

					// register to GCM server
					new GCMUtils().GCMRegistration(SignUpActivity.this, "");

					Intent intent = new Intent(SignUpActivity.this,
							SignUpAskToBindDialog.class);
					intent.putExtra(ActivityConstants.EXTRA_USER_NAME, userName);
					intent.putExtra(ActivityConstants.EXTRA_HASH_PASSWORD,
							hashPassword);
					intent.putExtra(ActivityConstants.EXTRA_GUARDIAN_ID,
							Long.parseLong(retStr));
					startActivityForResult(
							intent,
							ActivityConstants.REQUEST_GO_TO_SIGNUP_ASK_TO_BIND_DIALOG);

				} else if (retStr.equals(HttpConstants.SERVER_RETURN_F)) {
					Message msg = handler.obtainMessage();
					msg.what = CHECK_ACC_FALSE;
					handler.sendMessage(msg);

				} else {
					Message msg = handler.obtainMessage();
					msg.what = CONNECT_ERROR;
					handler.sendMessage(msg);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case CHECK_ACC_SUCCESS:
				tvUsername.setBackgroundResource(R.drawable.ic_selected);
				break;

			case CHECK_ACC_FALSE:
				Toast.makeText(SignUpActivity.this,
						R.string.text_username_is_used, Toast.LENGTH_SHORT)
						.show();
				tvUsername.setBackgroundResource(R.drawable.ic_cross);
				break;

			case CHECK_ACC_ERROR:
				Toast.makeText(SignUpActivity.this,
						R.string.text_error_username, Toast.LENGTH_SHORT)
						.show();
				tvUsername.setBackgroundResource(R.drawable.ic_cross);
				break;

			case CONNECT_ERROR:
				Toast.makeText(SignUpActivity.this,
						R.string.text_network_error, Toast.LENGTH_SHORT).show();

				break;

			case REG_SUCCESSFULLY:
				Toast.makeText(SignUpActivity.this,
						R.string.text_register_successfully, Toast.LENGTH_SHORT)
						.show();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ActivityConstants.REQUEST_GO_TO_SIGNUP_ASK_TO_BIND_DIALOG:
			setResult(ActivityConstants.RESULT_RESULT_OK);
			finish();
			break;
		}
	}

}
