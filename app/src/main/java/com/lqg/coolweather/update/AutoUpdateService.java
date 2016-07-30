package com.lqg.coolweather.update;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.lgq.coolweather.Util.HttpHandlerUtil;
import com.lgq.coolweather.Util.HttpListener;
import com.lgq.coolweather.Util.HttpRequestUtil;

public class AutoUpdateService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdateReceive.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        return super.onStartCommand(intent,flags,startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = share.getString("weather_code","");
        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        HttpRequestUtil.sendHttpRequest(address, new HttpListener() {
            @Override
            public void onSucceed(String response) {
                HttpHandlerUtil.handlerJsonResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }
}
