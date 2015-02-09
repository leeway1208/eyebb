package com.twinly.eyebb.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.LancherActivity;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.fragment.RadarFragment;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.model.SerializableDeviceMap;
import com.twinly.eyebb.utils.SharePrefsUtils;

@SuppressLint("NewApi")
public class AntiLostService extends Service {
	public final static String ACTION_DATA_CHANGED = "antilost.ACTION_DATA_CHANGED";
	public final static String ACTION_STOP_SERVICE = "antilost.ACTION_STOP_SERVICE";

	private final static String TAG = AntiLostService.class.getSimpleName();

	public static final String EXTRA_DEVICE_LIST = "DEVICE_LIST";
	public static final int MAX_DUAL_MODE_SIZE = 3;
	private final int MESSAGE_INIT_NOTIFICAION = 1;
	private final int MESSAGE_CONNECT_DEVICE = 2;
	private final int MESSAGE_DISCONNECT_DEVICE = 3;
	private final int MESSAGE_SCANN = 4;
	private final int MESSAGE_UPDATE_VIEW = 5;

	private int mServiceState;
	private final int STATE_STARTED = 2;
	private final int STATE_STOPPING = 3;
	private final int STATE_STOPPED = 4;

	private ServiceHandler mServiceHandler;
	// pending device list need to be connected
	private ArrayList<String> antiLostDeviceList;
	private HashMap<String, Device> antiLostDeviceHashMap;
	private SerializableDeviceMap serializableDeviceMap;
	private HashMap<String, Child> childMap;

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;
	private List<BluetoothGatt> mBluetoothGattList;
	private BleDevicesScanner scanner;
	private Timer timer;

	// current bluetooth connection state
	private int mConnectionState = BLEUtils.STATE_DISCONNECTED;
	private boolean isPasswordSet;
	private boolean isSingleMode;
	private int missedCount;
	private int scannedCount;

	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder serviceNotificationbuilder;
	private NotificationCompat.Builder alertNotificationbuilder;
	private Notification serviceNotification;
	private Notification alertNotificaion;
	private boolean isSoundOn;
	private boolean isVirbrateOn;

