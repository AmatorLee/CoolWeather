package com.lqg.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lgq.coolweather.Util.HttpHandlerUtil;
import com.lgq.coolweather.Util.HttpListener;
import com.lgq.coolweather.Util.HttpRequestUtil;
import com.lqg.coolweather.R;
import com.lqg.coolweather.update.AutoUpdateService;

/**
 * Created by DELL1 on 2016/7/30.
 */
public class ShowActivity extends Activity implements View.OnClickListener {
    /**
     * 初始化各控件
     */
    private LinearLayout weatherInfoLayout;

    /**
     * 显示城市名
     */
    private TextView cityNameText;
    /**
    *显示发布时间
     */
    private TextView pblishText;

    /**
     * 显示天气描述
     */
    private TextView weatherDespText;

    /**
     * 显示此时最低气温
     */
    private TextView temp1Text;

    /**
     * 显示此时最高 气温
     */
    private TextView temp2Text;

    /**
     * 显示当前日期
     */
    private TextView dataText;

    /**
     * 切换城市按钮
     */
    private Button switch_city;

    /**
     * 刷新按钮
     */
    private Button refresh_weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_show);
        //初始化控件
        initView();
    }

    private void initView() {
        weatherInfoLayout  = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        pblishText = (TextView) findViewById(R.id.publish_time);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        dataText = (TextView) findViewById(R.id.current_data);
        switch_city = (Button) findViewById(R.id.switch_city);
        refresh_weather = (Button) findViewById(R.id.refresh_weather);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)){
            pblishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else{
            //如果没有朱姐从本地读取天气信息
            showWeather();
        }

        switch_city.setOnClickListener(this);
        refresh_weather.setOnClickListener(this);
    }

    private void showWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(sharedPreferences.getString("city_name",""));
        temp1Text.setText(sharedPreferences.getString("temp1",""));
        temp2Text.setText(sharedPreferences.getString("temp2",""));
        pblishText.setText("今天"+sharedPreferences.getString("publish_time","")+"发布");
        dataText.setText(sharedPreferences.getString("current_data",""));
        weatherDespText.setText(sharedPreferences.getString("weather_desp",""));
        cityNameText.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        /**
        *开启自动更新服务
        */
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" +
                countyCode+".xml";
        queFromServer(address,"countyCode");
    }

    private void queFromServer(final String address, final String type) {
        HttpRequestUtil.sendHttpRequest(address, new HttpListener() {
            @Override
            public void onSucceed(String response) {
                if ("countyCode".equals(type)){
                    if (!TextUtils.isEmpty(response)){
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if ("weatherCode".equals(type)){
                    HttpHandlerUtil.handlerJsonResponse(ShowActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pblishText.setText("同步失败!");
                        }
                    });
            }
        });
    }

    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo"+weatherCode+".html";
        queFromServer(address,"weatherCode");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this,ChooseActivity.class);
                intent.putExtra("from_show_activity",true);
                startActivity(intent);
                finish();
            break;
            case R.id.refresh_weather:
                pblishText.setText("同步中...");
                SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = share.getString("weather_code","");
                if (!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
        }
    }
}
