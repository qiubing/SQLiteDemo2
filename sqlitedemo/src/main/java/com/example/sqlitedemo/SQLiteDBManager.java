package com.example.sqlitedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: qiubing
 * Date: 2017-12-16 19:02
 */
public class SQLiteDBManager {
    private static final String TAG = "SQLiteDemo";
    private SQLiteDBHelper mDBHelper;
    private SQLiteDatabase mDataBase;

    public SQLiteDBManager(Context context){
        mDBHelper = new SQLiteDBHelper(context);
        mDataBase = mDBHelper.getWritableDatabase();
    }

    public void addPersonList(List<Person> persons){
        mDataBase.beginTransaction();
        try {
            for (Person person : persons){
                mDataBase.execSQL("INSERT INTO " + SQLiteDBHelper.TABLE_NAME+ " VALUES(null,?,?,?)",
                        new Object[]{person.name,person.age,person.info});
            }
            mDataBase.setTransactionSuccessful();
        }finally {
            mDataBase.endTransaction();
        }
    }

    public void updatePersonAge(Person person){
        ContentValues value = new ContentValues();
        value.put(SQLiteDBHelper.PERSON_AGE,person.age);
        mDataBase.update(SQLiteDBHelper.TABLE_NAME, value, SQLiteDBHelper.PERSON_NAME + " = ?", new String[]{person.name});
    }

    public List<Person> queryAllPersons(){
        ArrayList<Person> persons = new ArrayList<Person>();
        Cursor cursor = null;
        try {
            cursor = mDataBase.rawQuery("SELECT * FROM " + SQLiteDBHelper.TABLE_NAME,null);
            while (cursor.moveToNext()){
                Person person = new Person();
                person._id = cursor.getInt(cursor.getColumnIndex(SQLiteDBHelper.PERSON_ID));
                person.name = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.PERSON_NAME));
                person.age = cursor.getInt(cursor.getColumnIndex(SQLiteDBHelper.PERSON_AGE));
                person.info = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.PERSON_INFO));
                persons.add(person);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){
                cursor.close();
            }
        }
        return persons;
    }

    public void deleteOldPerson(Person person){
        mDataBase.delete(SQLiteDBHelper.TABLE_NAME,SQLiteDBHelper.PERSON_AGE + " >= ? ",
                new String[]{String.valueOf(person.age)});
    }

    public void closeDB(){
        if (mDataBase != null){
            mDataBase.close();
        }
    }

}
