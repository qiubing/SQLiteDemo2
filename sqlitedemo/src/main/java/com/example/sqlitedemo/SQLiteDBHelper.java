package com.example.sqlitedemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Description:
 * Author: qiubing
 * Date: 2017-12-16 19:04
 */
public class SQLiteDBHelper extends SQLiteOpenHelper{
    private static final String TAG = "SQLiteDemo";

    private static final String DB_NAME = "test.db";
    private static final int DB_VERSION = 2;
    public static final String TABLE_NAME = "person";
    public static final String PERSON_ID = "_id";
    public static final String PERSON_NAME = "name";
    public static final String PERSON_AGE = "age";
    public static final String PERSON_INFO = "info";
    public static final String PERSON_OTHER = "other";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + "("
            + PERSON_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PERSON_NAME
            + " VARCHAR, "
            + PERSON_AGE
            + " INTEGER, "
            + PERSON_INFO
            + " TEXT)";

    private static final String UPGRADE_TABLE = "ALTER TABLE "
            + TABLE_NAME
            + " ADD COLUMN "
            + PERSON_OTHER
            + " STRING";

    public SQLiteDBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG,"onCreate()...CREATE_TABLE = " + CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG,"onUpgrade()...oldVersion = " + oldVersion + " newVersion = " + newVersion + " upgrade_table = " + UPGRADE_TABLE);
        db.execSQL(UPGRADE_TABLE);
    }
}
