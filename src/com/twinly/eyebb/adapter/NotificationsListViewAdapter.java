package com.twinly.eyebb.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Notifications;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.ImageUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class NotificationsListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Notifications> list;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;

	private final class ViewHolder {
		private CircleImageView icon;
		private TextView title;
		private TextView date;
	}

	public NotificationsListViewAdapter(Context context,
			List<Notifications> list) {
		this.context = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder viewHolder;
		if (convertView == null) {
			v = mInflater.inflate(R.layout.list_item_activity_info, parent,
					false);
			viewHolder = new ViewHolder();
			viewHolder.icon = (CircleImageView) v.findViewById(R.id.icon);
			viewHolder.title = (TextView) v.findViewById(R.id.title);
			viewHolder.date = (TextView) v.findViewById(R.id.date);
			v.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) v.getTag();
		}
		setUpView(viewHolder, position);
		return v;
	}

	private void setUpView(final ViewHolder viewHolder, final int position) {
		Notifications activityInfo = list.get(position);
		switch (SharePrefsUtils.getLanguage(context)) {
		case Constants.LOCALE_CN:
			viewHolder.title.setText(activityInfo.getTitleSc());
			break;
		case Constants.LOCALE_HK:
		case Constants.LOCALE_TW:
			viewHolder.title.setText(activityInfo.getTitleTc());
			break;
		default:
			viewHolder.title.setText(activityInfo.getTitle());
			break;
		}
		viewHolder.date.setText(activityInfo.getDate());
		if (CommonUtils.isNotNull(activityInfo.getIcon())) {
			imageLoader.displayImage(activityInfo.getIcon(), viewHolder.icon,
					ImageUtils.locationIconOpitons, null);
		}
	}
}
