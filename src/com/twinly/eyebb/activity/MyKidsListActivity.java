package com.twinly.eyebb.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.MykIdsListAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.customview.LinearLayoutForListView;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.utils.CommonUtils;

/**
 * @author eyebb team
 * 
 * @category MyKidsListActivity
 * 
 *           this activity is in options activity (The fourth layer), called
 *           children list. It displays three parts which contains binding
 *           children, unbinding children and children has been granted.
 * 
 */
public class MyKidsListActivity extends Activity {
	private LinearLayoutForListView listView;
	private MykIdsListAdapter adapter;
	private ArrayList<ArrayList<Child>> childrenList;
	private ArrayList<Child> allChildren;
	private ArrayList<Child> childrenWithAddress;
	private ArrayList<Child> childrenWithoutAddress;
	private ArrayList<Child> chidrenGuest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.btn_children_list));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_my_kids);

		listView = (LinearLayoutForListView) findViewById(R.id.listView);

		childrenList = new ArrayList<ArrayList<Child>>();

		childrenWithAddress = new ArrayList<Child>();
		childrenWithoutAddress = new ArrayList<Child>();
		chidrenGuest = new ArrayList<Child>();

		updateListView();
	}

	private void updateListView() {
		childrenList.clear();
		childrenWithAddress.clear();
		childrenWithoutAddress.clear();
		chidrenGuest.clear();

		allChildren = DBChildren.getChildrenList(this);

		Child child;
		for (int i = 0; i < allChildren.size(); i++) {
			child = allChildren.get(i);
			if (child.getRelationWithUser().equals("P")) {
				if (CommonUtils.isNull(child.getMacAddress())) {
					childrenWithoutAddress.add(child);
				} else {
					childrenWithAddress.add(child);
				}
			} else {
				chidrenGuest.add(child);
			}
		}
		if (childrenWithAddress.size() > 0) {
			childrenList.add(childrenWithAddress);
		}
		if (childrenWithoutAddress.size() > 0) {
			childrenList.add(childrenWithoutAddress);
		}
		if (chidrenGuest.size() > 0) {
			childrenList.add(chidrenGuest);
		}

		adapter = new MykIdsListAdapter(this, childrenWithAddress,
				childrenWithoutAddress, chidrenGuest);
		listView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.btn_add).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == 0) {
			Intent intent = new Intent(MyKidsListActivity.this,
					ChildInformationMatchingActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ActivityConstants.REQUEST_GO_TO_KID_PROFILE_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_UNBIND_SUCCESS) {
				updateListView();
			} else if (resultCode == ActivityConstants.RESULT_WRITE_MAJOR_MINOR_SUCCESS) {
				updateListView();
			}
		}
	}
}
