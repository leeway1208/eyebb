package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.activity.GrantKidsActivity;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.User;

public class GuestListViewAdapter extends BaseAdapter {
	private Context context;
	private List<User> data;
	private LayoutInflater inflater;
	private ViewGroup child_item;

	private List<User> master_data;

	private ArrayList<Child> auth_from_master_children_data;
	private ArrayList<Child> new_children_data;

	public final class ViewHolder {
		public CircleImageView avatar;
		public TextView name_left;
		public TextView phone_left;

		public TextView name_right;
		public TextView phone_right;

		public LinearLayout re_layout_left;
		public LinearLayout re_layout_right;

		public ViewGroup avatarContainer;

		public TextView auth_to_children_num;

		public TextView tv_authorized_to_others;

	}

	public GuestListViewAdapter(Context context, List<User> data,
			List<User> master_data,
			ArrayList<Child> auth_from_master_children_data) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = data;

		this.master_data = master_data;
		this.auth_from_master_children_data = auth_from_master_children_data;
	}

	/**
	 * we just need two list
	 */
	@Override
	public int getCount() {
		System.out.println("data.size()-->" + data.size());
		// start from 0
		return 2;
	}

	@Override
	public User getItem(int position) {
		// return data.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {

			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item_grant_kid_new,
					parent, false);

			viewHolder.avatarContainer = (ViewGroup) convertView
					.findViewById(R.id.avatarContainer);
			viewHolder.auth_to_children_num = (TextView) convertView
					.findViewById(R.id.auth_to_children_num);
			viewHolder.tv_authorized_to_others = (TextView) convertView
					.findViewById(R.id.tv_authorized_to_others);
			if (position == 1) {
				convertView.findViewById(R.id.liner_g_to_m).setBackground(
						context.getResources().getDrawable(
								R.drawable.bg_home_blue01));
			}

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, int position) {

		viewHolder.avatarContainer.removeAllViews();

		/*
		 * return 2 list , 1 is for master list, 0 is for guest list
		 * 
		 */
		if (position == 1) {

			viewHolder.tv_authorized_to_others.setText(context
					.getString(R.string.text_authorization_from_others));

			new_children_data = new ArrayList<Child>();

			for (int i = 0; i < master_data.size(); i++) {

				for (int y = 0; y < auth_from_master_children_data.size(); y++) {
					if (master_data
							.get(i)
							.getGuardianId()
							.equals(auth_from_master_children_data.get(y)
									.getPhone())) {
						new_children_data.add(auth_from_master_children_data
								.get(y));
						break;
					}
				}

			}
			for (int i = 0; i < master_data.size();) {

				/**
				 * 
				 * this view has two sides (left and right)
				 * and we get two data at once, so i = 1 + 2;
				 * 
				 */
				child_item = (ViewGroup) LayoutInflater.from(context).inflate(
						R.layout.list_item_grant_kid_new_child_item,
						viewHolder.avatarContainer, false);

				viewHolder.name_left = (TextView) child_item
						.findViewById(R.id.auth_nick_name_left);
				viewHolder.phone_left = (TextView) child_item
						.findViewById(R.id.auth_user_name_left);
				viewHolder.re_layout_left = (LinearLayout) child_item
						.findViewById(R.id.re_layout_left);

				viewHolder.name_right = (TextView) child_item
						.findViewById(R.id.auth_nick_name_right);
				viewHolder.phone_right = (TextView) child_item
						.findViewById(R.id.auth_user_name_right);
				viewHolder.re_layout_right = (LinearLayout) child_item
						.findViewById(R.id.re_layout_right);

				viewHolder.name_left.setText(master_data.get(i).getName());
				viewHolder.phone_left.setText(master_data.get(i)
						.getPhoneNumber());
				final User guest_left = master_data.get(i);

				viewHolder.re_layout_left
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								Intent intent = new Intent(context,
										GrantKidsActivity.class);
								intent.putExtra("guestId",
										guest_left.getGuardianId());
								intent.putExtra("guestName",
										guest_left.getName());
								intent.putExtra("from_where", "master");
								intent.putExtra("child_data", new_children_data);
								context.startActivity(intent);
								((Activity) context).finish();

							}
						});

				if (i + 1 > master_data.size() - 1) {
					viewHolder.re_layout_right.setVisibility(View.INVISIBLE);
				} else {
					viewHolder.name_right.setText(master_data.get(i + 1)
							.getName());
					viewHolder.phone_right.setText(master_data.get(i + 1)
							.getPhoneNumber());
					final User guest_right = master_data.get(i + 1);

					viewHolder.re_layout_right
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {

									Intent intent = new Intent(context,
											GrantKidsActivity.class);
									intent.putExtra("guestId",
											guest_right.getGuardianId());
									intent.putExtra("guestName",
											guest_right.getName());
									intent.putExtra("from_where", "master");
									intent.putExtra("child_data",
											new_children_data);
									context.startActivity(intent);
									((Activity) context).finish();

								}
							});

				}

				i = i + 2;
				viewHolder.avatarContainer.addView(child_item, 0);
			}
			viewHolder.auth_to_children_num.setText(master_data.size() + "");
		} else {

			for (int i = 0; i < data.size();) {

				child_item = (ViewGroup) LayoutInflater.from(context).inflate(
						R.layout.list_item_grant_kid_new_child_item,
						viewHolder.avatarContainer, false);

				viewHolder.name_left = (TextView) child_item
						.findViewById(R.id.auth_nick_name_left);
				viewHolder.phone_left = (TextView) child_item
						.findViewById(R.id.auth_user_name_left);
				viewHolder.re_layout_left = (LinearLayout) child_item
						.findViewById(R.id.re_layout_left);

				viewHolder.name_right = (TextView) child_item
						.findViewById(R.id.auth_nick_name_right);
				viewHolder.phone_right = (TextView) child_item
						.findViewById(R.id.auth_user_name_right);
				viewHolder.re_layout_right = (LinearLayout) child_item
						.findViewById(R.id.re_layout_right);

				viewHolder.name_left.setText(data.get(i).getName());
				viewHolder.phone_left.setText(data.get(i).getPhoneNumber());
				final User guest_left = data.get(i);

				viewHolder.re_layout_left
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

								Intent intent = new Intent(context,
										GrantKidsActivity.class);
								intent.putExtra("guestId",
										guest_left.getGuardianId());
								intent.putExtra("guestName",
										guest_left.getName());
								intent.putExtra("from_where", "guest");
								context.startActivity(intent);
								((Activity) context).finish();
							}
						});

				if (i + 1 > data.size() - 1) {
					viewHolder.re_layout_right.setVisibility(View.INVISIBLE);
				} else {
					viewHolder.name_right.setText(data.get(i + 1).getName());
					viewHolder.phone_right.setText(data.get(i + 1)
							.getPhoneNumber());
					final User guest_right = data.get(i + 1);

					viewHolder.re_layout_right
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {

									Intent intent = new Intent(context,
											GrantKidsActivity.class);
									intent.putExtra("guestId",
											guest_right.getGuardianId());
									intent.putExtra("guestName",
											guest_right.getName());
									intent.putExtra("from_where", "guest");
									context.startActivity(intent);
									((Activity) context).finish();
								}
							});

				}

				i = i + 2;
				viewHolder.avatarContainer.addView(child_item, 0);

			}

			viewHolder.auth_to_children_num.setText(data.size() + "");
		}

		// final User guest = data.get(position);

		// viewHolder.phone.setText(guest.getPhoneNumber());
		// viewHolder.name.setText(guest.getName());
	}

}
