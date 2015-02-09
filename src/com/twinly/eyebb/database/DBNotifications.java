package com.twinly.eyebb.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.twinly.eyebb.model.Notifications;

public class DBNotifications {
	private static SQLiteDatabase getInstance(Context context) {
		return new DBHelper(context).openDatabase();
	}

	public static void insert(Context context, Notifications notification) {
		SQLiteDatabase db = getInstance(context);
		ContentValues values = new ContentValues();
		values.put("title", notification.getTitle());
		values.put("title_tc", notification.getTitleTc());
		values.put("title_sc", notification.getTitleSc());
		values.put("url", notification.getUrl());
		values.put("url_tc", notification.getUrlTc());
		values.put("url_sc", notification.getUrlSc());
		values.put("date", notification.getDate());
		values.put("icon", notification.getIcon());
		db.insertOrThrow("notification", null, values);
		db.close();
	}

	public static ArrayList<Notifications> getNotifications(Context context) {
		ArrayList<Notifications> list = new ArrayList<Notifications>();
		SQLiteDatabase db = getInstance(context);
		Cursor cursor = db.rawQuery("select * from notification", null);
		while (cursor.moveToNext()) {
			Notifications notification = new Notifications();
			notification.setTitle(cursor.getString(cursor
					.getColumnIndex("title")));
			notification.setTitleTc(cursor.getString(cursor
					.getColumnIndex("title_tc")));
			notification.setTitleSc(cursor.getString(cursor
					.getColumnIndex("title_sc")));
			notification.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			notification.setUrlSc(cursor.getString(cursor
					.getColumnIndex("url_sc")));
			notification.setUrlTc(cursor.getString(cursor
					.getColumnIndex("url_tc")));
			notification
					.setDate(cursor.getString(cursor.getColumnIndex("date")));
			notification
					.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
			list.add(notification);
		}
		cursor.close();
		db.close();
		return list;
	}

	public static void clear(Context context) {
		SQLiteDatabase db = getInstance(context);
		db.execSQL("delete from notification");
		db.close();
	}
}
