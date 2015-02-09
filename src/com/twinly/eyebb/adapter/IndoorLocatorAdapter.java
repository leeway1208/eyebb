package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.customview.AvatarView;
import com.twinly.eyebb.model.ChildForLocator;
import com.twinly.eyebb.model.Location;
import com.twinly.eyebb.utils.ImageUtils;

public class IndoorLocatorAdapter extends BaseAdapter {
	private final int[] backgrouds = new int[] { R.drawable.bg_home_blue01,
			R.drawable.bg_home_green01, R.drawable.bg_home_yellow01,
			R.drawable.bg_home_orange, R.drawable.bg_home_pink,
			R.drawable.bg_home_purple, R.drawable.bg_home_blue02,
			R.drawable.bg_home_green02, R.drawable.bg_home_yellow02 };

	private Context context;
	private HashMap<Long, Location> locationMap;
	private HashMap<Long, ChildForLocator> childrenMap;
	private List<HashMap.Entry<Long, ArrayList<Long>>> list;

	private LayoutInflater inflater;
	private boolean isViewAllRooms;
	private ImageLoader imageLoader;

	private final class ViewHolder {
		public LinearLayout contentLayout;
		public ImageView icon;
		public TextView areaName;
		public TextView childrenNum;
		public ViewGroup avatarContainer;
	}

	public IndoorLocatorAdapter(Context context,
			List<HashMap.Entry<Long, ArrayList<Long>>> list,
			HashMap<Long, Location> locationMap,
			HashMap<Long, ChildForLocator> childrenMap, boolean isViewAllRooms) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.locationMap = locationMap;
		this.childrenMap = childrenMap;
		this.isViewAllRooms = isViewAllRooms;
		this.list = list;

		imageLoader = ImageLoader.getInstance();
		sort();
	}

	@Override
	public void notifyDataSetChanged() {
		sort();
		super.notifyDataSetChanged();
	}

	public void setViewAllRooms(boolean isViewAllRooms) {
		this.isViewAllRooms = isViewAllRooms;
		sort();
	}

	private void sort() {
		if (isViewAllRooms) {
			// sort the location by location id
			Collections.sort(list,
					new Comparator<HashMap.Entry<Long, ArrayList<Long>>>() {

						@Override
						public int compare(Entry<Long, ArrayList<Long>> lhs,
								Entry<Long, ArrayList<Long>> rhs) {
							return (int) (locationMap.get(lhs.getKey()).getId() - locationMap
									.get(rhs.getKey()).getId());
						}

					});
		} else {
			// remove the empty location
			for (int i = 0; i < this.list.size(); i++) {
				if (this.list.get(i).getValue().size() == 0) {
					this.list.remove(i);
					i--;
				}
			}
			// sort the location by the children number
			Collections.sort(list,
					new Comparator<HashMap.Entry<Long, ArrayList<Long>>>() {

						@Override
						public int compare(Entry<Long, ArrayList<Long>> lhs,
								Entry<Long, ArrayList<Long>> rhs) {
							if (lhs.getValue().size() == rhs.getValue().size()) {
								return 0;
							} else {
								return rhs.getValue().size()
										- lhs.getValue().size();
							}
						}

					});
		}
		// move the entry and exit location to the bottom
		Collections.sort(list,
				new Comparator<HashMap.Entry<Long, ArrayList<Long>>>() {

					@Override
					public int compare(Entry<Long, ArrayList<Long>> lhs,
							Entry<Long, ArrayList<Long>> rhs) {
						if (locationMap.get(lhs.getKey()).getType().equals("X")
								|| locationMap.get(lhs.getKey()).getType()
										.equals("N")) {
							return 1;
						} else if (locationMap.get(rhs.getKey()).getType()
								.equals("X")
								|| locationMap.get(rhs.getKey()).getType()
										.equals("N")) {
							return -1;
						} else {
							return 0;
						}
					}

				});
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
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
			convertView = inflater.inflate(R.layout.list_item_indoor_locator,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.contentLayout = (LinearLayout) convertView
					.findViewById(R.id.content);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.areaName = (TextView) convertView
					.findViewById(R.id.area_name);
			viewHolder.childrenNum = (TextView) convertView
					.findViewById(R.id.children_num);
			viewHolder.avatarContainer = (ViewGroup) convertView
					.findViewById(R.id.avatarContainer);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(ViewHolder viewHolder, int position) {
		// clear the view
		viewHolder.avatarContainer.removeAllViews();

		String locationType = locationMap.get(list.get(position).getKey())
				.getType();
		String locationName = locationMap.get(list.get(position).getKey())
				.getDisplayName(context);
		String locationIcon = locationMap.get(list.get(position).getKey())
				.getIcon();

		// set the area name
		viewHolder.areaName.setText(locationName);

		ArrayList<Long> childrenIds = new ArrayList<Long>();
		childrenIds.addAll(list.get(position).getValue());

		// remove the child if he showed in the EXIT area and stay more than 10 mins.
		if (locationType.equals("X")) {
			for (int i = 0; i < childrenIds.size(); i++) {
				if (System.currentTimeMillis()
						- childrenMap.get(childrenIds.get(i))
								.getLastAppearTime() > Constants.validTimeDuration) {
					childrenIds.remove(i);
					i--;
				}
			}
		}
		for (int i = 0; i < childrenIds.size(); i++) {
			childrenMap.get(childrenIds.get(i)).setLocationName(locationName);

			// add the avatar to flowlayout
			AvatarView avatarView;
			if (System.currentTimeMillis()
					- childrenMap.get(childrenIds.get(i)).getLastAppearTime() < Constants.validTimeDuration) {
				avatarView = new AvatarView(context,
						childrenMap.get(childrenIds.get(i)),
						viewHolder.avatarContainer, true);
			} else {
				avatarView = new AvatarView(context,
						childrenMap.get(childrenIds.get(i)),
						viewHolder.avatarContainer, false);
			}
			viewHolder.avatarContainer.addView(avatarView.getInstance(), 0);
		}

		// set the the number of children
		viewHolder.childrenNum.setText(String.valueOf(childrenIds.size()));

		// set location icon
		imageLoader.displayImage(locationIcon, viewHolder.icon,
				ImageUtils.locationIconOpitons, null);
		viewHolder.contentLayout.setBackgroundResource(backgrouds[position
				% backgrouds.length]);

	}
}
