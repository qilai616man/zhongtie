package com.digital.dl2.business.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.digital.dl2.business.util.Constant;

import static com.digital.dl2.business.util.Constant.TABLE_EXCEEDTIME;


/**
 * Created by sunbaochun on 15/1/28.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private static DatabaseHelper helper = null;

    public DatabaseHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String table_digital_library =
                "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_DIGITAL_LIBRARY + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "entityId integer," +
                        "frontCover String," +
                        "name String," +
                        "author String," +
                        "introduction String," +
                        "lastReadTime long," +
                        "borrowedTime long," +
                        "downloadUrl String," +
                        "fileSize long," +
                        "localUrl String," +
                        "unzipState int," +
                        "source integer," +
                        "type integer," +
                        "groupName String," +
                        "status integer" +
                        ")";

        String table_group =
                "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_GROUP + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name String" +
                        ")";
        //上海发行分部部分xml图书bookID和可借阅时间
        String table_type =
                "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_TYPE + " (" +
                        "_id  INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "id String,"+
                        "time String," +
                        "data String" +
                        ")";
        //成都无借阅时限用户
        String table_userjson =
                "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_USERJSON + " (" +
                        "_id  INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username String" +
                        ")";
        //2017.11.29  用户可借阅图书时间由本机构管理员设置，默认为试用用户为15天，其他用户为30天
        String table_exceedTime =
                "CREATE TABLE IF NOT EXISTS " + TABLE_EXCEEDTIME + " (" +
                        "_id  INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id integer," +
                        "username String," +
                        "time integer" +
                        ")";


        sqLiteDatabase.execSQL(table_digital_library);
        sqLiteDatabase.execSQL(table_group);
        sqLiteDatabase.execSQL(table_type);
        sqLiteDatabase.execSQL(table_userjson);
        sqLiteDatabase.execSQL(table_exceedTime);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
