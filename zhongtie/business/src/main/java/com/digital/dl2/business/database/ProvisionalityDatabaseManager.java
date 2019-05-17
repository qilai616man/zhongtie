package com.digital.dl2.business.database;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.digital.dl2.business.database.obj.DbBookshelfEntity;
import com.digital.dl2.business.util.Constant;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/23.
 */
public class ProvisionalityDatabaseManager {
    private static ProvisionalityDatabaseManager mDatabaseManager;
    private Context mContext;
    private DatabaseHelper mHelper;
    private SQLiteDatabase mDb;

    public static final ProvisionalityDatabaseManager getInstance() {
        if (mDatabaseManager == null) {
            mDatabaseManager = new ProvisionalityDatabaseManager();
        }

        return mDatabaseManager;
    }

    public void init(Application application) {
        mContext = application;
        mHelper = new DatabaseHelper(mContext);
    }

    /**
     * 批量插入数据库
     *
     * @param list
     */
    public void insertBookList(List<DbBookshelfEntity> list) {
        long t1 = System.currentTimeMillis();

        mDb = mHelper.getWritableDatabase();

        if (list == null || list.isEmpty()) {
            return;
        }

        try {
            mDb.beginTransaction();

            String sql = "INSERT INTO " + Constant.TABLE_DIGITAL_LIBRARY +
                    " (entityId, frontCover, name, author, introduction, " +
                    "lastReadTime, borrowedTime, downloadUrl, fileSize, " +
                    "localUrl, unzipState, source, type, groupName, status) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            SQLiteStatement statement = mDb.compileStatement(sql);

            for (DbBookshelfEntity dbObj : list) {
                statement.bindLong(1, dbObj.getEntityId());
                statement.bindString(2, dbObj.getFrontCover());
                statement.bindString(3, dbObj.getName());
                statement.bindString(4, dbObj.getAuthor());
                statement.bindString(5, dbObj.getIntroduction());
                statement.bindLong(6, dbObj.getLastReadTime());
                statement.bindLong(7, dbObj.getBorrowedTime());
                statement.bindString(8, dbObj.getDownloadUrl());
                statement.bindLong(9, dbObj.getFileSize());
                statement.bindString(10, dbObj.getLocalUrl());
                statement.bindLong(11, dbObj.getSource());
                statement.bindLong(12, dbObj.getType());
                statement.bindString(13, dbObj.getGroup());
                statement.bindLong(14, dbObj.getStatus());
                statement.bindLong(15, dbObj.getUnzipState());

                statement.executeInsert();
                statement.clearBindings();
            }

            mDb.setTransactionSuccessful();
            Ln.e("DatabaseManager:插入数据成功");

        } catch (Exception e) {
            Ln.e("DatabaseManager:插入数据失败：" + e);
        } finally {
            try {
                mDb.endTransaction();
            } catch (Exception e) {
                Ln.e("Exception:" + e);
            }
        }

        Ln.d("DatabaseManager:向数据库中插入数据:共耗时 " + (System.currentTimeMillis() - t1) + " 毫秒");
    }

    /**
     * 插入数据库
     *
     * @param book
     * @return
     */
    public int insertBook(DbBookshelfEntity book) {
        long t1 = System.currentTimeMillis();
        Ln.d("Insert Gson Book:" + new Gson().toJson(book));
        Ln.d("Insert Book.toString:" + book.toString());

        mDb = mHelper.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("entityId", book.getEntityId());
        initialValues.put("frontCover", book.getFrontCover());
        initialValues.put("name", book.getName());
        initialValues.put("author", book.getAuthor());
        initialValues.put("introduction", book.getIntroduction());
        initialValues.put("lastReadTime", book.getLastReadTime());
        initialValues.put("borrowedTime", book.getBorrowedTime());
        initialValues.put("downloadUrl", book.getDownloadUrl());
        initialValues.put("fileSize", book.getFileSize());
        initialValues.put("localUrl", book.getLocalUrl());
        initialValues.put("unzipState", book.getUnzipState());
        initialValues.put("source", book.getSource());
        initialValues.put("type", book.getType());
        initialValues.put("groupName", book.getGroup());
        initialValues.put("status", book.getStatus());

        int id = (int) mDb.insert(Constant.TABLE_DIGITAL_LIBRARY, null, initialValues);

        Ln.d("MonitorModule:向数据库中插入监测人" + (System.currentTimeMillis() - t1) + "毫秒");

        return id;
    }

