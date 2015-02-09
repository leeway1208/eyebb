package com.twinly.eyebb.customview;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.twinly.eyebb.R;
import com.twinly.eyebb.bluetooth.BLEUtils;
import com.twinly.eyebb.model.Device;

public class RadarView extends View {
	private int radius;
	private int smallRadius;
	private int normalRadius;
	private int largeRadius;
	private ArrayList<Device> scannedDeviceList;
	private Bitmap bitmap;
	private Paint paint;

	public RadarView(Context context) {
		super(context);
		setDrawingCacheEnabled(true); //开启Cache
		buildDrawingCache();
	}

	public void initialize(int radius, Context context) {
		this.radius = radius;
		smallRadius = radius / 3;
		normalRadius = smallRadius * 2;
		largeRadius = smallRadius * 3;
		bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_radar_dot);
		paint = new Paint();
	}

	public void updateView(ArrayList<Device> scannedDeviceList) {
		int theta, r, x, y;
		for (int i = 0; i < scannedDeviceList.size(); i++) {
			if (BLEUtils.getRssiLevel(scannedDeviceList.get(i).getRssi()) == BLEUtils
					.getRssiLevel(scannedDeviceList.get(i).getPreRssi())) {
				if (scannedDeviceList.get(i).getAxisX() != 0
						|| scannedDeviceList.get(i).getAxisY() != 0) {
					continue;
				}
			}
			// update postion when rssi change a lot
			theta = (int) (Math.random() * 91);
			r = 0;
			switch (BLEUtils.getRssiLevel(scannedDeviceList.get(i).getRssi())) {
			case BLEUtils.RSSI_STRONG:
				r = (int) (normalRadius + Math.random()
						* (largeRadius - normalRadius + 1));
				break;
			case BLEUtils.RSSI_GOOD:
				r = (int) (smallRadius + Math.random()
						* (normalRadius - smallRadius + 1));
				break;
			case BLEUtils.RSSI_WEAK:
				r = (int) (Math.random() * (smallRadius + 1));
				break;
			}
			x = (int) (Math.cos(theta) * r);
			y = (int) (Math.sin(theta) * r);
			switch ((int) Math.random() * 2) {
			case 0:
				scannedDeviceList.get(i).setAxisX(radius - x);
				break;
			case 1:
				scannedDeviceList.get(i).setAxisX(radius + x);
				break;
			}
			switch ((int) Math.random() * 2) {
			case 0:
				scannedDeviceList.get(i).setAxisY(radius + y);
				break;
			case 1:
				scannedDeviceList.get(i).setAxisY(radius - y);
				break;
			}
		}
		this.scannedDeviceList = scannedDeviceList;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (scannedDeviceList != null) {
			for (int i = 0; i < scannedDeviceList.size(); i++) {
				canvas.drawBitmap(bitmap, scannedDeviceList.get(i).getAxisX(),
						scannedDeviceList.get(i).getAxisY(), paint);
			}
		}
		super.onDraw(canvas);
	}
}
