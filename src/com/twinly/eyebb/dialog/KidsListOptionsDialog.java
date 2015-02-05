package com.twinly.eyebb.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;

public class KidsListOptionsDialog extends Activity {
	public static final String EXTRA_SORT_BY_NAME = "SORT_BY_NAME ";
	public static final String EXTRA_SORT_BY_LOCATION = "SORT_BY_LOCATION";

	private TextView tvSortByName;
	private TextView tvSortByLocation;
	private boolean isSortByName;
	private boolean isSortByLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_kids_list_options);

		isSortByName = getIntent().getBooleanExtra(EXTRA_SORT_BY_NAME, false);
		isSortByLocation = getIntent().getBooleanExtra(EXTRA_SORT_BY_LOCATION,
				false);

		tvSortByName = (TextView) findViewById(R.id.tv_name);
		tvSortByLocation = (TextView) findViewById(R.id.tv_location);

		if (isSortByName) {
			tvSortByName.setBackgroundResource(R.drawable.ic_selected);
		}
		if (isSortByLocation) {
			tvSortByLocation.setBackgroundResource(R.drawable.ic_selected);
		}

		findViewById(R.id.item_name).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// System.out.println("isSortByNameisSortByName-?" +
				// isSortByName);
				if (isSortByName) {
					isSortByName = false;
					tvSortByName.setBackgroundResource(R.drawable.ic_selected);

					isSortByLocation = false;
					tvSortByLocation
							.setBackgroundResource(R.drawable.ic_selected_off);
				} else {
					isSortByName = true;
					tvSortByName
							.setBackgroundResource(R.drawable.ic_selected_off);
					isSortByLocation = true;
					tvSortByLocation
							.setBackgroundResource(R.drawable.ic_selected);

				}

			}
		});

		findViewById(R.id.item_location).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// System.out.println("isSortByLocation-?" +
						// isSortByLocation);
						if (isSortByLocation) {
							isSortByName = false;
							tvSortByName
									.setBackgroundResource(R.drawable.ic_selected);
							isSortByLocation = false;
							tvSortByLocation
									.setBackgroundResource(R.drawable.ic_selected_off);

						} else {
							isSortByName = true;
							tvSortByName
									.setBackgroundResource(R.drawable.ic_selected_off);
							isSortByLocation = true;
							tvSortByLocation
									.setBackgroundResource(R.drawable.ic_selected);
						}

					}
				});
		findViewById(R.id.btn_confirm).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent data = new Intent();
						data.putExtra(EXTRA_SORT_BY_NAME, isSortByName);
						data.putExtra(EXTRA_SORT_BY_LOCATION, isSortByLocation);
						setResult(ActivityConstants.RESULT_RESULT_OK, data);
						finish();
					}
				});
	}
}