    /**
     * 删除 图书
     *
     * @param entityId
     */
    public void deleteBook(int entityId) {
        mDb = mHelper.getWritableDatabase();

        String[] id = new String[]{String.valueOf(entityId)};

        mDb.delete(Constant.TABLE_DIGITAL_LIBRARY, "entityId=?", id);
    }

    /**
     * 修改 图书
     *
     * @param book
     */
    public void updateBook(DbBookshelfEntity book) {
        mDb = mHelper.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("entityId", book.getEntityId());
        initialValues.put("frontCover", book.getFrontCover());
        initialValues.put("name", book.getName());
        initialValues.put("author", book.getAuthor());
        initialValues.put("introduction", book.getIntroduction());
        initialValues.put("lastReadTime", book.getLastReadTime());
        initialValues.put("borrowedTime", book.getBorrowedTime());
        initialValues.put("downloadUrl", book.getDownloadUrl());
        initialValues.put("fileSize", book.getFileSize());
        initialValues.put("localUrl", book.getLocalUrl());
        initialValues.put("unzipState", book.getUnzipState());
        initialValues.put("source", book.getSource());
        initialValues.put("type", book.getType());
        initialValues.put("groupName", book.getGroup());
        initialValues.put("status", book.getStatus());

        String[] id = new String[]{String.valueOf(book.getEntityId())};

        mDb.update(Constant.TABLE_DIGITAL_LIBRARY, initialValues, "entityId = ?", id);
    }


    /**
     * 查询所有图书
     *
     * @return
     */
    public List<DbBookshelfEntity> queryAllBook() {
        mDb = mHelper.getWritableDatabase();

        List<DbBookshelfEntity> list = new ArrayList<>();
        String sql = "select * from " + Constant.TABLE_DIGITAL_LIBRARY + " where status != " + Constant.DB_STATUS_DELETE + " order by borrowedTime desc";

        Cursor cursor = mDb.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            DbBookshelfEntity book = new DbBookshelfEntity();

            book.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            book.setEntityId(cursor.getInt(cursor.getColumnIndex("entityId")));
            book.setFrontCover(cursor.getString(cursor.getColumnIndex("frontCover")));
            book.setName(cursor.getString(cursor.getColumnIndex("name")));
            book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            book.setIntroduction(cursor.getString(cursor.getColumnIndex("introduction")));
            book.setLastReadTime(cursor.getLong(cursor.getColumnIndex("lastReadTime")));
            book.setBorrowedTime(cursor.getLong(cursor.getColumnIndex("borrowedTime")));
            book.setDownloadUrl(cursor.getString(cursor.getColumnIndex("downloadUrl")));
            book.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            book.setLocalUrl(cursor.getString(cursor.getColumnIndex("localUrl")));
            book.setUnzipState(cursor.getInt(cursor.getColumnIndex("unzipState")));
            book.setSource(cursor.getInt(cursor.getColumnIndex("source")));
            book.setType(cursor.getInt(cursor.getColumnIndex("type")));
            book.setGroup(cursor.getString(cursor.getColumnIndex("groupName")));
            book.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

            list.add(book);
        }
        cursor.close();

        return list;
    }






    public void closeDb() {
        mDb.close();
    }

    public void clearDb(){
        mDb = mHelper.getWritableDatabase();
        mDb.delete(Constant.TABLE_DIGITAL_LIBRARY,null,null);
        mDb.delete(Constant.TABLE_GROUP,null,null);
    }
}
