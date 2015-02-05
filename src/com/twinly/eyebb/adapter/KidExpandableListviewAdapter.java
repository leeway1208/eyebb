package com.twinly.eyebb.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.ImageUtils;

public class KidExpandableListviewAdapter extends BaseExpandableListAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private ArrayList<String> groupList;
	private ArrayList<ArrayList<Child>> childrenList;

	public KidExpandableListviewAdapter(Context context,
			ArrayList<String> groupList,
			ArrayList<ArrayList<Child>> childrenList) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.groupList = groupList;
		this.childrenList = childrenList;

		imageLoader = ImageLoader.getInstance();
	}

	private final class ChildrenHolder {
		public CircleImageView avatar;
		public TextView name;
	}

	private final class GroupHolder {
		public TextView title;
	}

	@Override
	public int getGroupCount() {
		return groupList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childrenList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childrenList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder groupHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_group_title,
					parent, false);
			groupHolder = new GroupHolder();
			groupHolder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(groupHolder);
		} else {
			groupHolder = (GroupHolder) convertView.getTag();
		}
		setUpGroupView(groupHolder, groupPosition);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildrenHolder childrenHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_kid_simple,
					parent, false);
			childrenHolder = new ChildrenHolder();
			childrenHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			childrenHolder.name = (TextView) convertView
					.findViewById(R.id.name);
			convertView.setTag(childrenHolder);
		} else {
			childrenHolder = (ChildrenHolder) convertView.getTag();
		}
		setUpChildrenView(childrenHolder, groupPosition, childPosition);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private void setUpGroupView(GroupHolder groupHolder, int groupPosition) {
		groupHolder.title.setText(groupList.get(groupPosition));
	}

	private void setUpChildrenView(ChildrenHolder childrenHolder,
			int groupPosition, int childPosition) {
		Child child = childrenList.get(groupPosition).get(childPosition);
		if (TextUtils.isEmpty(child.getIcon()) == false) {
			imageLoader.displayImage(child.getIcon(), childrenHolder.avatar,
					ImageUtils.avatarOpitons, null);
		} else {
			childrenHolder.avatar.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.icon_avatar_dark));
		}
		childrenHolder.name.setText(child.getName());
	}

}
