package com.lgq.coolweather.Util;

/**
 * Created by DELL1 on 2016/7/29.
 */
public interface HttpListener {
    public void onSucceed(String response);
    public void onError(Exception e);

}
