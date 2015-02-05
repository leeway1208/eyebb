package com.twinly.eyebb.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class PullToRefreshListView extends ListView {

	private float previousY;

	private PullToRefreshListener pullToRefreshListener;
	private boolean isRefreshing = false;
	private boolean allowScroll = true;
	private boolean lockPullAction = false;

	public interface PullToRefreshListener {
		/**
		 * Update the progressBar value when pull the listView
		 * @param value current progress
		 */
		public void updateProgressBar(int value);

		/**
		 * Cancel update the progressBar when release the listView 
		 */
		public void cancelProgressBar();
	}

	public PullToRefreshListView(Context context) {
		super(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setPullToRefreshListener(
			PullToRefreshListener pullToRefreshListener) {
		this.pullToRefreshListener = pullToRefreshListener;
	}

	/**
	 * @return If the list is in 'Refreshing' state
	 */
	public boolean isRefreshing() {
		return isRefreshing;
	}

	/**
	 * Default is false. When lockScrollWhileRefreshing is set to true, the list
	 * cannot scroll when in 'refreshing' mode. It's 'locked' on refreshing.
	 *
	 * @param lockScrollWhileRefreshing
	 */
	/*public void setRefreshing(boolean isRefreshing) {
		this.isRefreshing = isRefreshing;
		if (isRefreshing) {
			allowScroll = false;
		}
	}*/

	public void setLockPullAction(boolean lockPullAction) {
		this.lockPullAction = lockPullAction;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshing) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (getFirstVisiblePosition() == 0) {
				previousY = event.getY();
				allowScroll = true;
			} else {
				allowScroll = false;
			}

			break;

		case MotionEvent.ACTION_UP:
			if (getFirstVisiblePosition() == 0) {
				// cancel the progressbar
				pullToRefreshListener.cancelProgressBar();
			}
			allowScroll = true;
			break;

		case MotionEvent.ACTION_CANCEL:
			pullToRefreshListener.cancelProgressBar();
			allowScroll = true;
			break;

		case MotionEvent.ACTION_MOVE:
			if (allowScroll && getFirstVisiblePosition() == 0) {
				float y = event.getY();
				float diff = y - previousY;
				if (diff < -5) {
					// cancel the this scrolling
					allowScroll = false;
					pullToRefreshListener.cancelProgressBar();
				} else {
					int value = 0;
					if (diff <= 0) {
						value = -(int) Math
								.ceil((100 * (Math.ceil(-diff) / 400.0)));
					} else {
						value = (int) Math
								.ceil((100 * (Math.ceil(diff) / 400.0)));
					}
					if (lockPullAction == false)
						pullToRefreshListener.updateProgressBar(value);
				}
				previousY = y;
			}
			break;
		}

		return super.onTouchEvent(event);
	}

}
