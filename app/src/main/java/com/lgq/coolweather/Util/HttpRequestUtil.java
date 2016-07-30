package com.lgq.coolweather.Util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by DELL1 on 2016/7/29.
 */
public class HttpRequestUtil {


    public static void sendHttpRequest(final String address,final HttpListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    reader.close();
                    if (listener != null ){
                        listener.onSucceed(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null)
                        listener.onError(e);
                }finally {
                   connection.disconnect();
                }

            }
        }).start();
    }


}
