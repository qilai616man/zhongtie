package com.digital.dl2.business.database;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.digital.dl2.business.database.obj.DbBookshelfEntity;
import com.digital.dl2.business.database.obj.DbGroup;
import com.digital.dl2.business.util.Constant;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/23.
 */
public class DatabaseManager {
    private static DatabaseManager mDatabaseManager;
    private Context mContext;
    private DatabaseHelper mHelper;
    private SQLiteDatabase mDb;

    public static final DatabaseManager getInstance() {
        if (mDatabaseManager == null) {
            mDatabaseManager = new DatabaseManager();
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
     2017.11.29 ymd
     用户可借阅图书时间限制(管理员设定)。插入数据库type
     */
    public void insertExceedTime(int user_id,String username,int time) {
        long t1 = System.currentTimeMillis();
//        Toast.makeText(mContext,"size=="+jsobs.size()+"==time=="+time+"mHelper=="+mHelper,Toast.LENGTH_SHORT).show();
        mDb = mHelper.getWritableDatabase();
            ContentValues initialValues = new ContentValues();
            initialValues.put("user_id" , user_id);
            initialValues.put("username", username);
            initialValues.put("time",time);
            Ln.d("MonitorModule:向数据库中插入监测人" + (System.currentTimeMillis() - t1) + "毫秒");
            int id = (int) mDb.insert(Constant.TABLE_EXCEEDTIME, null, initialValues);

//            Toast.makeText(mContext,"id=="+id,Toast.LENGTH_SHORT).show();
    }
    public int queryExceedTime(String username) {
        Log.i("name==========1111",username);
        mDb = mHelper.getWritableDatabase();
        int time = -1;
        String sqlStr = "select * from " + Constant.TABLE_EXCEEDTIME; //+" where username = '" + username + "'"; //" + user_id +" and
        Cursor cursor = mDb.rawQuery(sqlStr,null);

//        Cursor cursor=mDb.query(Constant.TABLE_EXCEEDTIME,new String[]{"id","username","time"},"username=?",new String[]{username},null,null,null);
        if (cursor.moveToNext()) {
            time=cursor.getInt(cursor.getColumnIndex("time"));
            String name=cursor.getString(cursor.getColumnIndex("username"));
            Log.i("name==========",name+"=="+ cursor.getCount());
//            Toast.makeText(mContext, "name=========="+name+"=="+ cursor.getCount(), Toast.LENGTH_SHORT).show();
        }
        while (cursor.moveToNext()){
            time=cursor.getInt(cursor.getColumnIndex("time"));
            String name=cursor.getString(cursor.getColumnIndex("username"));
            if(name.equals(username)){
                return time;
            }
        }
        cursor.close();
        return time;
    }
    public void updateExceedTime(int user_id,String username,int time) {
        mDb = mHelper.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("time",time);
        initialValues.put("user_id",user_id);
        initialValues.put("username",username);
        try {
            String sqlStr = "select * from " + Constant.TABLE_EXCEEDTIME ;;//" where username = " + username;//user_id = " + user_id +" and
            Cursor cursor = mDb.rawQuery(sqlStr, null);
            if(!cursor.moveToNext()){
                insertExceedTime(user_id,username,time);
//                Toast.makeText(mContext, "time==" + time, Toast.LENGTH_SHORT).show();
            }
            while (cursor.moveToNext()){
                String name=cursor.getString(cursor.getColumnIndex("username"));
                if(name.equals(username)){
                    int oldtime = cursor.getInt(cursor.getColumnIndex("time"));
                    if (oldtime != time) {
                       int updataCount= mDb.update(Constant.TABLE_EXCEEDTIME, initialValues, "username=?", new String[]{username});
//                        Toast.makeText(mContext, "oldtime==+oldtime" + oldtime+"updataCount=="+updataCount, Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
            }
            int res= (int) mDb.insert(Constant.TABLE_EXCEEDTIME,null,initialValues);
//            Toast.makeText(mContext,"res=="+res,Toast.LENGTH_SHORT).show();
            cursor.close();
        }catch(Exception e){
//            Toast.makeText(mContext,"insert"+e.getMessage(),Toast.LENGTH_SHORT).show();
            int res= (int) mDb.insert(Constant.TABLE_EXCEEDTIME,null,initialValues);
//            Toast.makeText(mContext,"res=="+res,Toast.LENGTH_SHORT).show();
        }




    }
    /**
     * ymd 成都20用户借阅图书无时限
     * @param userjsons
     */
    public void insertUserJson(List<String> userjsons) {
        long t1 = System.currentTimeMillis();
//        Toast.makeText(mContext,"size=="+jsobs.size()+"==time=="+time+"mHelper=="+mHelper,Toast.LENGTH_SHORT).show();
        mDb = mHelper.getWritableDatabase();
        for(String userjson :userjsons){
            ContentValues initialValues = new ContentValues();
            initialValues.put("username", userjson);
            Ln.d("MonitorModule:向数据库中插入监测人" + (System.currentTimeMillis() - t1) + "毫秒");
            int id = (int) mDb.insert(Constant.TABLE_USERJSON, null, initialValues);

//            Toast.makeText(mContext,"id=="+id,Toast.LENGTH_SHORT).show();
        }
    }
    public boolean queryuserjson(String username) {
        mDb = mHelper.getWritableDatabase();
        String sqlStr = "select * from " + Constant.TABLE_USERJSON + " where username = "+username ;
        Cursor cursor = mDb.rawQuery(sqlStr, null);
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
//    public String queryuser(){
//        mDb = mHelper.getWritableDatabase();
//        String sql = "select _id from" + Constant.TABLE_USERJSON;
//        Cursor cursor = mDb.rawQuery(sql, null);
//        String user = "";
//        if (cursor.moveToNext()) {
//            user = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
//            cursor.close();
//            return user;
//        }
//        cursor.close();
//        return user;
//    }

    /**
     ymd
     上海发行分部 部分xml图书借阅时间改为30分钟。插入数据库type
     */
    public void insertBookType(int time,List<String> jsobs) {
        long t1 = System.currentTimeMillis();
//        Toast.makeText(mContext,"size=="+jsobs.size()+"==time=="+time+"mHelper=="+mHelper,Toast.LENGTH_SHORT).show();
        mDb = mHelper.getWritableDatabase();
        for(String jsob :jsobs){
            ContentValues initialValues = new ContentValues();
            initialValues.put("id", jsob);
            initialValues.put("data", "1");
            initialValues.put("time",time);
            Ln.d("MonitorModule:向数据库中插入监测人" + (System.currentTimeMillis() - t1) + "毫秒");
            int id = (int) mDb.insert(Constant.TABLE_TYPE, null, initialValues);

//            Toast.makeText(mContext,"id=="+id,Toast.LENGTH_SHORT).show();
        }
    }
    public void deletejsob() {
        mDb = mHelper.getWritableDatabase();

        int b = mDb.delete(Constant.TABLE_TYPE,null,null);
//        Toast.makeText(mContext,"int b=="+b,Toast.LENGTH_SHORT).show();
    }
    public int queryjsobById(String id) {
        Ln.d("queryBookById:id:" + id);
        mDb = mHelper.getWritableDatabase();
        Integer time = 0;
        String sqlStr = "select * from " + Constant.TABLE_TYPE + " where id = " + id;
        Cursor cursor = mDb.rawQuery(sqlStr, null);
        if (cursor.moveToNext()) {
            time = Integer.valueOf(cursor.getInt(cursor.getColumnIndex("time")));
            cursor.close();
            return time;
        }
        cursor.close();
        return time;
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

//    /**
//     * 根据 entityId 修改 数据状态
//     * @param entityId
//     * @param status
//     */
//    public void updateBookByEntityId(int entityId,int status){
//        mDb = mHelper.getWritableDatabase();
//
//        ContentValues initialValues = new ContentValues();
//        initialValues.put("status",status);
//
//        mDb.update(Constant.TABLE_DIGITAL_LIBRARY, initialValues, "entityId = ?", new String[]{String.valueOf(entityId)});
//    }

//    /**
//     * 根据 id 修改图书所属分组信息
//     * @param dbBook
//     */
//    public void updateBookByGroup(DbBookshelfEntity dbBook){
//        mDb = mHelper.getWritableDatabase();
//
//        ContentValues initialValues = new ContentValues();
////        initialValues.put("groupName",group);
//
//
//
////        "entityId integer," +
////                "frontCover String," +
////                "name String," +
////                "author String," +
////                "introduction String," +
////                "lastReadTime long," +
////                "borrowedTime long," +
////                "downloadUrl String," +
////                "fileSize long," +
////                "localUrl String," +
////                "source integer," +
////                "type integer," +
////                "groupName String," +
////                "status integer" +
//
//        mDb.update(Constant.TABLE_DIGITAL_LIBRARY, initialValues, "entityId = ?", new String[]{String.valueOf(dbBook.getEntityId())});
//    }

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

    /**
     * 根据 type 查询图书
     * @return
     */
    public List<DbBookshelfEntity> queryBookByType(int type) {
        mDb = mHelper.getWritableDatabase();

        List<DbBookshelfEntity> list = new ArrayList<>();

        String sql = "select * from " + Constant.TABLE_DIGITAL_LIBRARY + " where type = " + type + " and status != " + Constant.DB_STATUS_DELETE + " order by borrowedTime desc";

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

    /**
     * 根据 group 查询图书
     *
     * @return
     */
    public List<DbBookshelfEntity> queryBookByGroup(String group) {
        mDb = mHelper.getWritableDatabase();

        List<DbBookshelfEntity> list = new ArrayList<>();

        String sql = "select * from " + Constant.TABLE_DIGITAL_LIBRARY + " where groupName = '" + group + "' and status != " + Constant.DB_STATUS_DELETE;

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

    /**
     * 根据 Id 查询 DbBookshelfEntity
     *
     * @param id
     * @return
     */
    public DbBookshelfEntity queryBookById(int id) {
        Ln.d("queryBookById:id:" + id);
        mDb = mHelper.getWritableDatabase();

        String sqlStr = "select * from " + Constant.TABLE_DIGITAL_LIBRARY + " where entityId = " + id + " and status != " + Constant.DB_STATUS_DELETE;
        Cursor cursor = mDb.rawQuery(sqlStr, null);

        if (cursor.moveToNext()) {
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

            cursor.close();
            return book;
        }

        cursor.close();

        return null;
    }

    /**
     * 根据 entityId 查询 DbBookshelfEntity
     *
     * @param entityId
     * @return
     */
    public DbBookshelfEntity queryBookByEntityId(int entityId) {
        Ln.d("queryBookById:id:" + entityId);
        mDb = mHelper.getWritableDatabase();

        String sqlStr = "select * from " + Constant.TABLE_DIGITAL_LIBRARY + " where entityId = " + entityId;
        Cursor cursor = mDb.rawQuery(sqlStr, null);

        DbBookshelfEntity book = new DbBookshelfEntity();

        if (cursor.moveToNext()) {
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
        }
        cursor.close();

        return book;
    }

    /**
     * 根据 source 查询 List<DbBookshelfEntity>
     *
     * @param source
     * @return
     */
    public List<DbBookshelfEntity> queryBookListBySource(int source) {
        Ln.d("queryBookListBySource:source:" + source);
        mDb = mHelper.getWritableDatabase();

        List<DbBookshelfEntity> list = new ArrayList<>();
        String sql = "select * from " + Constant.TABLE_DIGITAL_LIBRARY + " where source = " + source + " and status != " + Constant.DB_STATUS_DELETE + " order by borrowedTime desc";

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

    /**
     * 插入 组 信息
     *
     * @param name
     * @return
     */
    public int insertGroup(String name) {
        long t1 = System.currentTimeMillis();

        mDb = mHelper.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("name", name);

        int id = (int) mDb.insert(Constant.TABLE_GROUP, null, initialValues);

        Ln.d("DatabaseManager:向数据库中插入组信息：耗时：" + (System.currentTimeMillis() - t1) + "毫秒");

        return id;
    }

    /**
     * 删除 分组
     *
     * @param groupId
     */
    public void deleteGroup(int groupId) {
        Ln.d("DatabaseManager:删除组：id:" + groupId);
        mDb = mHelper.getWritableDatabase();

        String[] id = new String[]{String.valueOf(groupId)};

        Ln.d("DatabaseManager:删除组：id:" + Arrays.toString(id));

        int i = mDb.delete(Constant.TABLE_GROUP, "_id=?", id);
        Ln.d("DatabaseManager:删除组：i:" + i);
    }

    /**
     * 修改 分组
     *
     * @param groupId
     * @param name
     */
    public void updateGroup(int groupId, String name) {
        mDb = mHelper.getWritableDatabase();

        ContentValues initialValues = new ContentValues();
        initialValues.put("name", name);

        String[] id = new String[]{String.valueOf(groupId)};

        mDb.update(Constant.TABLE_GROUP, initialValues, "_id = ?", id);
    }

    /**
     * 查询所有分组
     *
     * @return
     */
    public List<DbGroup> queryAllGroup() {
        mDb = mHelper.getWritableDatabase();

        List<DbGroup> list = new ArrayList<>();
        String sql = "select * from " + Constant.TABLE_GROUP;

        Cursor cursor = mDb.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            DbGroup group = new DbGroup();

            group.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            group.setName(cursor.getString(cursor.getColumnIndex("name")));

            list.add(group);
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
