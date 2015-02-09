package com.twinly.eyebb.dialog;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class FeedbackDialog extends Activity {

	private LinearLayout btnConfirm;
	private LinearLayout btnCancel;
	private String content;
	private EditText ed;
	private RadioGroup group;
	private int radioButtonId;
	private String type = "";
	private TextView textLast;

	public static final int SUCCESS_FEEDBACK = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_feedback);

		btnConfirm = (LinearLayout) findViewById(R.id.btn_confirm);
		btnCancel = (LinearLayout) findViewById(R.id.btn_cancel);
		ed = (EditText) findViewById(R.id.feedback_comments);
		group = (RadioGroup) findViewById(R.id.feedback_rg);
		textLast = (TextView) findViewById(R.id.text_last);

		ed.addTextChangedListener(new TextWatcher() {
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
				editStart = ed.getSelectionStart();
				editEnd = ed.getSelectionEnd();
				textLast.setText("(" + temp.length() + "/144)");
				if (temp.length() > 144) {
					s.delete(editStart - 1, editEnd);
					int tempSelection = editStart;
					ed.setText(s);
					ed.setSelection(tempSelection);
				}
			}
		});

		ed.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (ed.hasFocus() == true) {
					ed.setHint("");
				} else {
					ed.setHint(getResources().getString(
							R.string.text_your_comments));
				}

			}
		});

		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				radioButtonId = checkedId;
				switch (checkedId) {
				// case R.id.radio_bug:
				// type = "B";
				// break;

				case R.id.radio_idea:
					type = "I";
					break;

				case R.id.radio_question:
					type = "Q";
					break;

				}
				// 根据ID获取RadioButton的实例
				System.out.println("radioButtonId>" + radioButtonId + "--"
						+ checkedId);
			}
		});

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				content = ed.getText().toString();

				if (content.length() > 0 && type.length() > 0) {
					new Thread(postFeedBackToServerRunnable).start();
				} else if (content.length() <= 0 && type.length() == 0) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.NULL_FEEDBAKC_CONTENT;
					handler.sendMessage(msg);
				} else if (content.length() > 0 && type.length() == 0) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.FEEDBACK_DIALOG_CHOOSE_TYPE;
					handler.sendMessage(msg);
				}

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
			}
		});

	}

	Runnable postFeedBackToServerRunnable = new Runnable() {
		@Override
		public void run() {
			postFeedBackRelationToServer();

		}
	};

	@SuppressLint("ShowToast")
	private void postFeedBackRelationToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		System.out.println("info=>" + content + " " + radioButtonId + "");

		map.put("content", content);
		map.put("type", type);

		try {
			String retStr = HttpRequestUtils.post(HttpConstants.FEED_BACK, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.equals("true")) {
					Message msg = handler.obtainMessage();
					msg.what = SUCCESS_FEEDBACK;
					handler.sendMessage(msg);

					finish();
				} else if (retStr.equals("false")) {
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
			Toast toast = null;
			switch (msg.what) {

			case Constants.CONNECT_ERROR:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_network_error, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;
			case SUCCESS_FEEDBACK:
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

			case Constants.FEEDBACK_DIALOG_CHOOSE_TYPE:
				toast = Toast.makeText(getApplicationContext(),
						R.string.text_choose_your_feedback_type,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;
			}

		}
	};
}
