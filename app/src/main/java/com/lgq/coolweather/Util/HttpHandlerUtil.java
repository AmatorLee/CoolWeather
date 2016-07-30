package com.lgq.coolweather.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.lgq.coolweather.db.City;
import com.lgq.coolweather.db.County;
import com.lgq.coolweather.db.Province;
import com.lgq.coolweather.db.weatherDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DELL1 on 2016/7/29.
 */
public class HttpHandlerUtil {


    /**
     * 处理返回的省级数据u
     */
    public synchronized static boolean handlerProvince(weatherDB weatherDB , String response){
        if (!TextUtils.isEmpty(response)){
            String[] allprovinces = response.split(",");
            if(allprovinces.length > 0 && allprovinces.toString() !=null){
                for (String p : allprovinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    weatherDB.saveProvince(province);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 处理返回的市级数据
     */
    public synchronized  static boolean handlerCity(weatherDB weatherDB ,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allcities = response.split(",");
            if (allcities.length > 0 && allcities.toString() != null){
                for (String c : allcities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    weatherDB.saceCity(city);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 处理返回的县级数据
     */
    public synchronized  static boolean handlerCounty(weatherDB weatherDB ,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allcounties = response.split(",");
            if (allcounties.length > 0 && allcounties.toString() != null){
                for (String c : allcounties){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyName(array[1]);
                    county.setCountyCode(array[0]);
                    weatherDB.saceCounty(county);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 处理返回的weather数据
     */
    public static void handlerJsonResponse(Context context,String response){
        try {
            JSONObject object =  new JSONObject(response);
            JSONObject weatherInfo = object.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 利用sharepreference存入本地
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param publishTime
     */
    private static void saveWeatherInfo(Context context,String cityName, String weatherCode, String temp1
            , String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat fomat = new SimpleDateFormat("yyyy年M月d日", Locale.CANADA);
        SharedPreferences sharedpreferebces = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferebces.edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("teme1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_data",fomat.format(new Date()));
        editor.commit();
    }
}