	// Bluetoot LE scan callback, update scanned device
	LeScanCallback leScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (antiLostDeviceHashMap.get(device.getAddress()) != null) {
				antiLostDeviceHashMap.get(device.getAddress()).setPreRssi(
						antiLostDeviceHashMap.get(device.getAddress())
								.getRssi());
				antiLostDeviceHashMap.get(device.getAddress()).setRssi(rssi);
				antiLostDeviceHashMap.get(device.getAddress())
						.setLastAppearTime(System.currentTimeMillis());
			}
		}
	};

	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (mServiceState == STATE_STARTED) {
				if (newState == BluetoothProfile.STATE_CONNECTED
						&& status == BluetoothGatt.GATT_SUCCESS) {
					mConnectionState = BLEUtils.STATE_CONNECTED;
					Log.i(TAG,
							"Connected to GATT server. Attempting to start service discovery.");
					// it's new connetion, reset the password state
					isPasswordSet = false;
					// Attempts to discover services after successful connection.
					gatt.discoverServices();
				} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
					mConnectionState = BLEUtils.STATE_DISCONNECTED;
					Log.i(TAG, "Disconnected from GATT server ==> "
							+ gatt.getDevice().getAddress());
					// if current gatt is the disconnet gatt, cancel the timeout timer.
					if (gatt == mBluetoothGatt) {
						timer.cancel();
					}
					// release the gatt
					disconnectGatt(gatt);
					mBluetoothGattList.remove(gatt);
					// add back the disconnect device to the antiLostDeviceList
					if (antiLostDeviceList.contains(gatt.getDevice()
							.getAddress()) == false) {
						antiLostDeviceList.add(gatt.getDevice().getAddress());
					}
					// Only when the antiLostDeviceList has one item and send message to connect next,
					// if not, other callback wiil send the message to connect next device.
					if (antiLostDeviceList.size() == 1) {
						mServiceHandler
								.sendEmptyMessage(MESSAGE_CONNECT_DEVICE);
					}
					// set the device state to missed
					antiLostDeviceHashMap.get(gatt.getDevice().getAddress())
							.setMissed(true);
				}
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			// once discovered the service, write password firstly
			if (status == BluetoothGatt.GATT_SUCCESS) {
				write(gatt, BLEUtils.SERVICE_UUID_0002,
						BLEUtils.CHARACTERISTICS_PASSWORD, BLEUtils.PASSWORD);
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// if the state is started, the operation is to write password and anti-lost value
			if (mServiceState == STATE_STARTED) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					// if write password, write the timeout value
					if (isPasswordSet == false) {
						isPasswordSet = true;
						write(gatt, BLEUtils.SERVICE_UUID_0001,
								BLEUtils.CHARACTERISTICS_ANTI_LOST_PERIOD_UUID,
								"FFFF");
					} else {
						// when write success, cancel the timer
						if (gatt == mBluetoothGatt) {
							timer.cancel();
						}
						mConnectionState = BLEUtils.STATE_DISCOVERED;
						antiLostDeviceHashMap
								.get(gatt.getDevice().getAddress()).setMissed(
										false);
						antiLostDeviceHashMap
								.get(gatt.getDevice().getAddress()).setMissing(
										false);
						// remove the device from the pending list after setup the connection
						antiLostDeviceList
								.remove(gatt.getDevice().getAddress());
						mServiceHandler
								.sendEmptyMessage(MESSAGE_CONNECT_DEVICE);
						updateServiceNotification();
						broadcastUpdate();
					}
				} else {
					System.out
							.println("onCharacteristicWrite failed ==>> connect next");
					// if setup the connection failed, connect the next device
					connectNext(gatt);
				}
			}
			// if the state is stopping, the operation is to reset the anti-lost value
			else if (mServiceState == STATE_STOPPING) {
				/*System.out.println(status + " >>>>>>>>>> "
						+ gatt.getDevice().getAddress());*/
				Message msg = Message.obtain();
				msg.what = MESSAGE_DISCONNECT_DEVICE;
				msg.arg1 = mBluetoothGattList.indexOf(gatt) + 1;
				mServiceHandler.sendMessage(msg);
			}

		}

	};

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if (mServiceState != STATE_STOPPED) {
				switch (msg.what) {
				case MESSAGE_INIT_NOTIFICAION:
					if (initialize()) {
						buildNotification("开启中...");
						if (antiLostDeviceList.size() > MAX_DUAL_MODE_SIZE) {
							isSingleMode = true;
							// single mode: scan device only
							mServiceHandler.sendEmptyMessage(MESSAGE_SCANN);
						} else {
							isSingleMode = false;
							// dual mode: keep connection with device 
							mServiceHandler
									.sendEmptyMessage(MESSAGE_CONNECT_DEVICE);
						}
						mServiceHandler.sendEmptyMessage(MESSAGE_UPDATE_VIEW);
					}
					break;
				case MESSAGE_SCANN:
					startLeScan(leScanCallback, 500);
					break;
				case MESSAGE_CONNECT_DEVICE:
					if (mConnectionState != BLEUtils.STATE_CONNECTING) {
						if (antiLostDeviceList.size() > 0) {
							connect(antiLostDeviceList.get(0));
						}
					}
					break;
				case MESSAGE_DISCONNECT_DEVICE:
					if (mBluetoothGattList.size() > msg.arg1) {
						write(mBluetoothGattList.get(msg.arg1),
								BLEUtils.SERVICE_UUID_0001,
								BLEUtils.CHARACTERISTICS_ANTI_LOST_PERIOD_UUID,
								"0000");
					} else {
						// release all bluetooth resource
						for (BluetoothGatt gatt : mBluetoothGattList) {
							gatt.close();
						}
						mBluetoothGattList.clear();
						mServiceState = STATE_STOPPED;
						// stop the service after disconnect with the device
						stopForeground(true);
						stopSelf();
						System.out.println("onUnbind");
					}
					break;
				case MESSAGE_UPDATE_VIEW:
					updateServiceNotification();
					broadcastUpdate();
					mServiceHandler.sendEmptyMessageDelayed(
							MESSAGE_UPDATE_VIEW, 5000);
					break;
				}
			}
		}
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_STOP_SERVICE.equals(action)) {
				stop();
			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
					stop();
				}
			}
		}
	};

	@Override
	public void onCreate() {
		System.out.println("AntiLostService ==>> onCreate");
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_STOP_SERVICE);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(mReceiver, intentFilter);
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				android.os.Process.THREAD_PRIORITY_FOREGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceHandler = new ServiceHandler(thread.getLooper());

		serializableDeviceMap = new SerializableDeviceMap();
		childMap = DBChildren.getChildrenMap(this);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("onStartCommand ==>> flags:" + flags
				+ " ==>> startId:" + startId);
		antiLostDeviceList = intent.getStringArrayListExtra(EXTRA_DEVICE_LIST);
		antiLostDeviceHashMap = new HashMap<String, Device>();
		for (String device : antiLostDeviceList) {
			antiLostDeviceHashMap.put(device, new Device(device));
		}
		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.what = MESSAGE_INIT_NOTIFICAION;
		mServiceHandler.sendMessage(msg);
		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void stop() {
		mServiceHandler.removeMessages(MESSAGE_UPDATE_VIEW);
		mServiceHandler.removeMessages(MESSAGE_CONNECT_DEVICE);

		mNotificationManager.cancelAll();
		unregisterReceiver(mReceiver);
		if (isSingleMode) {
			stopLeScan();
			stopSelf();
			stopForeground(true);
		} else {
			// if it's dual mode, disconnect with device before stop the service
			mServiceState = STATE_STOPPING;
			Message msg = Message.obtain();
			msg.what = MESSAGE_DISCONNECT_DEVICE;
			msg.arg1 = 0;
			mServiceHandler.sendMessage(msg);
		}
	}

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	private boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		mBluetoothGattList = new ArrayList<BluetoothGatt>();

		return true;
	}

	private void startLeScan(BluetoothAdapter.LeScanCallback leScanCallback,
			long scanPeriod) {
		if (scanner == null) {
			scanner = new BleDevicesScanner(mBluetoothAdapter, leScanCallback);
			scanner.setScanPeriod(scanPeriod);
		}
		scanner.start();
	}

	private void stopLeScan() {
		if (scanner != null) {
			scanner.stop();
		}
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. 
	 * 		   The connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	private boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found. Unable to connect.");
			return false;
		}

		mConnectionState = BLEUtils.STATE_CONNECTING;
		// We want to directly connect to the device, so we are setting the
		// autoConnect  parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		mBluetoothGattList.add(mBluetoothGatt);

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (mConnectionState != BLEUtils.STATE_DISCOVERED) {
					System.out.println("timeout ==>> " + address
							+ " ==>> connect next");
					connectNext(mBluetoothGatt);
				}
			}
		}, 6000);

		System.out.println(" ");
		Log.d(TAG, "Trying to create a new connection ==>> " + address);
		return true;
	}

	/**
	 * When connect the current device failed, connect the next device from pending device list,
	 * Move the current device to the bottom of the pending list
	 * @param gatt Current BluetoothGatt
	 */
	private void connectNext(BluetoothGatt gatt) {
		disconnectGatt(gatt);
		mBluetoothGattList.remove(gatt);
		antiLostDeviceList.remove(gatt.getDevice().getAddress());
		antiLostDeviceList.add(gatt.getDevice().getAddress());
		System.out.println("antiLostDeviceList size = "
				+ antiLostDeviceList.size());
		if (mServiceState == STATE_STARTED) {
			mServiceHandler.sendEmptyMessage(MESSAGE_CONNECT_DEVICE);
		}
	}

	/**
	 * Write the value to given UUID
	 * @param serviceUuid
	 * @param gattUuid
	 * @param value
	 */
	private void write(BluetoothGatt gatt, String serviceUuid, String gattUuid,
			String value) {
		System.out.println("write == >> " + gattUuid + "  " + value);
		for (BluetoothGattService gattService : gatt.getServices()) {
			String uuid = gattService.getUuid().toString();
			//System.out.println("Service == >> " + uuid);
			if (uuid.equals(serviceUuid)) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService
						.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					uuid = gattCharacteristic.getUuid().toString();
					if (uuid.equals(gattUuid)) {
						//System.out.println("Characteristic == >> " + uuid);
						gattCharacteristic.setValue(BLEUtils
								.HexString2Bytes(value));
						gatt.writeCharacteristic(gattCharacteristic);
					}
				}
				break;
			}
		}
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	private void disconnectGatt(BluetoothGatt gatt) {
		mConnectionState = BLEUtils.STATE_DISCONNECTED;
		if (gatt == null) {
			return;
		}
		gatt.disconnect();
		gatt.close();
		gatt = null;
	}

	/**
	 * Update the foreground notification. Show the current state of each device 
	 */
	private void updateServiceNotification() {
		missedCount = 0;
		scannedCount = 0;

		String macAddress;

		Iterator<String> it = antiLostDeviceHashMap.keySet().iterator();
		while (it.hasNext()) {
			macAddress = it.next();
			if (isSingleMode) {
				if (System.currentTimeMillis()
						- antiLostDeviceHashMap.get(macAddress)
								.getLastAppearTime() < RadarFragment.LOST_TIMEOUT) {
					antiLostDeviceHashMap.get(macAddress).setMissed(false);
					antiLostDeviceHashMap.get(macAddress).setMissing(false);
					// cancel the previous alter notification when re-find the device
					mNotificationManager.cancel((int) childMap.get(macAddress)
							.getChildId());
					scannedCount++;
				} else {
					antiLostDeviceHashMap.get(macAddress).setMissed(true);
					if (antiLostDeviceHashMap.get(macAddress).isMissing() == false) {
						updateAlertNotification(macAddress);
						antiLostDeviceHashMap.get(macAddress).setMissing(true);
					}
					missedCount++;
				}
			} else {
				if (antiLostDeviceHashMap.get(macAddress).isMissed()) {
					if (antiLostDeviceHashMap.get(macAddress).isMissing() == false) {
						updateAlertNotification(macAddress);
						antiLostDeviceHashMap.get(macAddress).setMissing(true);
					}
					missedCount++;
				} else {
					// cancel the previous alter notification when re-find the device
					mNotificationManager.cancel((int) childMap.get(macAddress)
							.getChildId());
					scannedCount++;
				}
			}
		}

		String content = getString(R.string.btn_supervised) + ": "
				+ scannedCount + "      " + getString(R.string.btn_missed)
				+ ": " + missedCount;

		serviceNotificationbuilder.setContentText(content);
		serviceNotification = serviceNotificationbuilder.build();
		startForeground(1, serviceNotification);
	}

	/**
	 * Send the alter notificaion
	 * @param macAddress The device mac address of the kid
	 */
	private void updateAlertNotification(String macAddress) {
		alertNotificationbuilder.setContentText(childMap.get(macAddress)
				.getName() + " " + getString(R.string.btn_missed));
		alertNotificaion = alertNotificationbuilder.build();
		alertNotificaion.defaults |= Notification.DEFAULT_LIGHTS;
		alertNotificaion.flags = Notification.FLAG_AUTO_CANCEL;
		if (isSoundOn) {
			alertNotificaion.defaults |= Notification.DEFAULT_SOUND;
		}
		if (isVirbrateOn) {
			alertNotificaion.defaults |= Notification.DEFAULT_VIBRATE;
		}
		// Issue the notification
		mNotificationManager.notify(
				(int) childMap.get(macAddress).getChildId(), alertNotificaion);
	}

	/**
	 * Broadcast the newest data to AntiLost Fragment
	 */
	private void broadcastUpdate() {
		Intent data = new Intent(ACTION_DATA_CHANGED);
		Bundle bundle = new Bundle();
		serializableDeviceMap.setMap(antiLostDeviceHashMap);
		bundle.putSerializable(EXTRA_DEVICE_LIST, serializableDeviceMap);
		data.putExtras(bundle);
		sendBroadcast(data);
	}

	/**
	 * Build the foreground notification and alert notification
	 * @param content
	 */
	private void buildNotification(String content) {
		mServiceState = STATE_STARTED;

		/*
		 *  build service foreground notification
		 */
		serviceNotificationbuilder = new NotificationCompat.Builder(
				AntiLostService.this);
		serviceNotificationbuilder.setSmallIcon(R.drawable.ic_location_default);
		serviceNotificationbuilder
				.setContentTitle(getString(R.string.text_anti_lost_mode));
		serviceNotificationbuilder.setContentText(content);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, LancherActivity.class);
		resultIntent.setAction("android.intent.action.MAIN");
		resultIntent.addCategory("android.intent.category.LAUNCHER");

		PendingIntent resultPendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		serviceNotificationbuilder.setContentIntent(resultPendingIntent);
		serviceNotification = serviceNotificationbuilder.build();
		startForeground(1, serviceNotification);

		/*
		 *  build normal notification for notifying missed kids
		 */
		alertNotificationbuilder = new NotificationCompat.Builder(this);

		alertNotificationbuilder.setSmallIcon(R.drawable.ic_location_default);
		alertNotificationbuilder
				.setContentTitle(getString(R.string.text_anti_lost_mode));

		alertNotificationbuilder.setContentIntent(resultPendingIntent);

		isVirbrateOn = SharePrefsUtils.isVibrateOn(this);
		isSoundOn = SharePrefsUtils.isSoundOn(this);
	}
}
