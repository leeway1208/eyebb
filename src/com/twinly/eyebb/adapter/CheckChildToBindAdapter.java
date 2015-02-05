package com.twinly.eyebb.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.twinly.eyebb.R;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.ImageUtils;

public class CheckChildToBindAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Child> data;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
	}

	public CheckChildToBindAdapter(Context context,
			ArrayList<Child> childrenList) {
		// TODO Auto-generated constructor stub
		inflater = LayoutInflater.from(context);
		this.context = context;
		System.out.println("childrenList>" + childrenList.get(0).getIcon());
		this.data = childrenList;

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}

	@Override
	public int getCount() {
		// System.out.println("data.size()>" + data.size());
		return data.size();
	}

	@Override
	public Child getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.dialog_check_child_to_bind_list_listitem, parent,
					false);

			viewHolder = new ViewHolder();
			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, int position) {
		final Child child = data.get(position);
		if (TextUtils.isEmpty(child.getIcon()) == false
				&& !child.getIcon().equals("null")) {
			imageLoader.displayImage(child.getIcon(), viewHolder.avatar,
					ImageUtils.avatarOpitons, null);
		} else {
			viewHolder.avatar.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.ic_stub));
		}
		viewHolder.name.setText(child.getName());
	}
}
