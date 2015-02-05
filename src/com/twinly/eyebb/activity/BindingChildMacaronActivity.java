package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.bluetooth.BluetoothUtils;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.ImageUtils;

/**
 * @author eyebb team
 * 
 * @category BindingChildMacaronActivity
 * 
 *           this activity is used when get the qrcode and bind the device
 *           (qrcode) to the child
 */
public class BindingChildMacaronActivity extends Activity implements
		BluetoothUtils.BleConnectCallback {
	private final static int BIND_STEP_CONNECTING = 1;
	private final static int BIND_STEP_CONNECT_FAIL = 2;
	private final static int BIND_STEP_DISCOVERED = 3;
	private final static int BIND_STEP_MAJOR_WRITEN = 4;
	private final static int BIND_STEP_MINOR_WRITEN = 5;
	private final static int BIND_STEP_UPLOADING = 6;
	private final static int BIND_STEP_UPLOAD_FAIL = 7;
	private final static int BIND_STEP_BIND_FINISH = 8;
	private final int[] images = new int[] { R.drawable.ani_connecting_01,
			R.drawable.ani_connecting_02, R.drawable.ani_connecting_03,
			R.drawable.ani_connecting_04, R.drawable.ani_connecting_05,
			R.drawable.ani_connecting_06, R.drawable.ani_connecting_07,
			R.drawable.ani_connecting_08, R.drawable.ani_connecting_09,
			R.drawable.ani_connecting_10, R.drawable.ani_connecting_11 };

	private CircleImageView avatar;
	private TextView tvAnimation;
	private TextView tvMessage;
	private TextView tvAddress;
	private Button btnEvent;
	private Handler mHandler;
	private int index;
	private ImageLoader imageLoader;

	private int from;
	private String mDeviceAddress;
	private long childId;
	private String childIcon;
	private long guardianId;
	private String major;
	private String minor;
	private int bindStep;

	private BluetoothUtils mBluetoothUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binding_child_macaron);

		from = getIntent().getIntExtra(ActivityConstants.EXTRA_FROM, -1);
		mDeviceAddress = getIntent().getStringExtra(
				ActivityConstants.EXTRA_MAC_ADDRESS);
		guardianId = getIntent().getLongExtra(
				ActivityConstants.EXTRA_GUARDIAN_ID, -1L);
		childId = getIntent().getLongExtra(ActivityConstants.EXTRA_CHILD_ID, 0);
		childIcon = getIntent().getStringExtra(
				ActivityConstants.EXTRA_CHILD_ICON);

		avatar = (CircleImageView) findViewById(R.id.avatar);
		tvAnimation = (TextView) findViewById(R.id.tv_animation);
		tvMessage = (TextView) findViewById(R.id.message);
		btnEvent = (Button) findViewById(R.id.btn_event);
		tvAddress = (TextView) findViewById(R.id.tv_address);

		tvAddress.setText(mDeviceAddress);

		if (TextUtils.isEmpty(childIcon) == false) {
			if (ImageUtils.isLocalImage(childIcon)) {
				avatar.setImageBitmap(ImageUtils.getBitmapFromLocal(childIcon));
			} else {
				imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(childIcon, avatar,
						ImageUtils.avatarOpitons, null);
			}
		}

		mHandler = new Handler();
		mHandler.postDelayed(new UpdateAnimation(), 500);

		mBluetoothUtils = new BluetoothUtils(BindingChildMacaronActivity.this,
				getFragmentManager(), BindingChildMacaronActivity.this);
		new GetMajorMinorTask().execute();

		btnEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (bindStep) {
				case BIND_STEP_CONNECTING:
					finish();
					break;
				case BIND_STEP_CONNECT_FAIL:
					mBluetoothUtils.writeMajor(mDeviceAddress, 15000L, major);
					break;
				case BIND_STEP_UPLOAD_FAIL:
					new PostToServerTask().execute();
					break;
				case BIND_STEP_BIND_FINISH:
					switch (from) {
					case ActivityConstants.ACTIVITY_CHECK_CHILD_TO_BIND:
						Intent intent = new Intent(
								BindingChildMacaronActivity.this,
								LancherActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						break;
					case ActivityConstants.ACTIVITY_KID_PROFILE:
						setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS);
						break;
					}
					finish();
					break;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mBluetoothUtils.registerReceiver();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mBluetoothUtils.unregisterReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBluetoothUtils.disconnect();
	}

	private class UpdateAnimation implements Runnable {

		@Override
		public void run() {
			if (bindStep == BIND_STEP_CONNECTING
					|| bindStep == BIND_STEP_UPLOADING
					|| bindStep == BIND_STEP_DISCOVERED) {
				if (index == 10) {
					index = 0;
				} else {
					index++;
				}
				tvAnimation.setBackgroundResource(images[index]);
				mHandler.postDelayed(new UpdateAnimation(), 500);
			}
		}
	}

	/**
	 * To get major & minor from server by child_id and mac address
	 * 
	 * @author derek
	 * 
	 */
	private class GetMajorMinorTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("childId", String.valueOf(childId));
			map.put("macAddress", mDeviceAddress);

			return HttpRequestUtils.post(HttpConstants.CHECK_BEACON, map);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println(HttpConstants.CHECK_BEACON + " = " + result);

			if (result.length() > 0) {
				if (result.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
					return;
				} else if (result.equals(HttpConstants.SERVER_RETURN_NC)) {
					return;
				}
				if (result.equals(HttpConstants.SERVER_RETURN_USED)) {
					Toast.makeText(BindingChildMacaronActivity.this,
							R.string.text_device_already_binded,
							Toast.LENGTH_LONG).show();
					finish();
					return;
				} else {
					major = result.substring(0, result.indexOf(":"));
					minor = result.substring(result.indexOf(":") + 1,
							result.length());
					System.out.println("major = " + major + "  minor = "
							+ minor);
					mBluetoothUtils.writeMajor(mDeviceAddress, 15000L, major);
				}
			}
		}
	}

	/**
	 * To upload the data to server when bind target device succeed
	 * 
	 * @author derek
	 * 
	 */
	private class PostToServerTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			bindStep = BIND_STEP_UPLOADING;
			tvMessage.setText(R.string.text_update_server_data);
			btnEvent.setEnabled(false);
		}

		@Override
		protected String doInBackground(Void... params) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("childId", String.valueOf(childId));
			map.put("macAddress", mDeviceAddress);
			map.put("major", major);
			map.put("minor", minor);
			map.put("guardianId",
					guardianId == -1 ? "" : String.valueOf(guardianId));
			return HttpRequestUtils.post(HttpConstants.DEVICE_TO_CHILD, map);
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println(HttpConstants.DEVICE_TO_CHILD + " = " + result);
			if (result.length() > 0) {
				btnEvent.setText(R.string.btn_finish);
				btnEvent.setEnabled(true);
				if (result.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)) {
					uploadFailed();
					return;
				}
				if (result.equals("T")) {
					bindStep = BIND_STEP_BIND_FINISH;
					tvAnimation
							.setBackgroundResource(R.drawable.ani_connecting_done);
					tvMessage.setText(R.string.text_bind_success);
					DBChildren.updateMacAddressByChildId(
							BindingChildMacaronActivity.this, childId,
							mDeviceAddress);
				} else {
					uploadFailed();
					setResult(ActivityConstants.RESULT_WRITE_MAJOR_MINOR_FAIL);
				}
			} else {
				uploadFailed();
			}

		}
	}

	private void writeFailed() {
		bindStep = BIND_STEP_CONNECT_FAIL;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvAnimation
						.setBackgroundResource(R.drawable.ani_connecting_fail);
				tvMessage.setText(R.string.text_connect_device_failed);
				btnEvent.setText(R.string.btn_re_connect);
			}
		});

	}

	private void uploadFailed() {
		bindStep = BIND_STEP_UPLOAD_FAIL;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvAnimation
						.setBackgroundResource(R.drawable.ani_connecting_fail);
				tvMessage.setText(R.string.text_update_server_data_fail);
			}
		});
	}

	@Override
	public void onPreConnect() {
		bindStep = BIND_STEP_CONNECTING;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvMessage.setText(R.string.text_connecting);
				btnEvent.setText(R.string.btn_cancel);
			}
		});
	}

	@Override
	public void onConnectCanceled() {
		writeFailed();
	}

	@Override
	public void onConnected() {
		// do nothing
	}

	@Override
	public void onDisConnected() {
		writeFailed();
	}

	@Override
	public void onDiscovered() {
		bindStep = BIND_STEP_DISCOVERED;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvMessage.setText(R.string.text_update_device_data);
				btnEvent.setText(R.string.btn_cancel);
			}
		});
	}

	@Override
	public void onDataAvailable(String value) {
		// do nothing

	}

	@Override
	public void onResult(boolean result) {
		if (result) {
			if (bindStep == BIND_STEP_DISCOVERED) {
				bindStep = BIND_STEP_MAJOR_WRITEN;
				mBluetoothUtils.writeMajor(mDeviceAddress, 15000L, major);
			} else if (bindStep == BIND_STEP_MAJOR_WRITEN) {
				bindStep = BIND_STEP_MINOR_WRITEN;
				mBluetoothUtils.writeMinor(mDeviceAddress, 15000L, minor);
			} else {
				new PostToServerTask().execute();
			}
		} else {
			writeFailed();
		}

	}
}
