package com.lqg.coolweather.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by DELL1 on 2016/7/30.
 */
public class AutoUpdateReceive extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,AutoUpdateService.class);
        context.startService(i);
    }
}
