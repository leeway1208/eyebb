package com.twinly.eyebb.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.twinly.eyebb.model.Child;
import com.twinly.eyebb.model.ChildSelectable;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.utils.CommonUtils;

public class DBChildren {
	private static SQLiteDatabase getInstance(Context context) {
		return new DBHelper(context).openDatabase();
	}

	public static void insert(Context context, Child child) {
		if (updateIfExist(context, child)) {
			return;
		}
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("child_id", child.getChildId());
		values.put("name", child.getName());
		values.put("icon", child.getIcon());
		values.put("phone", child.getPhone());
		values.put("mac_address", child.getMacAddress());
		values.put("relation_with_user", child.getRelationWithUser());
		db.insertOrThrow("children", null, values);
		db.close();
	}

	private static boolean updateIfExist(Context context, Child child) {
		SQLiteDatabase db = getInstance(context);
		// if exist the friend, update his information
		ContentValues values = new ContentValues();
		values.put("child_id", child.getChildId());
		values.put("name", child.getName());
		//values.put("icon", child.getIcon());
		values.put("phone", child.getPhone());
		values.put("mac_address", child.getMacAddress());
		values.put("relation_with_user", child.getRelationWithUser());
		int result = db.update("children", values, "child_id=?",
				new String[] { String.valueOf(child.getChildId()) });

		db.close();
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}

	public static HashMap<String, Child> getChildrenMap(Context context) {
		HashMap<String, Child> map = new HashMap<String, Child>();
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery("select * from children", null);
		while (cursor.moveToNext()) {
			Child child = createChild(cursor);
			if (CommonUtils.isNotNull(child.getMacAddress())) {
				map.put(child.getMacAddress(), child);
			}
		}
		cursor.close();
		db.close();
		return map;
	}

	public static HashMap<String, Device> getChildrenMapWithAddress(
			Context context) {
		HashMap<String, Device> map = new HashMap<String, Device>();
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery(
				"select * from children where mac_address != '" + "'", null);
		while (cursor.moveToNext()) {
			Child child = createChild(cursor);
			map.put(child.getMacAddress(), new Device(child.getMacAddress()));
		}
		cursor.close();
		db.close();
		return map;
	}

	public static ArrayList<Child> getChildrenList(Context context) {
		ArrayList<Child> childList = new ArrayList<Child>();
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery("select * from children", null);
		while (cursor.moveToNext()) {
			Child child = createChild(cursor);
			childList.add(child);
		}
		cursor.close();
		db.close();
		return childList;
	}

	public static ArrayList<ChildSelectable> getChildrenListWithAddress(
			Context context) {
		ArrayList<ChildSelectable> childList = new ArrayList<ChildSelectable>();
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery(
				"select * from children where mac_address != '" + "'", null);
		while (cursor.moveToNext()) {
			ChildSelectable childSelectable = new ChildSelectable(
					createChild(cursor));
			childList.add(childSelectable);
		}
		cursor.close();
		db.close();
		return childList;
	}

	public static Child getChildById(Context context, long childId) {
		Child child = null;
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery("select * from children where child_id = "
				+ childId, null);
		if (cursor.moveToFirst()) {
			child = createChild(cursor);
		}
		cursor.close();
		db.close();
		return child;
	}

	public static String getChildIconById(Context context, long childId) {
		String icon = "";
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery("select * from children where child_id = "
				+ childId, null);
		if (cursor.moveToFirst()) {
			icon = cursor.getString(cursor.getColumnIndex("icon"));
		}
		cursor.close();
		db.close();
		return icon;
	}

	public static void updateMacAddressByChildId(Context context, long childId,
			String macAddress) {
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("mac_address", macAddress);
		db.update("children", values, "child_id=?",
				new String[] { String.valueOf(childId) });
		db.close();
	}

	public static void updateIconByChildId(Context context, long childId,
			String icon) {
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("icon", icon);
		db.update("children", values, "child_id=?",
				new String[] { String.valueOf(childId) });
		db.close();
	}

	private static Child createChild(Cursor cursor) {
		Child child = new Child();
		child.setChildId(cursor.getLong(cursor.getColumnIndex("child_id")));
		child.setName(cursor.getString(cursor.getColumnIndex("name")));
		child.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
		child.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
		child.setMacAddress(cursor.getString(cursor
				.getColumnIndex("mac_address")));
		child.setRelationWithUser(cursor.getString(cursor
				.getColumnIndex("relation_with_user")));
		return child;
	}

	public static void clear(Context context) {
		SQLiteDatabase db = getInstance(context);
		db.execSQL("delete from children");
		db.close();
	}

}
