package com.namleesin.smartalert.notimgr;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
	public static String UPDATE_FILTER = "update_filter";
	private String[] checkText = { Notification.EXTRA_TEXT, Notification.EXTRA_INFO_TEXT,
						   Notification.EXTRA_BIG_TEXT,
						   Notification.EXTRA_SUB_TEXT, Notification.EXTRA_SUMMARY_TEXT};
	private ArrayList<String> mFilterKeyword = new ArrayList<>();
	private ArrayList<String> mFilterPkg = new ArrayList<>();

	private BroadcastReceiver mFilterUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d("NJ LEE", "action : "+action);
			if(action.equals(UPDATE_FILTER))
			{
				loadFilterPkg();
				loadFilterKeyword();
			}
		}
	};

	private void loadFilterKeyword()
	{
		DbHandler handler = new DbHandler(getApplication());
		mFilterKeyword.clear();
		Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERWORD_INFO, null);
		Log.d("NJ LEE", "cursor : "+cursor);
		if(cursor == null || cursor.getCount() == 0)
			return;
		cursor.moveToFirst();
		do {
			String word = cursor.getString(0);
			if(word != null)
			{
				mFilterKeyword.add(word);
				Log.d("NJ LEE", "word : " + word);
			}
		}while(cursor.moveToNext());
	}

	private void loadFilterPkg()
	{
		DbHandler handler = new DbHandler(getApplication());
		mFilterPkg.clear();
		Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERPKG_INFO, null);
		if(cursor == null || cursor.getCount() == 0)
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

		Log.d("NJ LEE", "NotificationListener oncreate");
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_FILTER);
		registerReceiver(mFilterUpdateReceiver, filter);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d("NJ LEE", "onNotificationRemoved");
		unregisterReceiver(mFilterUpdateReceiver);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) 
	{
		super.onNotificationRemoved(sbn);
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
			notiData.filter_word = like;
		}

		boolean dislike = isDislikePkg(notiData.packagename);
		if(dislike)
		{
			notiData.status = DBValue.STATUS_DISLIKE;
			notiData.filter_word = null;
		}

		Log.d("NJ LEE", "notiText : "+notiText);
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
