package com.twinly.eyebb.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.ChildSelectable;
import com.twinly.eyebb.utils.ImageUtils;
import com.twinly.eyebb.utils.RegularExpression;
import com.woozzu.android.util.StringMatcher;

public class KidsListViewSimpleAdapter extends ArrayAdapter<ChildSelectable> implements
		SectionIndexer {
	private Context context;
	private List<ChildSelectable> data;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private boolean isSelectable;

	public final class ViewHolder {
		public RelativeLayout rootLayout;
		public CircleImageView avatar;
		public TextView name;
		public CheckedTextView tvSelected;
	}

	public KidsListViewSimpleAdapter(Context context, List<ChildSelectable> data,
			boolean isSelectable) {
		super(context, android.R.layout.simple_list_item_1);

		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = data;
		this.isSelectable = isSelectable;

		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public ChildSelectable getItem(int position) {
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
			convertView = inflater.inflate(R.layout.list_item_kid_simple,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.rootLayout = (RelativeLayout) convertView
					.findViewById(R.id.root_layout);
			viewHolder.avatar = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.tvSelected = (CheckedTextView) convertView
					.findViewById(R.id.tv_selected);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		setUpView(viewHolder, position);
		return convertView;
	}

	private void setUpView(final ViewHolder viewHolder, final int position) {
		if (isSelectable) {
			viewHolder.tvSelected.setVisibility(View.VISIBLE);
			viewHolder.rootLayout.setClickable(true);
			data.get(position).setSelected(true);
			viewHolder.rootLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (viewHolder.tvSelected.isChecked()) {
						viewHolder.tvSelected.setChecked(false);
						data.get(position).setSelected(false);
					} else {
						viewHolder.tvSelected.setChecked(true);
						data.get(position).setSelected(true);
					}

				}
			});
		}
		final ChildSelectable child = data.get(position);
		if (TextUtils.isEmpty(child.getIcon()) == false) {
			imageLoader.displayImage(child.getIcon(), viewHolder.avatar,
					ImageUtils.avatarOpitons, null);
		} else {
			viewHolder.avatar.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.icon_avatar_dark));
		}
		viewHolder.name.setText(child.getName());
	}

	@Override
	public int getPositionForSection(int section) {
		// If there is no item for current section, previous section will be
		// selected
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				if (i == 0) {
					// For numeric section
					for (int k = 0; k <= 9; k++) {
						if (StringMatcher.match(String.valueOf((getItem(j))
								.getName().charAt(0)), String.valueOf(k)))
							return j;
					}
				} else {
					if (!RegularExpression
							.getStringToDetectionLetters(RegularExpression.mSections
									.charAt(i))) {
						if (StringMatcher
								.match(String.valueOf((getItem(j)).getName()
										.charAt(0)), String
										.valueOf(RegularExpression.mSections
												.charAt(i)))) {
							return j;
						}
					} else {
						if (RegularExpression
								.getStringToDetectionLetters((getItem(j))
										.getName().charAt(0))) {
							return j;
						}
					}

				}
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[RegularExpression.mSections.length()];
		for (int i = 0; i < RegularExpression.mSections.length(); i++)
			sections[i] = String.valueOf(RegularExpression.mSections.charAt(i));
		return sections;
	}
}
