package com.lgq.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL1 on 2016/7/29.
 */
public class weatherDB {

    private static final int VERSION = 1;
    private static final String WEATHER = "weather_db";
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private static weatherDB weatherDB;


    private weatherDB(Context context) {

        dbHelper = new DBHelper(context,WEATHER,null, VERSION);
        db = dbHelper.getWritableDatabase();
    }
    /**
     * 暴露数据库实例化方法
     */
    public synchronized static weatherDB getInstance(Context context){
        if(weatherDB == null ){
            weatherDB = new weatherDB(context);
        }
        return weatherDB;
    }


    /**
        添加存储和读取省级列表的数据的方法
     */
    public void saveProvince(Province province){
        if(province != null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("province",null,values);
        }
    }

    public List<Province> loadProvince(){
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("province",null,null,null,null,null,null);
        for (int i = 0; i <cursor.getCount() ; ++i){
            cursor.moveToPosition(i);
            Province province = new Province();
            province.setId(cursor.getInt(cursor.getColumnIndex("id")));
            province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            list.add(province);
        }
        if(cursor != null)
        cursor.close();
        return list;
    }

    /**
     * 添加存储和读取城市列表的数据的方法
     */
    public void saceCity(City city){
        if(city != null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("city",null,values);
        }
    }

    public List<City> loadCity(int provinceId){
        List<City> list = new ArrayList<>() ;
        Cursor cursor = db.query("city",null,"province_id = ?",new String[]{String.valueOf(provinceId)},null,null,null);
        for(int i = 0; i <cursor.getCount() ; ++i){
            cursor.moveToPosition(i);
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            city.setProvinceId(provinceId);
            list.add(city);
        }
        if(cursor != null)
        cursor.close();
        return list;
    }
    /**
     * 添加存储和读取County数据
     */
    public void saceCounty(County county){
        if(county != null){
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("county", null ,values);
        }
    }

    public List<County> loadCounty(int cityId){
        List<County> list = new ArrayList<>();
        Cursor cursor = db.query("county",null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null,null);
        for (int  i = 0 ; i <cursor.getCount() ; ++i){
            cursor.moveToPosition(i);
            County county = new County();
            county.setId(cursor.getInt(cursor.getColumnIndex("id")));
            county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
            county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
            county.setCityId(cityId);
            list.add(county);
        }
        if(cursor != null)
        cursor.close();
        return list;
    }

}
