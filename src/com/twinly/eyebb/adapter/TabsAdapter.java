package com.twinly.eyebb.adapter;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.twinly.eyebb.fragment.IndoorLocatorFragment;
import com.twinly.eyebb.fragment.ProfileFragment;
import com.twinly.eyebb.fragment.ReportFragment;
import com.twinly.eyebb.fragment.RadarFragment;
import com.twinly.eyebb.utils.BroadcastUtils;

/**
 * This is a helper class that implements the management of tabs and all details
 * of connecting a ViewPager with associated TabHost. It relies on a trick.
 * Normally a tab host has a simple API for supplying a View or Intent that each
 * tab will show. This is not sufficient for switching between pages. So instead
 * we make the content part of the tab host 0dp high (it is not shown) and the
 * TabsAdapter supplies its own dummy view to show as the tab content. It
 * listens to changes in tabs, and takes care of switch to the correct paged in
 * the ViewPager whenever the selected tab changes.
 */
public class TabsAdapter extends FragmentPagerAdapter implements
		TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
	private final Context mContext;
	private final TabHost mTabHost;
	private final ViewPager mViewPager;
	// private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	private final ArrayList<Integer> mFragments = new ArrayList<Integer>();
	private IndoorLocatorFragment indoorLocatorFragment;
	private RadarFragment radarFragment;
	private ReportFragment reportFragment;
	private ProfileFragment profileFragment;

	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}

	public TabsAdapter(FragmentActivity activity, TabHost tabHost,
			ViewPager pager) {
		super(activity.getFragmentManager());
		mContext = activity;
		mTabHost = tabHost;
		mViewPager = pager;
		mTabHost.setOnTabChangedListener(this);
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addFragment(TabHost.TabSpec tabSpec,
			IndoorLocatorFragment indoorLocatorFragment) {
		this.indoorLocatorFragment = indoorLocatorFragment;
		mFragments.add(0);
		addTab(tabSpec);
	}

	public void addFragment(TabHost.TabSpec tabSpec, RadarFragment radarFragment) {
		this.radarFragment = radarFragment;
		mFragments.add(1);
		addTab(tabSpec);
	}

	public void addFragment(TabHost.TabSpec tabSpec,
			ReportFragment reportFragment) {
		this.reportFragment = reportFragment;
		mFragments.add(2);
		addTab(tabSpec);
	}

	public void addFragment(TabHost.TabSpec tabSpec,
			ProfileFragment profileFragment) {
		this.profileFragment = profileFragment;
		mFragments.add(3);
		addTab(tabSpec);
	}

	private void addTab(TabHost.TabSpec tabSpec) {
		tabSpec.setContent(new DummyTabFactory(mContext));
		mTabHost.addTab(tabSpec);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		switch (mFragments.get(position)) {
		case 0:
			return indoorLocatorFragment;
		case 1:
			return radarFragment;
		case 2:
			return reportFragment;
		default:
			return profileFragment;
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		mViewPager.setCurrentItem(position);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) throws NotFoundException {
		// Unfortunately when TabHost changes the current tab, it kindly
		// also takes care of putting focus on it when not in touch mode.
		// The jerk.
		// This hack tries to prevent this from pulling focus out of our
		// ViewPager.
		TabWidget widget = mTabHost.getTabWidget();
		int oldFocusability = widget.getDescendantFocusability();
		widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mTabHost.setCurrentTab(position);
		widget.setDescendantFocusability(oldFocusability);

		switch (position) {
		case 1:
			break;
		case 2:
			reportFragment.refreshPerformanceFragment();
			break;
		case 3:
			BroadcastUtils.cancelNotificationDot(mContext);
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}
}
