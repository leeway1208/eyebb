package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.KidsListViewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.dialog.KidsListOptionsDialog;
import com.twinly.eyebb.model.ChildForLocator;
import com.twinly.eyebb.model.SerializableChildrenMap;
import com.woozzu.android.widget.IndexableListView;

/**
 * @author eyebb team
 * 
 * @category KidsListActivity
 * 
 *           this activity is used for the first fragment activity. There is a
 *           button that open it at the bottom of the screen. It shows the kids
 *           list and also provide the search and sort functions.
 * 
 */
public class KidsListActivity extends Activity {
	private IndexableListView listView;
	private EditText etSearch;
	private List<Map.Entry<Long, ChildForLocator>> list;
	private List<Map.Entry<Long, ChildForLocator>> searchList;
	private KidsListViewAdapter adapter;
	private boolean isSortByName = true;
	private boolean isSortByLocation = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_kids_list));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		setContentView(R.layout.activity_kids_list);

		searchList = new ArrayList<Map.Entry<Long, ChildForLocator>>();
		Bundle bundle = getIntent().getExtras();
		SerializableChildrenMap serializableMap = (SerializableChildrenMap) bundle
				.get("childrenMap");

		if (serializableMap != null) {
			list = new ArrayList<Map.Entry<Long, ChildForLocator>>(serializableMap
					.getMap().entrySet());

			adapter = new KidsListViewAdapter(this, list, isSortByName,
					isSortByLocation);
		}

		etSearch = (EditText) findViewById(R.id.et_search);
		listView = (IndexableListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);
		listView.setFastScrollEnabled(true);

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				search(etSearch.getText().toString());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_kids_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void search(String keyword) {
		if (!TextUtils.isEmpty(keyword)) {
			searchList.clear();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getValue().getName().contains(keyword)) {
					searchList.add(list.get(i));
				}
			}
			adapter = new KidsListViewAdapter(KidsListActivity.this,
					searchList, isSortByName, isSortByLocation);
			listView.setAdapter(adapter);
		} else {
			adapter = new KidsListViewAdapter(KidsListActivity.this, list,
					isSortByName, isSortByLocation);
			listView.setAdapter(adapter);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.menu_options) {
			Intent intent = new Intent(this, KidsListOptionsDialog.class);
			intent.putExtra(KidsListOptionsDialog.EXTRA_SORT_BY_NAME,
					isSortByName);
			intent.putExtra(KidsListOptionsDialog.EXTRA_SORT_BY_LOCATION,
					isSortByLocation);
			startActivityForResult(intent,
					ActivityConstants.REQUEST_GO_TO_OPTIONS_DIALOG);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ActivityConstants.REQUEST_GO_TO_OPTIONS_DIALOG) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				if (isSortByName != data.getBooleanExtra(
						KidsListOptionsDialog.EXTRA_SORT_BY_NAME, isSortByName)
						|| isSortByLocation != data.getBooleanExtra(
								KidsListOptionsDialog.EXTRA_SORT_BY_LOCATION,
								isSortByLocation)) {
					isSortByName = data.getBooleanExtra(
							KidsListOptionsDialog.EXTRA_SORT_BY_NAME,
							isSortByName);
					isSortByLocation = data.getBooleanExtra(
							KidsListOptionsDialog.EXTRA_SORT_BY_LOCATION,
							isSortByLocation);
					adapter = new KidsListViewAdapter(KidsListActivity.this,
							list, isSortByName, isSortByLocation);
					listView.setAdapter(adapter);
				}
			}
		}
	}

}
