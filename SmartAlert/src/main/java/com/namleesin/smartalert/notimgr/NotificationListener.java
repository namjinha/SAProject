package com.namleesin.smartalert.notimgr;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.namleesin.smartalert.data.NotiData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService
{
	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) 
	{
	}
	
	public boolean haveSpamWords(String str)
	{
		
		return false;
	}
	
	public boolean haveHarmUrl(String str)
	{
		return false;
	}
	
	public boolean haveFavorite(String str)
	{
		return false;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onNotificationPosted(StatusBarNotification sbn)
	{
		Notification noti = sbn.getNotification();
		
		NotiData notiData = new NotiData();
		notiData.notiid = sbn.getId()+"";
		notiData.notikey = sbn.getKey();
		notiData.packagename = sbn.getPackageName();

		Log.d("NJ LEE", "notiData.packagename : " + notiData.packagename);
		//Big Text으로 넘겨줬을 경우에 대한 고려도 해야되지 않을까??
		notiData.titletxt = noti.extras.getString(Notification.EXTRA_TITLE);
		notiData.subtxt = noti.extras.getString(Notification.EXTRA_SUB_TEXT);
		notiData.notitime = sbn.getPostTime()+"";
		notiData.dislikestatus = 0;
		notiData.likestatus = 0;
		notiData.urlstatus = 0;
		
		DbHandler handler = new DbHandler(getApplicationContext());
		handler.insertDB(DBValue.TYPE_INSERT_NOTIINFO, notiData);

	}
}
