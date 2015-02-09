package com.twinly.eyebb.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.SelectKidsActivity;
import com.twinly.eyebb.bluetooth.BluetoothUtils;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.model.ChildSelectable;
import com.twinly.eyebb.model.SerializableChildrenList;
import com.twinly.eyebb.utils.SharePrefsUtils;

@SuppressLint("NewApi")
public class RadarFragment extends Fragment {
	public final static int LOST_TIMEOUT = 10000;

	private RadarTrackingFragment radarTrackingFragment;
	private AntiLostFragment antiLostFragment;
	private TextView btnRadarSwitch;
	private RelativeLayout container;
	private CheckedTextView tvRadarTracking;
	private CheckedTextView tvAntiLost;

	private BluetoothUtils mBluetoothUtils;
	public static int radarState;
	private boolean isRadarTrackingOn = false;
	private boolean isAntiLostOn = false;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
				case BluetoothAdapter.STATE_OFF:
					stop();
					break;
				case BluetoothAdapter.STATE_ON:
					if (radarState == BluetoothAdapter.STATE_TURNING_ON) {
						start();
					}
					break;
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getActivity().registerReceiver(mReceiver,
				new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView");
		mBluetoothUtils = new BluetoothUtils(getActivity(),
				getFragmentManager());

		View v = inflater.inflate(R.layout.fragment_radar, container, false);
		setUpView(v);
		setupListener();
		return v;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	private void setUpView(View v) {
		btnRadarSwitch = (TextView) v.findViewById(R.id.btn_radar_switch);
		container = (RelativeLayout) v.findViewById(R.id.container);
		tvRadarTracking = (CheckedTextView) v.findViewById(R.id.radar_tracking);
		tvAntiLost = (CheckedTextView) v.findViewById(R.id.anti_lost);

		FragmentTransaction fragmentTransaction = getChildFragmentManager()
				.beginTransaction();
		radarTrackingFragment = (RadarTrackingFragment) getChildFragmentManager()
				.findFragmentByTag("radar");
		if (radarTrackingFragment == null) {
			radarTrackingFragment = new RadarTrackingFragment();
			fragmentTransaction.add(R.id.container, radarTrackingFragment,
					"radar");
		}

		antiLostFragment = (AntiLostFragment) getChildFragmentManager()
				.findFragmentByTag("antiLost");
		if (antiLostFragment == null) {
			antiLostFragment = new AntiLostFragment();
			fragmentTransaction.add(R.id.container, antiLostFragment,
					"antiLost");
		}

		if (SharePrefsUtils.isAntiLostOn(getActivity())) {
			start();
		} else {
			fragmentTransaction.hide(antiLostFragment);
		}
		fragmentTransaction.commit();
	}

	private void setupListener() {
		btnRadarSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (radarState == BluetoothAdapter.STATE_ON) {
					stop();
				} else {
					if (mBluetoothUtils.initialize()) {
						start();
					} else {
						radarState = BluetoothAdapter.STATE_TURNING_ON;
					}
				}
			}
		});
		tvRadarTracking.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startRadarTracking();
			}
		});
		tvAntiLost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isAntiLostOn == false) {
					Intent intent = new Intent(getActivity(),
							SelectKidsActivity.class);
					startActivityForResult(intent, 1);
				}
			}
		});
	}

	private void start() {
		radarState = BluetoothAdapter.STATE_ON;
		container.setAlpha(1F);
		container.setEnabled(true);
		tvRadarTracking.setEnabled(true);
		tvAntiLost.setEnabled(true);
		btnRadarSwitch.setBackgroundResource(R.drawable.btn_switch_on);
		if (SharePrefsUtils.isAntiLostOn(getActivity())) {
			resumeAntiLost();
		} else {
			startRadarTracking();
		}
	}

	private void stop() {
		if (radarState == BluetoothAdapter.STATE_ON
				|| radarState == BluetoothAdapter.STATE_TURNING_ON) {
			radarState = BluetoothAdapter.STATE_OFF;
			container.setAlpha(0.3F);
			container.setEnabled(false);
			tvRadarTracking.setEnabled(false);
			tvAntiLost.setEnabled(false);
			btnRadarSwitch.setBackgroundResource(R.drawable.btn_switch_off);
			stopRadarTracking();
			stopAntiLost();
		}
	}

	private void startRadarTracking() {
		if (isRadarTrackingOn == false) {
			stopAntiLost();
			isRadarTrackingOn = true;
			tvRadarTracking.setChecked(true);
			radarTrackingFragment.start();
			getChildFragmentManager().beginTransaction()
					.show(radarTrackingFragment).hide(antiLostFragment)
					.commit();
		}
	}

	private void stopRadarTracking() {
		if (isRadarTrackingOn == true) {
			isRadarTrackingOn = false;
			tvRadarTracking.setChecked(false);
			radarTrackingFragment.stop();
		}
	}

	private void startAntiLost(ArrayList<String> antiLostDeviceList) {
		if (isAntiLostOn == false) {
			SharePrefsUtils.setAntiLostOn(getActivity(), true);
			stopRadarTracking();
			isAntiLostOn = true;
			tvAntiLost.setChecked(true);
			antiLostFragment.start(antiLostDeviceList);
			getChildFragmentManager().beginTransaction().show(antiLostFragment)
					.hide(radarTrackingFragment).commit();
		}
	}

	private void resumeAntiLost() {
		isAntiLostOn = true;
		tvAntiLost.setChecked(true);
		antiLostFragment.resume();
		getChildFragmentManager().beginTransaction().show(antiLostFragment)
				.hide(radarTrackingFragment).commit();
	}

	private void stopAntiLost() {
		if (isAntiLostOn == true) {
			SharePrefsUtils.setAntiLostOn(getActivity(), false);
			isAntiLostOn = false;
			tvAntiLost.setChecked(false);
			antiLostFragment.stop();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
			mBluetoothUtils.stopLeScan();

			SerializableChildrenList serializableChildrenList = (SerializableChildrenList) data
					.getExtras().getSerializable(
							SelectKidsActivity.EXTRA_CHILDREN_LIST);
			ArrayList<ChildSelectable> childrenList = serializableChildrenList
					.getList();
			ArrayList<String> antiLostDeviceList = new ArrayList<String>();
			for (int i = 0; i < childrenList.size(); i++) {
				if (childrenList.get(i).isSelected()) {
					antiLostDeviceList.add(childrenList.get(i).getMacAddress());
				}
			}
			startAntiLost(antiLostDeviceList);
		}
	}
}
