package com.sust.game;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {
	private static final int		DATABASE_VERSION	= 1;
	private static final String		DATABASE_NAME		= "game.db";
	private static final String		DATA				= "data";

	public static final String		ID					= "_id";
	public static final String		NAME				= "name";
	public static final String		SCORE				= "score";

	private final DatabaseHelper	database;
	private final SQLiteDatabase	sqLiteDatabase;

	public DB(Context context) {
		database = new DatabaseHelper(context);
		sqLiteDatabase = database.getWritableDatabase();
	}

	public void closeDataBase() {
		if (sqLiteDatabase != null)
			sqLiteDatabase.close();
		if (database != null)
			database.close();
	}

	public class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATA + "( " + ID
					+ "  INTEGER PRIMARY KEY, " + NAME + " TEXT," + SCORE
					+ " INTEGER)");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATA);
			onCreate(db);
		}

	}

	public void saveData(Data data) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(NAME, data.getName());
			contentValues.put(SCORE, data.getScore());
			sqLiteDatabase.insert(DATA, null, contentValues);
			Log.e("data ", data.getName());
		} catch (SQLException e) {
		} catch (Exception e) {
		}
	}

	public void saveDatas(List<Data> datas) {
		for (int i = 0; i < datas.size(); i++) {
			saveData(datas.get(i));
		}
	}

	public ArrayList<Data> readData() {
		ArrayList<Data> datas = new ArrayList<Data>();
		try {
			Cursor cur = sqLiteDatabase.rawQuery("select * from " + DATA
					+ " order by " + SCORE + " desc", null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						Data data = new Data();
						data.setName(cur.getString(1));
						data.setScore(cur.getInt(2));
						datas.add(data);
						Log.e("read data ",
								data.getName() + " : " + data.getScore());
					} while (cur.moveToNext());
				}
				if (!cur.isClosed()) {
					cur.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datas;
	}
}
