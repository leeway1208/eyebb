package com.twinly.eyebb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.twinly.eyebb.R;

/**
 * @author eyebb team
 * 
 * @category OldPasswordIncorrectDialog
 * 
 *           this dialog will tell the user old password is not right when the
 *           user want to changer their password.
 * 
 */
public class OldPasswordIncorrectDialog extends Activity {

	private TextView btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_old_password_error);

		btnBack = (TextView) findViewById(R.id.btn_back);

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}
}
