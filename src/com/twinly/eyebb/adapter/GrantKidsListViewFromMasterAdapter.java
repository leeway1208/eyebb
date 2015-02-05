package com.twinly.eyebb.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.ChildForGrant;
import com.twinly.eyebb.utils.ImageUtils;

public class GrantKidsListViewFromMasterAdapter extends BaseAdapter {
	private Context context;
	private List<ChildForGrant> data;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name;
		public TextView selected;
		public RelativeLayout layout;
	}

	public GrantKidsListViewFromMasterAdapter(Context context,
			List<ChildForGrant> data) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = data;

		imageLoader = ImageLoader.getInstance();

	}

	@Override
	public int getCount() {

		return data.size();

	}

	@Override
	public ChildForGrant getItem(int position) {
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
			convertView = inflater.inflate(R.layout.list_item_grant_kid,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);

			viewHolder.selected = (TextView) convertView
					.findViewById(R.id.selected);
			viewHolder.selected.setVisibility(View.GONE);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(final ViewHolder viewHolder, final int position) {
		final ChildForGrant child = data.get(position);

		if (TextUtils.isEmpty(child.getIcon()) == false) {
			imageLoader.displayImage(child.getIcon(), viewHolder.avatar,
					ImageUtils.avatarOpitons, null);
		} else {
			viewHolder.avatar.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.ic_stub));
		}
		viewHolder.name.setText(child.getName());
	}
}
