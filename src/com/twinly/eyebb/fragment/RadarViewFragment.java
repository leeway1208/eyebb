package com.twinly.eyebb.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.twinly.eyebb.R;
import com.twinly.eyebb.customview.RadarView;
import com.twinly.eyebb.model.Device;

@SuppressLint("NewApi")
public class RadarViewFragment extends Fragment {
	private ImageView radarRotate;
	private RelativeLayout radarViewContainer;
	private RadarView radarView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_radar_view, container,
				false);
		radarRotate = (ImageView) v.findViewById(R.id.bg_radar_rotate_img);
		radarViewContainer = (RelativeLayout) v.findViewById(R.id.radar_view);
		radarViewContainer.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						radarView.initialize(
								radarViewContainer.getHeight() / 2,
								getActivity());
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
							radarViewContainer.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						} else {
							radarViewContainer.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						}

					}
				});
		radarView = new RadarView(getActivity());
		radarViewContainer.addView(radarView);
		return v;
	}

	public void startAnimation() {
		final Animation anim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.rotate_anim);
		anim.setFillAfter(true);
		radarRotate.startAnimation(anim);
	}

	public void stopAnimation() {
		if (radarRotate != null) {
			radarRotate.clearAnimation();
		}
	}

	public void updateView(ArrayList<Device> scannedDeviceList) {
		radarView.updateView(scannedDeviceList);
	}
}
