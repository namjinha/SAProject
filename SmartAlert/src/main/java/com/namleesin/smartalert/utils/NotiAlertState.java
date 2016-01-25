package com.namleesin.smartalert.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.namleesin.smartalert.notimgr.NotificationListener;

/**
 * Created by namjinha on 2016-01-23.
 */
public class NotiAlertState
{
    public static boolean isNLServiceRunning(Context aContext)
    {
        ActivityManager manager = (ActivityManager) aContext.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (NotificationListener.class.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }

        return false;
    }
}
