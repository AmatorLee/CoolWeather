package com.lgq.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DELL1 on 2016/7/29.
 */
public class DBHelper extends SQLiteOpenHelper{

    private static final String CREATE_PROVINCE = "create table province" +
            "( id integer primary key autoincrement,province_name text" +
            ",province_code text)";
    private static final String CREATE_CITY = "create table city(" +
            "id integer primary key autoincrement , city_name text ," +
            "city_code text ,privince_id integer)";
    private static  final String CREATE_COUNTY = "create table county(" +
            "id integer primary key autoincrement,county_name text ," +
            "county_code text , city_id integer)";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
