package com.twinly.eyebb.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.User;

public class SearchGuestListViewAdapter extends BaseAdapter {
	private List<User> data;
	private LayoutInflater inflater;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public TextView phone;

	}

	public SearchGuestListViewAdapter(Context context, List<User> data) {
		inflater = LayoutInflater.from(context);
		this.data = data;
	}

	@Override
	public int getCount() {
		System.out.println("data.size()-->" + data.size());
		return data.size();

	}

	@Override
	public User getItem(int position) {
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
			convertView = inflater.inflate(R.layout.list_item_search_guest,
					parent, false);
			viewHolder = new ViewHolder();
			// viewHolder.avatar = (CircleImageView) convertView
			// .findViewById(R.id.avatar);
			//
			// //NO NEED
			// viewHolder.avatar.setVisibility(View.GONE);

			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, int position) {
		final User guest = data.get(position);
		// if (TextUtils.isEmpty(guest.getIcon()) == false) {
		// imageLoader.displayImage(guest.getIcon(), viewHolder.avatar,
		// CommonUtils.getDisplayImageOptions(), null);
		// } else {
		// viewHolder.avatar.setImageDrawable(context.getResources()
		// .getDrawable(R.drawable.ic_stub));
		// }

		viewHolder.phone.setText(guest.getPhoneNumber());
		viewHolder.name.setText(guest.getName());
	}
}
