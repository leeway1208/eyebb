package com.twinly.eyebb.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.twinly.eyebb.model.ActivityInfo;

public class DBActivityInfo {
	private static SQLiteDatabase getInstance(Context context) {
		return new DBHelper(context).openDatabase();
	}

	public static void insert(Context context, ActivityInfo activityInfo) {
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("child_id", activityInfo.getChildId());
		values.put("title", activityInfo.getTitle());
		values.put("title_tc", activityInfo.getTitleTc());
		values.put("title_sc", activityInfo.getTitleSc());
		values.put("url", activityInfo.getUrl());
		values.put("url_tc", activityInfo.getUrlTc());
		values.put("url_sc", activityInfo.getUrlSc());
		values.put("date", activityInfo.getDate());
		values.put("icon", activityInfo.getIcon());
		db.insertOrThrow("activity_infos", null, values);
		db.close();
	}

	public static ArrayList<ActivityInfo> getActivityInfoByChildId(
			Context context, long childId) {
		ArrayList<ActivityInfo> list = new ArrayList<ActivityInfo>();
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery(
				"select * from activity_infos where child_id = " + childId,
				null);
		while (cursor.moveToNext()) {
			ActivityInfo activityInfo = new ActivityInfo();
			activityInfo.setChildId(childId);
			activityInfo.setTitle(cursor.getString(cursor
					.getColumnIndex("title")));
			activityInfo.setTitleTc(cursor.getString(cursor
					.getColumnIndex("title_tc")));
			activityInfo.setTitleSc(cursor.getString(cursor
					.getColumnIndex("title_sc")));
			activityInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			activityInfo.setUrlSc(cursor.getString(cursor
					.getColumnIndex("url_sc")));
			activityInfo.setUrlTc(cursor.getString(cursor
					.getColumnIndex("url_tc")));
			activityInfo
					.setDate(cursor.getString(cursor.getColumnIndex("date")));
			activityInfo
					.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
			list.add(activityInfo);
		}
		cursor.close();
		db.close();
		return list;
	}

	public static void deleteByChildId(Context context, long childId) {
		SQLiteDatabase db = getInstance(context);
		db.delete("activity_infos", "child_id=?",
				new String[] { String.valueOf(childId) });
		db.close();
	}

	public static void clear(Context context) {
		SQLiteDatabase db = getInstance(context);
		db.execSQL("delete from activity_infos");
		db.close();
	}
}
