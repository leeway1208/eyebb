package com.twinly.eyebb.customview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twinly.eyebb.R;
import com.twinly.eyebb.dialog.ChildDialog;
import com.twinly.eyebb.model.ChildForLocator;
import com.twinly.eyebb.utils.ImageUtils;

public class AvatarView {
	private Context context;
	private ChildForLocator child;
	private CircleImageView avatar;
	private ViewGroup avatarViewItem;
	private boolean isOnline;
	private ImageLoader imageLoader;

	public AvatarView(Context context, ChildForLocator child,
			ViewGroup viewGroup, boolean isOnline) {
		this.context = context;
		this.child = child;
		this.isOnline = isOnline;

		imageLoader = ImageLoader.getInstance();

		setUpView(viewGroup);
	}

	public ViewGroup getInstance() {
		return avatarViewItem;
	}

	private void setUpView(ViewGroup viewGroup) {
		avatarViewItem = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.item_avatar, viewGroup, false);
		avatar = (CircleImageView) avatarViewItem.findViewById(R.id.avatar);
		if (isOnline == false) {
			avatar.setAlpha(0.4f);
		}
		if (TextUtils.isEmpty(child.getIcon()) == false) {
			if (ImageUtils.isLocalImage(child.getIcon())) {
				avatar.setImageBitmap(ImageUtils.getBitmapFromLocal(child
						.getIcon()));
			} else {
				imageLoader.displayImage(child.getIcon(), avatar,
						ImageUtils.avatarOpitons, null);
			}
		}

		avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ChildDialog.class);
				intent.putExtra("macAddress", child.getMacAddress());
				intent.putExtra("id", child.getChildId());
				intent.putExtra("phone", child.getPhone());
				intent.putExtra("location", child.getLocationName());
				intent.putExtra("name", child.getName());
				intent.putExtra("icon", child.getIcon());
				context.startActivity(intent);
			}
		});
	}

}
