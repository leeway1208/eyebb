package com.twinly.eyebb.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.BarcodeCreater;
import com.twinly.eyebb.utils.DensityUtil;
import com.twinly.eyebb.utils.HttpRequestUtils;

/**
 * @author eyebb team
 * 
 * @category RequireQrCodeDialog
 * 
 *           this activity is used in the kidProfileActivity. it will show the
 *           lastest mac-address of device. the mac-address also use
 *           "BarcodeCreater.creatBarcode" function to become QR code
 */
public class RequireQrCodeDialog extends Activity {
	private ImageView imgQrCode;
	private TextView txtQrCodeAddress;
	private TextView btnCancel;
	private Dialog qrCodeDialog;
	private String childId;
	private String deviceAddress;

	private Bitmap btMap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_require_qr_code);

		imgQrCode = (ImageView) findViewById(R.id.device_qr_code);
		txtQrCodeAddress = (TextView) findViewById(R.id.device_address);
		btnCancel = (TextView) findViewById(R.id.btn_cancel);

		Intent intent = getIntent();
		childId = intent.getStringExtra("childId");

		qrCodeDialog = LoadingDialog.createLoadingDialog(
				RequireQrCodeDialog.this, getString(R.string.text_loading));

		new Thread(getQrCodeFromServerRunnable).start();

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	Runnable getQrCodeFromServerRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getQrCodeFromServer();
		}
	};

	private void getQrCodeFromServer() {
		try {

			Map<String, String> map = new HashMap<String, String>();
			if (childId != null)
				map.put("childId", childId);
			// System.out.println("childId--->" + childId);

			String retStr = HttpRequestUtils.post(
					HttpConstants.REQUIRE_OR_GET_QR_CODE, map);
			System.out.println("retStrpost======>" + retStr);
			if (retStr.equals(HttpConstants.HTTP_POST_RESPONSE_EXCEPTION)
					|| retStr.equals("") || retStr.length() == 0) {
				System.out.println("connect error");

				Message msg = handler.obtainMessage();
				msg.what = Constants.CONNECT_ERROR;
				handler.sendMessage(msg);
			} else {
				if (retStr.length() > 4) {
					Message msg = handler.obtainMessage();
					msg.what = Constants.GET_QR_CODE_SUCCESS;
					handler.sendMessage(msg);

					deviceAddress = retStr;
					// System.out.println("deviceAddress -- >" +
					// deviceAddress);
				} else {
					Message msg = handler.obtainMessage();
					msg.what = Constants.GET_QR_CODE_FAIL;
					handler.sendMessage(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	Handler handler = new Handler() {

		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.GET_QR_CODE_SUCCESS:
				if (qrCodeDialog.isShowing() && qrCodeDialog != null) {
					qrCodeDialog.dismiss();
				}
				// txtQrCodeAddress.setText(deviceAddress);

				if (deviceAddress != null) {

					btMap = BarcodeCreater.creatBarcode(
							RequireQrCodeDialog.this, deviceAddress,
							DensityUtil.dip2px(RequireQrCodeDialog.this, 250),
							DensityUtil.dip2px(RequireQrCodeDialog.this, 250),
							true, 2);
					imgQrCode.setImageBitmap(btMap);
				}

				break;

			case Constants.GET_QR_CODE_FAIL:
				if (qrCodeDialog.isShowing() && qrCodeDialog != null) {
					qrCodeDialog.dismiss();
				}

				txtQrCodeAddress
						.setText(getString(R.string.text_Apply_qr_code_fail));
				imgQrCode.setBackground(getResources().getDrawable(
						R.drawable.ic_verify_cross));

				break;

			case Constants.CONNECT_ERROR:
				Toast.makeText(RequireQrCodeDialog.this,
						getString(R.string.text_network_error),
						Toast.LENGTH_LONG).show();
				break;

			}
		}
	};
}
