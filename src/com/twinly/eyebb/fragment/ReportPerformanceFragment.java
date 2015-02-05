package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.PerformanceListViewAdapter;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.PullToRefreshListView;
import com.twinly.eyebb.customview.PullToRefreshListView.PullToRefreshListener;
import com.twinly.eyebb.database.DBPerformance;
import com.twinly.eyebb.model.Performance;
import com.twinly.eyebb.model.PerformanceListItem;

public class ReportPerformanceFragment extends Fragment implements
		PullToRefreshListener {
	private final int[] days = new int[] { 5, 14, 50 };
	private int selectDay = days[0];
	private PullToRefreshListView listView;
	private Spinner mSpinner;
	private TextView tvDay;
	private PerformanceListViewAdapter adapter;
	private List<PerformanceListItem> list;
	private CallbackInterface callback;
	private long childId;

	public interface CallbackInterface {
		/**
		 * Update the progressBar value when pull the listView
		 * @param value current progress
		 */
		public void updateProgressBar(int value);

		/**
		 * Cancel update the progressBar when release the listView  
		 */
		public void cancelProgressBar();

		/**
		 * Update view when select a new period 
		 */
		public void onSelectDaysChange();
	}

	public void setCallbackInterface(CallbackInterface callback) {
		this.callback = callback;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_report_performance,
				container, false);
		tvDay = (TextView) v.findViewById(R.id.days);
		mSpinner = (Spinner) v.findViewById(R.id.customized_day);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.tt, R.layout.item_spinner);
		adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
		tvDay.setText(getResources().getStringArray(R.array.tt)[0]);
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				tvDay.setText(getResources().getStringArray(R.array.tt)[position]);
				if (selectDay != days[position]) {
					selectDay = days[position];
					callback.onSelectDaysChange();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		listView = (PullToRefreshListView) v.findViewById(R.id.listView);
		listView.setPullToRefreshListener(this);

		list = new ArrayList<PerformanceListItem>();
		childId = getArguments().getLong("childId");
		if (childId != -1) {
			updateView(DBPerformance.getPerformanceByChildId(getActivity(),
					childId));
		}

		return v;
	}

	public void updateView(Performance performance) {
		if (performance != null) {
			list.clear();

			JSONArray jsonArray;
			try {
				jsonArray = new JSONArray(performance.getJsonData());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					// location name
					PerformanceListItem item = new PerformanceListItem(
							object.getString(HttpConstants.JSON_KEY_REPORT_PERFORMANCE_LOC_NAME),
							-1, 0, 0, 0);
					list.add(item);
					// today
					item = new PerformanceListItem(
							"",
							R.drawable.my_progress_blue,
							(int) Double.parseDouble(object
									.getString(HttpConstants.JSON_KEY_REPORT_PERFORMANCE_DAILY)),
							(int) Double.parseDouble(object
									.getString(HttpConstants.JSON_KEY_REPORT_PERFORMANCE_DAILY)),
							1440);
					list.add(item);
					// average
					item = new PerformanceListItem(
							"",
							R.drawable.my_progress_yellow,
							(int) Double.parseDouble(object
									.getString(HttpConstants.JSON_KEY_REPORT_PERFORMANCE_AVERAGE)),
							(int) Double.parseDouble(object
									.getString(HttpConstants.JSON_KEY_REPORT_PERFORMANCE_AVERAGE)),
							1440);
					list.add(item);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			updateAdapter();
		}

	}

	public void updateAdapter() {
		adapter = new PerformanceListViewAdapter(getActivity(), list);
		listView.setAdapter(adapter);
	}

	@Override
	public void updateProgressBar(int value) {
		callback.updateProgressBar(value);
	}

	@Override
	public void cancelProgressBar() {
		callback.cancelProgressBar();

	}

	public int getSelectDay() {
		return selectDay;
	}

	/**
	 * Set the listView state. The list cannot scroll when is refreshing, 
	 * @param isRefreshing whether requesting server to update data
	 */
	/*public void setRefreshing(boolean isRefreshing) {
		if(listView != null) {
			listView.setRefreshing(isRefreshing);
		}
	}*/
}
