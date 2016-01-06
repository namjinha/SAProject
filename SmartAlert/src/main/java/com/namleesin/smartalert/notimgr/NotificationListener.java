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
	String[] checkText = { Notification.EXTRA_TEXT, Notification.EXTRA_INFO_TEXT,
						   Notification.EXTRA_BIG_TEXT,
						   Notification.EXTRA_SUB_TEXT, Notification.EXTRA_SUMMARY_TEXT};
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

		notiData.titletxt = noti.extras.getString(Notification.EXTRA_TITLE);

		String notiText = "";
		for(int i = 0; i< checkText.length; i++) {
			CharSequence temp = noti.extras.getCharSequence(checkText[i]);
			if(temp != null) {
				notiText += temp.toString();
				notiText += "\n";
			}
		}

		notiData.subtxt = notiText;
		notiData.notitime = sbn.getPostTime()+"";
		notiData.dislikestatus = 0;
		notiData.likestatus = 0;
		notiData.urlstatus = 0;

		DbHandler handler = new DbHandler(getApplicationContext());
		handler.insertDB(DBValue.TYPE_INSERT_NOTIINFO, notiData);

	}
}
