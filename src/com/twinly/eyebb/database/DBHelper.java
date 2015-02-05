package com.twinly.eyebb.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.twinly.eyebb.constant.Constants;

public class DBHelper extends SQLiteOpenHelper {

	private SQLiteDatabase db;

	public DBHelper(Context context) {
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
	}

	public SQLiteDatabase openDatabase() {

		if (db == null) {
			/*
			 * 得到一个数据库的实例 调用这个方法时，查找系统中的资源， 如果不存在相关资源，调用onCreate(SQLiteDatabase
			 * db)方法, 如果存在，直接返回相关数据库
			 */
			// db = this.getWritableDatabase(Constants.DB_PASSWORD);
			db = this.getWritableDatabase();
		}
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer tableCreate = new StringBuffer();
		tableCreate = new StringBuffer();
		tableCreate.append("create table if not exists children")
				.append("(id integer primary key autoincrement,")
				.append("child_id integer,")
				.append("name text,")
				.append("icon text,")
				.append("phone text,")
				.append("relation_with_user text,")
				.append("mac_address text)");
		db.execSQL(tableCreate.toString());

		tableCreate = new StringBuffer();
		tableCreate.append("create table if not exists performance")
				.append("(id integer primary key autoincrement,")
				.append("child_id integer,")
				.append("json_data text,")
				.append("last_update_date text)");
		db.execSQL(tableCreate.toString());
			
		tableCreate = new StringBuffer();
		tableCreate.append("create table if not exists activity_infos")
				.append("(id integer primary key autoincrement,")
				.append("child_id integer,")
				.append("title text,")
				.append("title_tc text,")
				.append("title_sc text,")
				.append("url text,")
				.append("url_tc text,")
				.append("url_sc text,")
				.append("date text,")
				.append("icon text)");
		db.execSQL(tableCreate.toString());
		
		tableCreate = new StringBuffer();
		tableCreate.append("create table if not exists notification")
				.append("(id integer primary key autoincrement,")
				.append("title text,")
				.append("title_tc text,")
				.append("title_sc text,")
				.append("url text,")
				.append("url_tc text,")
				.append("url_sc text,")
				.append("date text,")
				.append("icon text)");
		db.execSQL(tableCreate.toString());
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

}
