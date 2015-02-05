package com.twinly.eyebb.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.utils.SharePrefsUtils;

/**
 * @author eyebb team
 * 
 * @category WelcomeActivity
 * 
 *           WelcomeActivity shows when you first open the eyebb app. It
 *           contains two functions, one is for sign-up(SignUpActivity) and the other is for
 *           login(LoginActivity).
 * 
 */
public class WelcomeActivity extends Activity {
	private ImageView logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		logo = (ImageView) findViewById(R.id.icon);
		checkLogo();
		findViewById(R.id.sign_up).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this,
						SignUpActivity.class);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_SIGN_UP_ACTIVITY);
			}
		});

		findViewById(R.id.login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_LOGIN_ACTIVITY);
			}
		});

	}

	@SuppressLint("NewApi")
	private void checkLogo() {
		switch (SharePrefsUtils.getLanguage(this)) {
		case Constants.LOCALE_TW:
		case Constants.LOCALE_HK:
		case Constants.LOCALE_CN:
			logo.setBackground(getResources().getDrawable(R.drawable.logo_cht));
			break;
		default:
			logo.setBackground(getResources().getDrawable(R.drawable.logo_en));
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_SIGN_UP_ACTIVITY
				|| requestCode == ActivityConstants.REQUEST_GO_TO_LOGIN_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}

}
