package com.twinly.eyebb.dialog;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.ImageUtils;

public class ChildDialog extends Activity {

	private TextView phone;
	private TextView name;
	private TextView locationName;
	private LinearLayout phoneBtn;
	private CircleImageView avatar;
	private String icon;
	private long childId;
	private String macAddress;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	final static int START_PROGRASSS_BAR = 1;
	final static int STOP_PROGRASSS_BAR = 2;
	private Dialog dialog;
	private String URL = "reportService/api/configBeaconRel";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_child);

		phone = (TextView) findViewById(R.id.phone);
		phoneBtn = (LinearLayout) findViewById(R.id.phone_btn);
		name = (TextView) findViewById(R.id.name);
		locationName = (TextView) findViewById(R.id.area_name);
		avatar = (CircleImageView) findViewById(R.id.avatar);

		phone.setText(getIntent().getStringExtra("phone"));
		name.setText(getIntent().getStringExtra("name"));
		locationName.setText("@ " + getIntent().getStringExtra("location"));
		icon = getIntent().getStringExtra("icon");
		childId = getIntent().getLongExtra("id", -1L);
		macAddress = getIntent().getStringExtra("macAddress");

		if (phone.getText().toString().trim().length() == 0) {
			phoneBtn.setVisibility(View.GONE);
		}

		imageLoader = ImageLoader.getInstance();
		if (ImageUtils.isLocalImage(icon)) {
			avatar.setImageBitmap(ImageUtils.getBitmapFromLocal(icon));
		} else {
			imageLoader.displayImage(icon, avatar, ImageUtils.avatarOpitons,
					animateFirstListener);
		}

		phoneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri telUri = Uri.parse("tel:" + phone.getText());
				Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
				startActivity(intent);
			}
		});

		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.maindialog_beep_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (CommonUtils.isFastDoubleClick()) {
							return;
						} else {
							//							Intent intent = new Intent(ChildDialog.this,
							//									BeepDialog.class);
							new Thread(postToServerRunnable).start();
							//							startActivity(intent);
						}
					}
				});

	}

	private class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				CircleImageView imageView = (CircleImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	Runnable postToServerRunnable = new Runnable() {
		@Override
		public void run() {
			// HANDLER
			Message msg = handler.obtainMessage();
			msg.what = START_PROGRASSS_BAR;
			handler.sendMessage(msg);
			postToServer();

		}
	};

	private void postToServer() {
		// TODO Auto-generated method stub

		Map<String, String> map = new HashMap<String, String>();
		map.put("childId", childId + "");
		map.put("macAddress", macAddress);

		try {
			// String retStr = GetPostUtil.sendPost(url, postMessage);
			String retStr = HttpRequestUtils.post(URL, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals("retStr.equals => "
					+ HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				dialog.dismiss();

			} else {
				// successful

				dialog.dismiss();

			}

		} catch (Exception e) {

			e.printStackTrace();

			Message msg = handler.obtainMessage();
			msg.what = STOP_PROGRASSS_BAR;
			handler.sendMessage(msg);

		}

		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case START_PROGRASSS_BAR:
				dialog = LoadingDialog.createLoadingDialog(ChildDialog.this,
						getString(R.string.toast_loading));
				dialog.show();
				break;

			case STOP_PROGRASSS_BAR:
				dialog.dismiss();

				break;

			}
		}
	};
}
