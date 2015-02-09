package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter;
import com.twinly.eyebb.bluetooth.AntiLostService;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.model.SerializableDeviceMap;

public class AntiLostFragment extends Fragment {
	private HashMap<String, Device> deviceHashMap;
	private ArrayList<Device> deviceList;
	private SerializableDeviceMap serializableMacaronMap;
	private ProgressBar progressBar;
	private ListView listView;
	private RadarKidsListViewAdapter mAdapter;
	private boolean isAntiLostOn = false;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (AntiLostService.ACTION_DATA_CHANGED.equals(action)) {
				Bundle bundle = intent.getExtras();
				serializableMacaronMap = (SerializableDeviceMap) bundle
						.get(AntiLostService.EXTRA_DEVICE_LIST);
				updateView();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_anti_lost, container, false);
		progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		listView = (ListView) v.findViewById(R.id.listView);
		deviceList = new ArrayList<Device>();
		mAdapter = new RadarKidsListViewAdapter(getActivity(), deviceList);
		listView.setAdapter(mAdapter);
		getActivity().registerReceiver(mReceiver,
				new IntentFilter(AntiLostService.ACTION_DATA_CHANGED));
		return v;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	public void start(ArrayList<String> antiLostDeviceList) {
		Intent antiLostServiceIntent = new Intent();
		antiLostServiceIntent.setClass(getActivity(), AntiLostService.class);
		antiLostServiceIntent.putStringArrayListExtra(
				AntiLostService.EXTRA_DEVICE_LIST, antiLostDeviceList);
		getActivity().startService(antiLostServiceIntent);
		isAntiLostOn = true;
	}

	public void resume() {
		isAntiLostOn = true;
	}

	public void stop() {
		isAntiLostOn = false;
		Intent action = new Intent(AntiLostService.ACTION_STOP_SERVICE);
		getActivity().sendBroadcast(action);
	}

	private void updateView() {
		if (isAntiLostOn) {
			deviceHashMap = serializableMacaronMap.getMap();
			String macAddress;
			Iterator<String> it = deviceHashMap.keySet().iterator();
			deviceList.clear();
			while (it.hasNext()) {
				macAddress = it.next();
				if (deviceHashMap.size() > AntiLostService.MAX_DUAL_MODE_SIZE) {
					if (System.currentTimeMillis()
							- deviceHashMap.get(macAddress).getLastAppearTime() < RadarFragment.LOST_TIMEOUT) {
						deviceHashMap.get(macAddress).setMissed(false);
					} else {
						deviceHashMap.get(macAddress).setMissed(true);
					}
				}
				deviceList.add(deviceHashMap.get(macAddress));
			}
			if (deviceList.size() < AntiLostService.MAX_DUAL_MODE_SIZE) {
				mAdapter.setRssiDisplayed(false);
			}
			progressBar.setVisibility(View.INVISIBLE);
			mAdapter.notifyDataSetChanged();
		}
	}

}
