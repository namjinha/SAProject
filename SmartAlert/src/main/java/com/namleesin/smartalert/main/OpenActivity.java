package com.namleesin.smartalert.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.namleesin.smartalert.graph.SAGraphActivity;
import com.namleesin.smartalert.guidemgr.GuideMgrActivity;
import com.namleesin.smartalert.settingmgr.AlertSettingActivity;
import com.namleesin.smartalert.settingmgr.NotiSettingActivity;

public class OpenActivity
{
	public static void startSplashScreenActivity(Context aContext)
	{
		Intent i = new Intent(aContext, SplashScreenActivity.class);
		((Activity)aContext).startActivityForResult(i, MainValue.RES_SPLASH_SCREEN);
	}
	
	public static void startGuideMgrActivity(Context aContext)
	{
		Intent i = new Intent(aContext, GuideMgrActivity.class);
		((Activity)aContext).startActivityForResult(i, MainValue.RES_GUIDE_WIZARD);
	}
	
	public static void startAlertSettingActivity(Context aContext)
	{
		Intent i = new Intent(aContext, AlertSettingActivity.class);
		((Activity)aContext).startActivityForResult(i, MainValue.RES_ALERT_SETTING);
	}
	
	public static void startSpamSettingActivity(Context aContext)
	{
		Intent i = new Intent(aContext, NotiSettingActivity.class);
		((Activity)aContext).startActivityForResult(i, MainValue.RES_SPAM_SETTING);
	}

	public static void startGraphActivity(Context aContext)
	{
		Intent i = new Intent(aContext, SAGraphActivity.class);
		((Activity)aContext).startActivity(i);
	}	
}
