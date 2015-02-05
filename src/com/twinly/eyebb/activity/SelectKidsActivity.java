package com.twinly.eyebb.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.KidsListViewSimpleAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.database.DBChildren;
import com.twinly.eyebb.model.ChildSelectable;
import com.twinly.eyebb.model.SerializableChildrenList;
import com.woozzu.android.widget.IndexableListView;

/**
 * @author eyebb team
 * 
 * @category SelectKidsActivity
 * 
 *           this activity is used to select the child in the
 *           RadarFragment(Activity). it main function is supported to anti-thief.
 * 
 */
public class SelectKidsActivity extends Activity {
	public static final String EXTRA_CHILDREN_LIST = "children_list";

	private IndexableListView mListView;
	private KidsListViewSimpleAdapter adapter;
	private ArrayList<ChildSelectable> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_change_kids));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_select_kids);

		mListView = (IndexableListView) findViewById(R.id.listview);

		mList = DBChildren.getChildrenListWithAddress(this);
		adapter = new KidsListViewSimpleAdapter(this, mList, true);
		mListView.setAdapter(adapter);
		mListView.setFastScrollEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.btn_confirm).setShowAsAction(
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
			Intent data = new Intent();
			SerializableChildrenList serializableChildrenList = new SerializableChildrenList();
			serializableChildrenList.setList(mList);
			Bundle bundle = new Bundle();
			bundle.putSerializable(EXTRA_CHILDREN_LIST,
					serializableChildrenList);
			data.putExtras(bundle);
			setResult(ActivityConstants.RESULT_RESULT_OK, data);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
