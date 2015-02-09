package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter;
import com.twinly.eyebb.bluetooth.BluetoothUtils;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.utils.BroadcastUtils;

@SuppressLint("NewApi")
public class RadarTrackingFragment extends Fragment {
	private final int MESSAGE_WHAT_UPDATE_VIEW = 0;
	private final int MESSAGE_WHAT_REMOVE_CALLBACK = 1;

	private BluetoothUtils mBluetoothUtils;
	private ListView listView;
	private RelativeLayout btnSuperised;
	private RelativeLayout btnMissed;
	private TextView redDividerSuperised;
	private TextView blackDividerSuperised;
	private TextView tvSuperised;
	private TextView redDividerMissed;
	private TextView blackDividerMissed;
	private TextView tvMissed;

	private TextView tvSupervisedNumber;
	private TextView tvMissedNumber;
	private RadarViewFragment radarViewFragment;

	private HashMap<String, Device> macaronHashMap;
	private ArrayList<Device> displayDeviceList;
	private ArrayList<Device> scannedDeviceList;
	private ArrayList<Device> missedDeviceList;
	private RadarKidsListViewAdapter mAdapter;

	private boolean isSuperisedSection = true;
	private boolean isRadarTrackingOn = false;

	LeScanCallback leScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (macaronHashMap.get(device.getAddress()) != null) {
				macaronHashMap.get(device.getAddress()).setPreRssi(
						macaronHashMap.get(device.getAddress()).getRssi());
				macaronHashMap.get(device.getAddress()).setRssi(rssi);
				macaronHashMap.get(device.getAddress()).setLastAppearTime(
						System.currentTimeMillis());
			}
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mBluetoothUtils = new BluetoothUtils(getActivity(),
				getFragmentManager());

		View v = inflater.inflate(R.layout.fragment_radar_tracking, container,
				false);
		listView = (ListView) v.findViewById(R.id.listView);
		btnSuperised = (RelativeLayout) v.findViewById(R.id.btn_supervised);
		tvSuperised = (TextView) v.findViewById(R.id.tv_supervised);
		redDividerSuperised = (TextView) v
				.findViewById(R.id.red_divider_supervised);
		blackDividerSuperised = (TextView) v
				.findViewById(R.id.black_divider_supervised);
		tvMissed = (TextView) v.findViewById(R.id.tv_missed);
		redDividerMissed = (TextView) v.findViewById(R.id.red_divider_missed);
		blackDividerMissed = (TextView) v
				.findViewById(R.id.black_divider_missed);
		btnMissed = (RelativeLayout) v.findViewById(R.id.btn_missed);
		tvSupervisedNumber = (TextView) v
				.findViewById(R.id.tv_supervised_number);
		tvMissedNumber = (TextView) v.findViewById(R.id.tv_missed_number);

		radarViewFragment = (RadarViewFragment) getChildFragmentManager()
				.findFragmentByTag("radarView");
		if (radarViewFragment == null) {
			radarViewFragment = new RadarViewFragment();
			getChildFragmentManager().beginTransaction()
					.add(R.id.radar_view, radarViewFragment, "radarView")
					.commit();
		}

		setupListener();
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		macaronHashMap = DBChildren.getChildrenMapWithAddress(getActivity());
		displayDeviceList = new ArrayList<Device>();
		scannedDeviceList = new ArrayList<Device>();
		missedDeviceList = new ArrayList<Device>();
		mAdapter = new RadarKidsListViewAdapter(getActivity(),
				displayDeviceList);
		listView.setAdapter(mAdapter);
	}

	private void setupListener() {
		btnSuperised.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSuperisedSection = true;
				tvMissed.setTextAppearance(getActivity(),
						R.style.LightGreyText_18);
				redDividerMissed.setVisibility(View.INVISIBLE);
				blackDividerMissed.setVisibility(View.VISIBLE);

				tvSuperised
						.setTextAppearance(getActivity(), R.style.RedText_18);
				redDividerSuperised.setVisibility(View.VISIBLE);
				blackDividerSuperised.setVisibility(View.INVISIBLE);

				updateListView();
			}
		});
		btnMissed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSuperisedSection = false;
				tvSuperised.setTextAppearance(getActivity(),
						R.style.LightGreyText_18);
				redDividerSuperised.setVisibility(View.INVISIBLE);
				blackDividerSuperised.setVisibility(View.VISIBLE);

				tvMissed.setTextAppearance(getActivity(), R.style.RedText_18);
				redDividerMissed.setVisibility(View.VISIBLE);
				blackDividerMissed.setVisibility(View.INVISIBLE);

				updateListView();
			}
		});
	}

	public void start() {
		isRadarTrackingOn = true;
		mBluetoothUtils.startLeScan(leScanCallback, 500);
		radarViewFragment.startAnimation();
		BroadcastUtils.opeanRadar(getActivity());

		mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_UPDATE_VIEW, 2000);
	}

	public void stop() {
		isRadarTrackingOn = false;
		mBluetoothUtils.stopLeScan();
		radarViewFragment.stopAnimation();
		BroadcastUtils.closeRadar(getActivity());
		mHandler.removeMessages(MESSAGE_WHAT_UPDATE_VIEW);
	}

	Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_WHAT_UPDATE_VIEW:
				mHandler.post(updateViewRunnable);
				break;
			case MESSAGE_WHAT_REMOVE_CALLBACK:
				mHandler.removeCallbacks(updateViewRunnable);
				break;
			}
		}
	};

	Runnable updateViewRunnable = new Runnable() {

		@Override
		public void run() {
			if (isRadarTrackingOn) {
				updateView();
				mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_UPDATE_VIEW, 5000);
			}

		}

	};

	public void updateView() {
		if (isRadarTrackingOn) {
			String macAddress;

			scannedDeviceList.clear();
			missedDeviceList.clear();

			Iterator<String> it = macaronHashMap.keySet().iterator();
			while (it.hasNext()) {
				macAddress = it.next();
				if (System.currentTimeMillis()
						- macaronHashMap.get(macAddress).getLastAppearTime() < RadarFragment.LOST_TIMEOUT) {
					macaronHashMap.get(macAddress).setMissed(false);
					scannedDeviceList.add(macaronHashMap.get(macAddress));
				} else {
					macaronHashMap.get(macAddress).setMissed(true);
					missedDeviceList.add(macaronHashMap.get(macAddress));
				}
			}

			radarViewFragment.updateView(scannedDeviceList);
			updateListView();
		}
	}

	private void updateListView() {
		displayDeviceList.clear();
		if (isSuperisedSection) {
			displayDeviceList.addAll(scannedDeviceList);
		} else {
			displayDeviceList.addAll(missedDeviceList);
		}

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvSupervisedNumber.setText(scannedDeviceList.size() + "");
				tvMissedNumber.setText(missedDeviceList.size() + "");

				mAdapter.notifyDataSetChanged();
				setListViewHeightBasedOnChildren(listView);
			}
		});
	}

	/**
	 * 动态设置ListView的高度
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(final ListView listView) {
		if (listView == null)
			return;

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition 
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		final ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		listView.setLayoutParams(params);

	}
}
