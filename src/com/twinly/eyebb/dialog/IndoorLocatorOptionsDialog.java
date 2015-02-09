package com.twinly.eyebb.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class IndoorLocatorOptionsDialog extends Activity {
	public static final String EXTRA_AUTO_REFRESH = "AUTO_REFRESH";
	public static final String EXTRA_VIEW_ALL_ROOMS = "VIEW_ALL_ROOMS";

	private TextView tvAutoRefresh;
	private TextView tvViewAllRooms;
	private boolean isAutoRefresh;
	private boolean isViewAllRooms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_indoor_locator_options);

		isViewAllRooms = getIntent().getBooleanExtra(EXTRA_VIEW_ALL_ROOMS,
				true);

		tvAutoRefresh = (TextView) findViewById(R.id.tv_auto_refresh);
		tvViewAllRooms = (TextView) findViewById(R.id.tv_view_all_room);

		isAutoRefresh = SharePrefsUtils.isAutoUpdate(this);
		if (isAutoRefresh) {
			tvAutoRefresh.setBackgroundResource(R.drawable.ic_selected);
		}
		if (isViewAllRooms) {
			tvViewAllRooms.setBackgroundResource(R.drawable.ic_selected);
		}

		findViewById(R.id.item_auto_refresh).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (isAutoRefresh) {
							isAutoRefresh = false;
							tvAutoRefresh
									.setBackgroundResource(R.drawable.ic_selected_off);
						} else {
							isAutoRefresh = true;
							tvAutoRefresh
									.setBackgroundResource(R.drawable.ic_selected);
						}

					}
				});

		findViewById(R.id.item_view_all_room).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (isViewAllRooms) {
							isViewAllRooms = false;
							tvViewAllRooms
									.setBackgroundResource(R.drawable.ic_selected_off);
						} else {
							isViewAllRooms = true;
							tvViewAllRooms
									.setBackgroundResource(R.drawable.ic_selected);
						}

					}
				});
		findViewById(R.id.btn_confirm).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent data = new Intent();
						data.putExtra(EXTRA_AUTO_REFRESH, isAutoRefresh);
						data.putExtra(EXTRA_VIEW_ALL_ROOMS, isViewAllRooms);
						SharePrefsUtils.setAutoUpdate(IndoorLocatorOptionsDialog.this,
								isAutoRefresh);
						setResult(ActivityConstants.RESULT_RESULT_OK, data);
						finish();
					}
				});
	}
}
