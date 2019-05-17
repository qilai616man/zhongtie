package com.digital.dl2.business.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.digital.dl2.business.util.Constant;

public class ProvisionalityDatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    //数据库名
//    private static final String DB_NAME="newsFavoriteTitle7";
    private static final String DB_NAME= "privisionality_digital_library.db";
    //数据表名
//    public static final String TBL_NAME="person7";
    public static final String TBL_NAME= "privisionality_digital_library";
    //创建表的语句
    private static final  String CRETAE_TBL = "create table privisionality_digital_library(_id integer primary key autoincrement,bookEntityId char(10))";
    public ProvisionalityDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(CRETAE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TBL_NAME, null, values);
        db.close();
    }

    public Cursor query(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TBL_NAME, null, null, null, null, null, null);
        return cursor;
    }

    public void del(int id){
        if(db == null){
            db = getWritableDatabase();
            db.delete(TBL_NAME, "bookEntityId=?", new String[]{String.valueOf(id)});
        }
    }

    public void close(){
        if(db != null){
            db.close();
        }
    }
}
