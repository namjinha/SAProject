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

import com.namleesin.smartalert.data.KeywordData;
import com.namleesin.smartalert.data.NotiData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.shortcut.PrivacyMode;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService
{
	public static String UPDATE_FILTER = "update_filter";
	private String[] checkText = {  Notification.EXTRA_TEXT,
									Notification.EXTRA_INFO_TEXT,
									Notification.EXTRA_BIG_TEXT,
						   			Notification.EXTRA_SUB_TEXT,
									Notification.EXTRA_SUMMARY_TEXT};

	private ArrayList<KeywordData> mFilterKeyword = new ArrayList<>();
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
		if(cursor == null || cursor.getCount() == 0) {
			return;
		}

		cursor.moveToFirst();
		do {
			KeywordData keyword = new KeywordData();
			keyword.setKeywordata(cursor.getString(0));
			keyword.setKeywordstatus(cursor.getInt(1));
			if(keyword != null)
			{
				mFilterKeyword.add(keyword);
			}
		}while(cursor.moveToNext());
	}

	private void loadFilterPkg()
	{
		DbHandler handler = new DbHandler(getApplication());
		mFilterPkg.clear();
		Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERPKG_INFO, null);
		if(cursor == null || cursor.getCount() == 0) {
			Log.d("NJ LEE", "Nothing to load");
			return;
		}

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

		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_FILTER);
		registerReceiver(mFilterUpdateReceiver, filter);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(mFilterUpdateReceiver);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) 
	{
		super.onNotificationRemoved(sbn);
	}
	
	public Object isDislikePkg(String pkg_name, String noti)
	{
		for(String pkg : mFilterPkg) {
			if (pkg_name.equals(pkg)) {
				return true;
			}
		}

		for(KeywordData word : mFilterKeyword)
		{
			if(word.getKeywordstatus() == DBValue.STATUS_DISLIKE &&
					noti.contains(word.getKeywordata()))
				return word.getKeywordata();
		}

		return false;
	}
	
	public String getLikeFilter(String str)
	{
		for(KeywordData word : mFilterKeyword)
		{
			Log.d("NJ LEE", "word : "+word.getKeywordata()+" status : "+word.getKeywordstatus());
			if(word.getKeywordstatus() == DBValue.STATUS_LIKE &&
					str.contains(word.getKeywordata()))
				return word.getKeywordata();
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

		Log.d("NJ LEE", "notiText : "+notiText);
		notiData.subtxt = notiText;
		notiData.notitime = sbn.getPostTime()+"";
		notiData.urlstatus = 0;

		String like = getLikeFilter(notiText);
		if(like!= null)
		{
			notiData.status = DBValue.STATUS_LIKE;
			notiData.filter_word = like;
		}

		Object dislike = isDislikePkg(notiData.packagename, notiText);
		if( dislike instanceof Boolean)
		{
			if((Boolean)dislike == true)
			{
				notiData.status = DBValue.STATUS_DISLIKE;
				notiData.filter_word = null;
				cancelNotification(sbn.getKey());
			}
			else
			{
				notiData.status = DBValue.STATUS_NORMAL;
			}
		}
		else if (dislike instanceof String) {
			notiData.status = DBValue.STATUS_DISLIKE;
			notiData.filter_word = (String) dislike;
			cancelNotification(sbn.getKey());
		}

		DbHandler handler = new DbHandler(getApplicationContext());
		int result = handler.insertDB(DBValue.TYPE_INSERT_NOTIINFO, notiData);

		if(isPrivacyMode())
		{
			//cancelAllNotifications();
		}
	}
}
