package com.artifex.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pdf.db";
    private static final int DATABASE_VERSION = 4;
    private static DBHelper dbInstance;
    private static final String TABLE_PDF_MARK = "pdf_mark";
    private static final String CREATE_TABLE_PDF_MARK = 
            "CREATE TABLE " + TABLE_PDF_MARK + " ("
            		+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            		+ "mark_describe CHAR(255),"
            		+ "mark_index INTEGER,"
            		+ "modified INTEGER"
            		+ ");";
    
    static DBHelper getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DBHelper(context);
        }
        return dbInstance;
    }
    
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PDF_MARK);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_PDF_MARK);
        onCreate(db);
    }
}