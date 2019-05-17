package org.geometerplus.android.fbreader.extBkMarkDB;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * 
 * 操作标签数据库
 * @author zuo
 *
 */
public class BookMarkDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "fbBookMarks.db";
	private static final int DATABASE_VERSION = 8;
	private static BookMarkDBHelper dbInstance;
	
	private SQLiteDatabase mDb;
	private SQLiteStatement bookStatement;
	/**
	 * 获取单例
	 * @param context
	 * @return
	 */
	public static BookMarkDBHelper getInstance(Context context) {
		if (dbInstance == null) {
			dbInstance = new BookMarkDBHelper(context);
		}
		return dbInstance;
	}

	private BookMarkDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS myBookMark(" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"content VARCHAR(500)," +
				"createTime DATE," +
				"bookId INTEGER NOT NULL," +
				"elementIndex INTEGER NOT NULL," +
				"charIndex INTEGER NOT NULL," +
				"startParagraphIndex INTEGER NOT NULL," +
				"paragraphIndex INTEGER NOT NULL)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS myBookMark");
		onCreate(db);
	}
	
	/**
	 * 保存批注
	 * @param bk
	 */
	public void addBookMark(BookMark bk){
		Log.d("保存批注", bk.getContent()+"bookId:"+bk.getBookId() + " , paragraphIndex:" + bk.getParagraphIndex() + " , ElementIndex:" + bk.getElementIndex() + " , CharIndex:" + bk.getCharIndex() + " , startParagraphIndex:" + bk.getStartParagraphIndex());
		mDb = dbInstance.getWritableDatabase();
		bookStatement = mDb.compileStatement("INSERT OR REPLACE INTO myBookMark(content, createTime, bookId,paragraphIndex,elementIndex,charIndex,startParagraphIndex) VALUES (?, ?, ?,?,?,?,?)");
		
		bookStatement.bindString(1, bk.getContent());
		bookStatement.bindString(2, bk.getCreateTime()+"");
		bookStatement.bindLong(3, bk.getBookId());
		bookStatement.bindString(4, bk.getParagraphIndex()+"");
		bookStatement.bindString(5, bk.getElementIndex()+"");
		bookStatement.bindString(6, bk.getCharIndex()+"");
		bookStatement.bindString(7, bk.getStartParagraphIndex()+"");
		try {
			bookStatement.execute();
		} catch (SQLException e) {
			Log.d("保存出错 ", e.getMessage());
		}finally{
			if(null != bookStatement){
				bookStatement.close();
			}
			if(null != mDb){
				mDb.close();
			}
		}
	}
	
	/**
	 * 查询一本书的所有批注
	 * @param bookId
	 * @return List<BookMark>
	 */
	public List<BookMark> getBookById(Long bookId){
		List<BookMark> rs = new ArrayList<BookMark>();
		String sql = "SELECT _id,content,createTime,paragraphIndex,elementIndex,charIndex,startParagraphIndex FROM myBookMark WHERE bookId = ?";
		mDb = dbInstance.getWritableDatabase();
		try {
			Cursor mks = mDb.rawQuery(sql, new String[]{bookId+""});
			BookMark mk = null;
			for (mks.moveToFirst(); !mks.isAfterLast(); mks.moveToNext()) {
				mk = new BookMark();
				mk.set_id(mks.getLong(0));
				mk.setContent(mks.getString(1));
				//mk.setCreateTime(mks.getString(2));
				mk.setParagraphIndex(mks.getInt(3));
				mk.setElementIndex(mks.getInt(4));
				mk.setCharIndex(mks.getInt(5));
				mk.setBookId(bookId);
				mk.setStartParagraphIndex(mks.getInt(6));
				rs.add(mk);
			}
		} catch (SQLException e) {
			Log.d("查询出错 ", e.getMessage());
		}finally{
			if(null != bookStatement){
				bookStatement.close();
			}
			if(null != mDb){
				mDb.close();
			}
		}
		return rs;
	}
	/**
	 * 查询当前段落是否有批注
	 * @param bookId
	 * @param paragraphIndex
	 * @return BookMark
	 */
	public List<BookMark> getBookMarkByParagraphIndex(Long bookId,int paragraphIndex){
		List<BookMark> mksrs = new ArrayList<BookMark>();
		BookMark mk = null;
		String sql = "SELECT _id,content,createTime,paragraphIndex,elementIndex,charIndex,startParagraphIndex FROM myBookMark WHERE bookId = ? and paragraphIndex = ?";
		mDb = dbInstance.getWritableDatabase();
		try {
			Cursor mks = mDb.rawQuery(sql, new String[]{bookId+"",paragraphIndex+""});
			for (mks.moveToFirst(); !mks.isAfterLast(); mks.moveToNext()) {
				mk = new BookMark();
				//mks.moveToLast();
				mk.set_id(mks.getLong(0));
				mk.setContent(mks.getString(1));
				//mk.setCreateTime(mks.getString(2));
				mk.setParagraphIndex(mks.getInt(3));
				mk.setElementIndex(mks.getInt(4));
				mk.setCharIndex(mks.getInt(5));
				mk.setBookId(bookId);
				mk.setStartParagraphIndex(mks.getInt(6));
				mksrs.add(mk);
			}
		} catch (SQLException e) {
			Log.d("查询出错 ", e.getMessage());
		}finally{
			if(null != bookStatement){
				bookStatement.close();
			}
			if(null != mDb){
				mDb.close();
			}
		}
		return mksrs;
	}
	
	/**
	 * 按主键删除标签，批注
	 * @param _id
	 */
	public void deleteMarkById(Long _id){
		mDb = dbInstance.getWritableDatabase();
		try {
			mDb.delete("myBookMark", "_id = ?", new String[]{_id+""});
		} catch (SQLException e) {
			Log.d("删除出错 ", e.getMessage());
		}finally{
			if(null != bookStatement){
				bookStatement.close();
			}
			if(null != mDb){
				mDb.close();
			}
		}
	}
	/**
	 * 根据主键更新批注内容
	 * @param _id
	 * @param content
	 */
	public void updateContentById(Long _id,String content){
		mDb = dbInstance.getWritableDatabase();
		try {
			ContentValues cv = new ContentValues();
			cv.put("content", content);
			mDb.update("myBookMark", cv, "_id = ?", new String[]{_id+""});
		} catch (SQLException e) {
			Log.d("更新出错 ", e.getMessage());
		}finally{
			if(null != bookStatement){
				bookStatement.close();
			}
			if(null != mDb){
				mDb.close();
			}
		}
	}
}