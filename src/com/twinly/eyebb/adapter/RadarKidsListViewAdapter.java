package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.bluetooth.BLEUtils;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.utils.ImageUtils;

public class RadarKidsListViewAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private ArrayList<Device> deviceList;
	private HashMap<String, Child> childMap;
	private boolean isRssiDisplayed;
	private ViewHolder viewHolder;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public TextView deviceRssi;
	}

	public RadarKidsListViewAdapter(Context context,
			ArrayList<Device> deviceList) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.deviceList = deviceList;
		childMap = DBChildren.getChildrenMap(context);
		imageLoader = ImageLoader.getInstance();
		isRssiDisplayed = true;
		sort();
	}

	public void setRssiDisplayed(boolean isRssiDisplayed) {
		this.isRssiDisplayed = isRssiDisplayed;
	}

	private void sort() {
		Collections.sort(this.deviceList, new Comparator<Device>() {

			@Override
			public int compare(Device lhs, Device rhs) {
				if (lhs.isMissed() == rhs.isMissed()) {
					long left = childMap.get(lhs.getMacAddress()).getChildId();
					long right = childMap.get(rhs.getMacAddress()).getChildId();
					return (int) (left - right);
				} else if (lhs.isMissed()) {
					return -1;
				} else {
					return 1;
				}

			}
		});
	}

	@Override
	public int getCount() {
		return deviceList.size();
	}

	@Override
	public Object getItem(int position) {
		return deviceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void notifyDataSetChanged() {
		sort();
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.list_item_radar_tracking_kid, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.deviceRssi = (TextView) convertView
					.findViewById(R.id.rssi);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(final ViewHolder viewHolder, final int position) {
		if (deviceList.get(position).isMissed()) {
			viewHolder.avatar.setBorderColor(context.getResources().getColor(
					R.color.red));
			viewHolder.deviceRssi.setTextColor(context.getResources().getColor(
					R.color.red));
			viewHolder.deviceRssi.setText(context
					.getString(R.string.btn_missed));
		} else {
			viewHolder.avatar.setBorderColor(context.getResources().getColor(
					R.color.green));
			viewHolder.deviceRssi.setTextColor(context.getResources().getColor(
					R.color.dark_grey));
			if (isRssiDisplayed) {
				int rssi = deviceList.get(position).getRssi();
				int displayedRssi = rssi + 100;
				switch (BLEUtils.getRssiLevel(rssi)) {
				case BLEUtils.RSSI_STRONG:
					viewHolder.deviceRssi.setText(context.getResources()
							.getString(R.string.text_rssi_strong)
							+ "("
							+ displayedRssi + ")");
					/*viewHolder.deviceRssi.setTextColor(context.getResources()
							.getColor(R.color.sky_blue));*/
					break;
				case BLEUtils.RSSI_GOOD:
					viewHolder.deviceRssi.setText(context.getResources()
							.getString(R.string.text_rssi_good)
							+ "("
							+ displayedRssi + ")");
					/*viewHolder.deviceRssi.setTextColor(context.getResources()
							.getColor(R.color.dark_grey));*/
					break;
				case BLEUtils.RSSI_WEAK:
					viewHolder.deviceRssi.setText(context.getResources()
							.getString(R.string.text_rssi_weak)
							+ "("
							+ displayedRssi + ")");
					/*viewHolder.deviceRssi.setTextColor(context.getResources()
							.getColor(R.color.red));*/
					break;
				}
			} else {
				viewHolder.deviceRssi.setText(context
						.getString(R.string.btn_supervised));
			}

		}
		Child child = childMap.get(deviceList.get(position).getMacAddress());
		if (TextUtils.isEmpty(child.getIcon()) == false) {
			imageLoader.displayImage(child.getIcon(), viewHolder.avatar,
					ImageUtils.avatarOpitons, null);
		} else {
			viewHolder.avatar.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.icon_avatar_dark));
		}
		viewHolder.name.setText(child.getName());

	}

}