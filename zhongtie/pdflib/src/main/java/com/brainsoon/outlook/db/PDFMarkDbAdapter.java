package com.brainsoon.outlook.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

//import android.widget.Toast;

public class PDFMarkDbAdapter {
	private static final String DATABASE_TABLE = "pdf_mark";
	private Context mCtx = null;
	private DBHelper mDbHelper;
	private SQLiteDatabase mDb;
	private String mBookNameString  = "";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_MARK_DESCRIBE = "mark_describe";
	public static final String KEY_INDEX = "mark_index";
	public static final String KEY_BOOK_NAME = "book_name";
	public static final String KEY_MODIFIED = "modified";
	
	public PDFMarkDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}
	
	public PDFMarkDbAdapter open( String bookName) throws SQLException {
		mDbHelper = DBHelper.getInstance(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		mBookNameString = bookName;
		return this;
	}
	
	public void close() {
		if(mDbHelper != null) {
			mDbHelper.close();
		}
	}
	
	public long add(String markDescribe, String index) {
		Date now = new Date();
		long nowTime = now.getTime();
		ContentValues args = new ContentValues();
		args.put(KEY_BOOK_NAME, mBookNameString);
		args.put(KEY_MARK_DESCRIBE, markDescribe);
		args.put(KEY_INDEX, index);
		args.put(KEY_MODIFIED, nowTime);

		return mDb.insert(DATABASE_TABLE, null, args);
	}
	
	public int count() {
		Cursor mCount= mDb.rawQuery("SELECT COUNT(*) FROM " 
				+ DATABASE_TABLE + " WHERE " + KEY_BOOK_NAME + "=?", new String[]{mBookNameString});
		mCount.moveToFirst();
		int total= mCount.getInt(0);
		mCount.close();
		return total;
	}
	
	public List<BookmarkItem> getall() {
		List<BookmarkItem> rs = new ArrayList<BookmarkItem>();
		BookmarkItem bc = null;
		Cursor cur = mDb.rawQuery("SELECT mark_describe,mark_index FROM " 
				+ DATABASE_TABLE + " WHERE " + KEY_BOOK_NAME + "=?", new String[]{mBookNameString});
		for(cur.moveToFirst(); ! cur.isAfterLast(); cur.moveToNext()){
			bc = new BookmarkItem(cur.getString(0), cur.getString(1));
			rs.add(bc);
		}
		return rs;
	}
	public String get(String index){
		Cursor cursor = mDb.rawQuery("SELECT mark_describe FROM " 
				+ DATABASE_TABLE + " WHERE " + KEY_BOOK_NAME + "=?" + " and " + KEY_INDEX + "=?", new String[]{mBookNameString, index});
		cursor.moveToFirst();
		String mark = cursor.getString(0);
		cursor.close();
		return mark;
	}
	
	public boolean exist(String index){
		Cursor mCount= mDb.rawQuery("SELECT COUNT(*) FROM " 
				+ DATABASE_TABLE + " WHERE " + KEY_BOOK_NAME + "=?" + " and " + KEY_INDEX + "=?", new String[]{mBookNameString, index});
		mCount.moveToFirst();
		boolean isExist= mCount.getInt(0) > 0;
		mCount.close();
		return isExist;
	}
	public boolean delete(String index) {
		return mDb.delete(DATABASE_TABLE, KEY_INDEX + "=?" + " and " 
				+ KEY_BOOK_NAME + "=?" , new String[]{index, mBookNameString}) > 0;
	}
	public boolean update(String describe, String index){
		Date now = new Date();
		long nowTime = now.getTime();
		ContentValues args = new ContentValues();
		args.put(KEY_MARK_DESCRIBE, describe);
		args.put(KEY_MODIFIED, nowTime);
		return mDb.update(DATABASE_TABLE, args,KEY_INDEX + "=?" + " and " 
				+ KEY_BOOK_NAME + "=?" , new String[]{index, mBookNameString}) > 0;
	}
}
