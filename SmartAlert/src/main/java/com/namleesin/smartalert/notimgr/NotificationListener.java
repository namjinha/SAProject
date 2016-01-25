package com.namleesin.smartalert.notimgr;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.namleesin.smartalert.data.NotiData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.shortcut.PrivacyMode;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService
{
	String[] checkText = { Notification.EXTRA_TEXT, Notification.EXTRA_INFO_TEXT,
						   Notification.EXTRA_BIG_TEXT,
						   Notification.EXTRA_SUB_TEXT, Notification.EXTRA_SUMMARY_TEXT};
	ArrayList<String> mFilterKeyword = new ArrayList<>();
	ArrayList<String> mFilterPkg = new ArrayList<>();

	private void loadFilterKeyword()
	{
		DbHandler handler = new DbHandler(getApplication());
		Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERWORD_INFO, null);
		if(cursor == null)
			return;
		cursor.moveToFirst();
		do {
			String word = cursor.getString(0);
			if(word != null)
			{
				mFilterKeyword.add(word);
			}
		}while(cursor.moveToNext());
	}

	private void loadFilterPkg()
	{
		DbHandler handler = new DbHandler(getApplication());
		Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERPKG_INFO, null);
		if(cursor == null)
			return;

		cursor.moveToFirst();
		do {
			String pkg = cursor.getString(0);
			if(pkg != null)
			{
				mFilterPkg.add(pkg);
			}
		}while(cursor.moveToNext());
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		loadFilterPkg();
		loadFilterKeyword();
		Log.d("NJ LEE", "onCreate");
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) 
	{
	}
	
	public boolean isDislikePkg(String str)
	{
		for(String pkg : mFilterPkg) {
			if (str.equals(pkg)) {
				return true;
			}
		}
		return false;
	}
	
	public String getLikeFilter(String str)
	{
		for(String word : mFilterKeyword)
		{
			if(str.contains(word))
				return word;
		}
		return null;
	}

	private boolean isPrivacyMode()
	{
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		int mode = pref.getInt(PrivacyMode.PREF_PRIVACY_MODE, -1);
		if(mode == PrivacyMode.PRIVACY_MODE_ON)
			return true;
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

		String like = getLikeFilter(notiText);
		if(like!= null)
		{
			notiData.status = DBValue.STATUS_LIKE;
			notiData.filter_word = like;;
		}

		boolean dislike = isDislikePkg(notiData.packagename);
		if(dislike)
		{
			notiData.status = DBValue.STATUS_DISLIKE;
			notiData.filter_word = null;
		}

		notiData.subtxt = notiText;
		notiData.notitime = sbn.getPostTime()+"";
		notiData.urlstatus = 0;

		DbHandler handler = new DbHandler(getApplicationContext());
		int result = handler.insertDB(DBValue.TYPE_INSERT_NOTIINFO, notiData);

		if(isPrivacyMode())
		{
			cancelAllNotifications();
		}
	}
}
