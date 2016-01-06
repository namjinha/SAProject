package com.namleesin.smartalert.timeline;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;

import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.main.NotiInfoData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimelineDataLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<TimelineData>>
{
	private PackageManager mPkgMgr;
	private DbHandler mDbHandler;
	private int mQueryType = 0;
	private String mParam;

	private ArrayList<TimelineData> noti_data_list = null;

	public TimelineDataLoader(Context context, int queryType, String param)
	{
		super(context);
		mPkgMgr = context.getPackageManager();
		mDbHandler = new DbHandler(context);
		mQueryType = queryType;
		mParam = param;
	}

	@Override
	public ArrayList<TimelineData> loadInBackground()
	{
		noti_data_list = new ArrayList<TimelineData>();
		Log.d("NJ LEE", "loadInBackground : ");
		Cursor cursor = mDbHandler.selectDBData(mQueryType, mParam);
		cursor.moveToFirst();
		do
		{
			String pkgName = cursor.getString(DBValue.CULUM_PACKAGE);
			TimelineData data = null;
			try {
				Log.d("NJ LEE", "pkgName : " + pkgName);
				data = new TimelineData()
                        .setPkgName(pkgName)
                        .setContent(cursor.getString(DBValue.CULUM_SUBTXT))
                        .setAppName((String) mPkgMgr.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES).loadLabel(mPkgMgr))
                        .setLikeStatus(cursor.getInt(DBValue.CULUM_LIKESTATUS));
				noti_data_list.add(data);
			}
			catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}while(cursor.moveToNext());

		Log.d("NJ LEE", "size : "+noti_data_list.size());
		return noti_data_list;
	}

	@Override protected void onStartLoading() {
		if (noti_data_list != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(noti_data_list);
		}
	}

	@Override public void deliverResult(ArrayList<TimelineData> datas) {

		super.deliverResult(datas);
	}
}
