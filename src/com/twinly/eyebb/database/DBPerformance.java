package com.twinly.eyebb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.twinly.eyebb.model.Performance;

public class DBPerformance {
	private static SQLiteDatabase getInstance(Context context) {
		return new DBHelper(context).openDatabase();
	}

	public static void insert(Context context, Performance performance) {
		if (updateIfExist(context, performance)) {
			return;
		}
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("child_id", performance.getChildId());
		values.put("json_data", performance.getJsonData());
		values.put("last_update_date", performance.getLastUpdateTime());
		db.insertOrThrow("performance", null, values);
		db.close();
	}

	private static boolean updateIfExist(Context context,
			Performance performance) {
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("json_data", performance.getJsonData());
		values.put("last_update_date", performance.getLastUpdateTime());
		int result = db.update("performance", values, "child_id=?",
				new String[] { String.valueOf(performance.getChildId()) });

		db.close();
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}

	public static Performance getPerformanceByChildId(Context context,
			long childId) {
		Performance performance = null;
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery(
				"select * from performance where child_id = " + childId, null);
		if (cursor.moveToFirst()) {
			performance = new Performance();
			performance.setChildId(childId);
			performance.setJsonData(cursor.getString(cursor
					.getColumnIndex("json_data")));
			performance.setLastUpdateTime(cursor.getString(cursor
					.getColumnIndex("last_update_date")));
		}
		db.close();
		return performance;
	}

	public static void clear(Context context) {
		SQLiteDatabase db = getInstance(context);
		db.execSQL("delete from performance");
		db.close();

	}
}
